//Real-Time Ticketing System Backend by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.
package com.hkrw2082289.ticketing_system.service;
import com.hkrw2082289.ticketing_system.model.TicketEntity;
import com.hkrw2082289.ticketing_system.repository.TicketRepository;
import com.hkrw2082289.ticketing_system.utils.TicketUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This service class is used to manage the shared ticket pool, including adding and removing tickets,
 * and handling consumer and vendor interactions with the ticket pool in a thread-safe manner.
 */
@Service
@Scope("singleton")
public class TicketPoolService {

    /**
     * This is a thread-safe queue to hold tickets of the system.
     */
    private final ConcurrentLinkedQueue<TicketEntity> tickets = new ConcurrentLinkedQueue<>();

    /**
     * This Lock is for synchronizing ticket-related operations of this Real-time ticketing system.
     */
    private final Lock ticketLock = new ReentrantLock();

    /**
     * This a condition to signal vendors when tickets can be added again in a situation where the ticket pool is fool.
     */
    private final Condition vendorCondition = ticketLock.newCondition();

    /**
     * This is a Map to manage conditions for consumers based on event names when certain tickets are out of stock.
     */
    private final Map<String, Condition> consumerConditions = new HashMap<>();

    /**
     * This is a Map to track usage counts of events for condition cleanup.
     */
    private final Map<String, Integer> eventUsageCount = new HashMap<>();

    /**
     * This is the Repository interface used to interact with the ticketpool database table.
     */
    private final TicketRepository ticketRepository;

    /**
     * This is a service class used to fetch configuration details needed for ticket operations.
     */
    private final ConfigurationService configurationService;

    /**
     * This Utility class is used to keep track of "Available" ticket count to make sure max ticket capacity does
     * not exceed its limit.
     */
    private final TicketUtility ticketUtility;

    /**
     * This is the Messaging template for sending updates to the frontend to update the user with latest updates.
     */
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * This Logger is for logging operations in this service class.
     */
    private static final Logger logger = LoggerFactory.getLogger(TicketPoolService.class);

    /**
     * This constructor constructs a TicketPoolService with necessary dependencies and initializes the
     * ticket pool from the database.
     *
     * @param ticketRepository the repository for ticket data to be stored and retrieved.
     * @param configurationService the service for configuration management of the system.
     * @param ticketUtility the utility class for ticket operations like finding the total tickets available in the
     *                     system which are not booked.
     * @param messagingTemplate the messaging template for frontend communication.
     */
    @Autowired
    public TicketPoolService(TicketRepository ticketRepository,
                             ConfigurationService configurationService,
                             TicketUtility ticketUtility,
                             SimpMessagingTemplate messagingTemplate) {
        this.ticketRepository = ticketRepository;
        this.configurationService = configurationService;
        this.ticketUtility = ticketUtility;
        this.messagingTemplate = messagingTemplate;
        loadTicketsFromDatabase();
    }

    /**
     * This method sends a log message to the frontend via WebSocket, including the current date and time.
     *
     * @param message the message to be sent.
     */
    private void sendLogMessage(String message) {
        // Get the current date and time.
        LocalDateTime now = LocalDateTime.now();
        // Format the date and time.
        String formattedDateTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        // Message with date and time added.
        String messageWithTimestamp = "[" + formattedDateTime + "] " + message;
        messagingTemplate.convertAndSend("/topic/logs", messageWithTimestamp);
    }

    /**
     * This method retrieves the current maximum capacity of the ticket pool from configuration class.
     *
     * @return the maximum ticket capacity the system must maintain.
     */
    private int getCurrentMaxCapacity() {
        return configurationService.viewConfiguration().getMaxTicketCapacity();
    }

    /**
     * This method loads tickets from the database into the pool during initialization.
     */
    private void loadTicketsFromDatabase() {
        ticketLock.lock();
        try {
            List<TicketEntity> dbTickets = ticketRepository.findAll();
            tickets.addAll(dbTickets);
        } finally {
            ticketLock.unlock();
        }
    }

    /**
     * This method is used to add a ticket to the shared pool in a thread-safe manner, waiting if the shared ticket
     * pool is full. When a ticket is added to the ticket pool it signals consumer waiting for the specific event so
     * they can resume purchasing of tickets.
     *
     * @param ticket the ticket to be added.
     * @return {@code true} if the ticket was added successfully, {@code false} otherwise.
     */
    public boolean addTicket(TicketEntity ticket) {
        ticketLock.lock();
        try {
            while (countAvailableTickets() >= getCurrentMaxCapacity()) {
                sendLogMessage("Waiting to add ticket, pool is full..."+ Thread.currentThread().getId());
                logger.info("Waiting to add ticket, pool is full...");
                vendorCondition.await();
            }

            TicketEntity savedTicket = ticketRepository.save(ticket);
            tickets.add(savedTicket);

            messagingTemplate.convertAndSend("/topic/ticketpool", tickets);
            sendLogMessage("Added ticket for event: " + ticket.getEventName()+" with ID: "+ticket.getTicketId());

            logger.info("Added ticket for event: {}", ticket.getEventName());
            notifyConsumersForEvent(ticket.getEventName());
            logger.info("Thread {} notified consumers for event: {}",
                    Thread.currentThread().getId(), ticket.getEventName());
            cleanupUnusedConditions();
            return true;
        } catch (InterruptedException e) {
            sendLogMessage("Thread " + Thread.currentThread().getId() +" for vendor: "+ ticket.getVendorId() +
                    " interrupted while adding ticket");
            logger.error("Thread {} interrupted while adding ticket", Thread.currentThread().getId());
            Thread.currentThread().interrupt();
            return false;
        } finally {
            ticketLock.unlock();
        }
    }

    /**
     * This method books a ticket for a specified event for a customer in a thread-safe manner,
     * waiting if no tickets are available. When a ticket is booked it will signal all vendors to resume adding tickets.
     *
     * @param eventName the name of the event.
     * @param customerId the ID of the customer.
     * @return an array containing the success status and ticket ID, if booked
     */
    public Object[] removeTicket(String eventName, String customerId) {
        ticketLock.lock();
        try {
            while (!isTicketAvailable(eventName)) {
                sendLogMessage("Thread " + Thread.currentThread().getId() + " waiting for " +
                        "tickets to become available for event: " + eventName);
                logger.info("Thread {} waiting for tickets to become available for event: {}",
                        Thread.currentThread().getId(), eventName);
                waitForSpecificEventTicket(eventName);
            }
            TicketEntity ticket = findAvailableTicket(eventName);
            if (ticket != null) {
                ticket.setTicketStatus("Booked");
                ticket.setCustomerId(customerId);

                TicketEntity updatedTicket = ticketRepository.save(ticket);

                messagingTemplate.convertAndSend("/topic/ticketpool", tickets);
                sendLogMessage("Thread " + Thread.currentThread().getId() + " booked ticket " +
                        updatedTicket.getTicketId() + " for event: " + eventName + " by customer: " + customerId);

                logger.info("Thread {} booked ticket {} for event: {} by customer: {}",
                        Thread.currentThread().getId(),  updatedTicket.getTicketId(), eventName, customerId);
                vendorCondition.signalAll();
                logger.info("Thread {} signaled vendors for more capacity", Thread.currentThread().getId());
                cleanupUnusedConditions();
                return new Object[]{true, ticket.getTicketId()};
            }
            sendLogMessage("Thread " + Thread.currentThread().getId() + " found no tickets available for event: "
                    + eventName);
            logger.info("Thread {} found no tickets available for event: {}",
                    Thread.currentThread().getId(), eventName);
            return new Object[]{false, null};
        } catch (InterruptedException e) {
            sendLogMessage("Thread " + Thread.currentThread().getId() + " interrupted while booking ticket");
            logger.error("Thread {} interrupted while booking ticket", Thread.currentThread().getId());
            Thread.currentThread().interrupt();
            return new Object[]{false, null};
        } finally {
            ticketLock.unlock();
        }
    }

    /**
     * This method checks if a ticket is available for a specific event in a thread-safe manner.
     *
     * @param eventName the name of the event.
     * @return {@code true} if a ticket is available, {@code false} otherwise.
     */
    private boolean isTicketAvailable(String eventName) {
        ticketLock.lock();
        try {
            return tickets.stream().anyMatch(
                    t -> "Available".equals(t.getTicketStatus()) && eventName.equals(t.getEventName()));
        } finally {
            ticketLock.unlock();
        }
    }

    /**
     * This method counts the number of tickets with ticket status set to "Available" in the pool.
     *
     * @return the count of available tickets.
     */
    public int countAvailableTickets() {
        ticketLock.lock();
        try {
            return (int) ticketUtility.countAvailableTickets();
        } finally {
            ticketLock.unlock();
        }
    }

    /**
     * This method finds an available ticket for a specific event.
     *
     * @param eventName the name of the event.
     * @return the available ticket entity, or {@code null} if none found.
     */
    private TicketEntity findAvailableTicket(String eventName) {
        ticketLock.lock();
        try {
            return tickets.stream()
                    .filter(t -> "Available".equals(t.getTicketStatus()) && eventName.equals(t.getEventName()))
                    .findFirst()
                    .orElse(null);
        } finally {
            ticketLock.unlock();
        }
    }

    /**
     * This method makes threads to wait for a specific event ticket to become available if there are out of stock.
     *
     * @param eventName the name of the event.
     * @throws InterruptedException if the thread is interrupted while waiting.
     */
    private void waitForSpecificEventTicket(String eventName) throws InterruptedException {
        ticketLock.lock();
        try {
            consumerConditions.putIfAbsent(eventName, ticketLock.newCondition());
            eventUsageCount.put(eventName, eventUsageCount.getOrDefault(eventName, 0) + 1);
            consumerConditions.get(eventName).await();
        } finally {
            ticketLock.unlock();
        }
    }

    /**
     * This method notifies consumers threads waiting for a specific event ticket, to wake them up.
     *
     * @param eventName the name of the event.
     */
    private void notifyConsumersForEvent(String eventName) {
        ticketLock.lock();
        try {
            Condition condition = consumerConditions.get(eventName);
            if (condition != null) {
                condition.signalAll();
            }
            Integer usageCount = eventUsageCount.get(eventName);
            if (usageCount != null && usageCount <= 0) {
                consumerConditions.remove(eventName);
                eventUsageCount.remove(eventName);
            }
        } finally {
            ticketLock.unlock();
        }
    }

    /**
     * This method cleans up unused conditions for events no longer in use.
     */
    private void cleanupUnusedConditions() {
        ticketLock.lock();
        try {
            Iterator<Map.Entry<String, Integer>> iterator = eventUsageCount.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Integer> entry = iterator.next();
                if (entry.getValue() <= 0) {
                    consumerConditions.remove(entry.getKey());
                    iterator.remove();
                }
            }
        } finally {
            ticketLock.unlock();
        }
    }

    /**
     * This method retrieves a map of available ticket counts grouped by event for a specific vendor.
     *
     * @param vendorId the ID of the vendor.
     * @return a map of event names to available ticket counts for specific vendor ID.
     */
    public Map<String, Integer> viewAvailableTicketCountsByVendor(String vendorId) {
        ticketLock.lock();
        try {
            return tickets.stream()
                    .filter(ticket -> vendorId.equals(ticket.getVendorId()) &&
                            "Available".equals(ticket.getTicketStatus()))
                    .collect(Collectors.groupingBy(TicketEntity::getEventName, Collectors.summingInt(ticket -> 1)));
        } finally {
            ticketLock.unlock();
        }
    }

    /**
     * This method counts the available tickets grouped by event.
     *
     * @return a map of event names to available ticket counts.
     */
    public Map<String, Integer> countAvailableTicketsByEvent() {
        ticketLock.lock();
        try {
            return tickets.stream()
                    .filter(ticket -> "Available".equals(ticket.getTicketStatus()))
                    .collect(Collectors.groupingBy(TicketEntity::getEventName, Collectors.summingInt(ticket -> 1)));
        } finally {
            ticketLock.unlock();
        }
    }

    /**
     * This method counts the booked tickets grouped by event.
     *
     * @return a map of event names to booked ticket counts.
     */
    public Map<String, Integer> countBookedTicketsByEvent() {
        ticketLock.lock();
        try {
            return tickets.stream()
                    .filter(ticket -> "Booked".equals(ticket.getTicketStatus()))
                    .collect(Collectors.groupingBy(TicketEntity::getEventName, Collectors.summingInt(ticket -> 1)));
        } finally {
            ticketLock.unlock();
        }
    }

    /**
     * This method counts the booked tickets for a specific customer grouped by event.
     *
     * @param customerId the ID of the customer.
     * @return a map of event names to booked ticket counts for specific customer ID.
     */
    public Map<String, Long> countBookedTicketsByCustomerId(String customerId) {
        ticketLock.lock();
        try {
            return tickets.stream()
                    .filter(ticket -> "Booked".equals(ticket.getTicketStatus()) &&
                            customerId.equals(ticket.getCustomerId()))
                    .collect(Collectors.groupingBy(TicketEntity::getEventName, Collectors.counting()));
        } finally {
            ticketLock.unlock();
        }
    }
}

