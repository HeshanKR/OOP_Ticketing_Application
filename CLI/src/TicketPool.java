//Real-Time Ticketing System CLI by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * This TicketPool class represents a thread-safe singleton pool of tickets
 * that provides functionalities for managing tickets for various events.
 * It allows vendors to add tickets and consumers to book tickets for specific events,
 * ensuring thread safety through locks and conditions.
 */
public class TicketPool {
    private static TicketPool instance;  // Singleton instance of the shared ticket pool.
    //The queue holding the tickets in the pool.
    private final ConcurrentLinkedQueue<Ticket> tickets = new ConcurrentLinkedQueue<>();
    private long maxCapacity; //he maximum capacity of the ticket pool.
    private static final String TICKETS_FILE = "Tickets.json"; //The file path for persisting tickets.
    //The Gson instance for JSON serialization and deserialization.
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // The lock for managing access to the ticket pool.
    private final Lock ticketLock = new ReentrantLock();
    // Condition for vendors to wait when the pool is full.
    private final Condition vendorCondition = ticketLock.newCondition();
    // Conditions for consumers to wait for specific events.
    private final Map<String, Condition> consumerConditions = new HashMap<>();
    // A map to track the usage count of each event condition.
    private final Map<String, Integer> eventUsageCount = new HashMap<>();

    /**
     * All the Loggers for different operations.
     */
    private static final Logger logger = LogManager.getLogger(TicketPool.class);
    private static final Logger loggerAdd = LogManager.getLogger("TicketPoolAdd");
    private static final Logger loggerSave = LogManager.getLogger("TicketPoolSave");
    private static final Logger loggerRemove = LogManager.getLogger("TicketPoolRemove");

    /**
     * This is the private constructor to enforce the singleton pattern on the Ticket pool system.
     *
     * @param maxCapacity The maximum capacity of the ticket pool.
     */
    private TicketPool(long maxCapacity) {
        this.maxCapacity = maxCapacity;
        loadTickets();  // Load tickets from JSON file
    }

    /**
     * This retrieves the singleton instance of TicketPool.
     *
     * @param maxCapacity The maximum capacity of the ticket pool.
     * @return The singleton TicketPool instance.
     */
    public static TicketPool getInstance(long maxCapacity) {
        if (instance == null) {
            instance = new TicketPool(maxCapacity);
        }
        return instance;
    }

    /**
     * This retrieves a set of all unique event names available in the pool.
     *
     * @return A set of unique event names.
     */
    public Set<String> getEventDetails() {
        ticketLock.lock();
        try {
            return tickets.stream()
                    .map(Ticket::getEventName)
                    .collect(Collectors.toSet());
        } finally {
            ticketLock.unlock();
        }
    }

    /**
     * This method loads tickets from the JSON file into the ticket pool.
     */
    private void loadTickets() {
        ticketLock.lock();
        try {
            try (FileReader reader = new FileReader(TICKETS_FILE)) {
                Type ticketListType = new TypeToken<List<Ticket>>() {}.getType();
                List<Ticket> loadedTickets = gson.fromJson(reader, ticketListType);
                if (loadedTickets != null) {
                    tickets.addAll(loadedTickets);
                }
                System.out.println("Loaded tickets from file.");
            } catch (IOException | com.google.gson.JsonSyntaxException e) {
                System.out.println("Could not load tickets: " + e.getMessage());
            }
        } finally {
            ticketLock.unlock();
        }
    }

    /**
     * This method counts the number of available tickets in the pool.
     *
     * @return The count of available tickets.
     */
    public int countAvailableTickets() {
        ticketLock.lock();
        try {
            return (int) tickets.stream().filter(ticket -> "Available".equals(ticket.getTicketStatus())).count();
        } finally {
            ticketLock.unlock();
        }
    }

    /**
     * This method Adds a ticket to the shared ticket pool.
     * If the pool is full, the vendor waits until space is available.
     *
     * @param ticket The ticket to be added.
     * @return True if the ticket was added successfully, false otherwise.
     */
    public boolean addTicket(Ticket ticket) {
        ticketLock.lock();
        try {
            // If the pool is full, vendors will wait until a customer removes a ticket.
            while (countAvailableTickets() >= maxCapacity) {
                loggerAdd.info("Ticket pool is full. Vendor: "+ ticket.getVendorId()+ " is waiting...");
                vendorCondition.await();  // All Vendor waits.
            }

            tickets.add(ticket);
            loggerAdd.info("Ticket added to pool: " + ticket.getTicketId() + " " + ticket.getEventName());
            saveTickets();  // Save tickets after addition.

            // Notify all consumers waiting for a specific event.
            notifyConsumersForEvent(ticket.getEventName());  // Notify only consumers looking for this event.

            // Cleanup unused conditions of customers.
            cleanupUnusedConditions();
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            ticketLock.unlock();
        }
    }

    /**
     * This method notifies consumers waiting for a specific event.
     *
     * @param eventName The name of the event for which consumers are waiting.
     */
    private void notifyConsumersForEvent(String eventName) {
        ticketLock.lock();
        try {
            // Notify consumers waiting for tickets for the event
            Condition eventCondition = consumerConditions.get(eventName);
            if (eventCondition != null) {
                eventCondition.signalAll();
            }

            // Cleanup the event condition if there are no more waiting consumers
            Integer usageCount = eventUsageCount.get(eventName);
            if (usageCount != null && usageCount <= 0) {
                // If no more consumers are waiting for this event, clean up the resources
                consumerConditions.remove(eventName);
                eventUsageCount.remove(eventName);
            }
        } finally {
            ticketLock.unlock();
        }
    }

    /**
     * This method checks if a ticket is available for a specific event.
     *
     * @param eventName The name of the event.
     * @return True if a ticket is available, false otherwise.
     */
    public boolean isTicketAvailable(String eventName) {
        ticketLock.lock();
        try {
            return tickets.stream().anyMatch(t -> "Available".equals(t.getTicketStatus()) &&
                    eventName.equals(t.getEventName()));
        } finally {
            ticketLock.unlock();
        }
    }

    /**
     * This method Books a ticket for a specific event from the pool.
     * If no ticket is available, the consumer waits for the specific events tickets to be available again.
     *
     * @param eventName  The name of the event.
     * @param customerId The ID of the customer booking the ticket.
     * @return True if the ticket was successfully booked, false otherwise.
     */
    public boolean removeTicket(String eventName, String customerId) {
        ticketLock.lock();
        try {
            // Consumer waits if no ticket is available for the specific event.
            while (!isTicketAvailable(eventName)) {
                System.out.println("No tickets available for event: " + eventName + ". Consumer waiting...");
                waitForSpecificEventTicket(eventName);
            }

            Ticket ticket = findAvailableTicket(eventName);
            if (ticket != null) {
                ticket.setTicketStatus("Booked");
                ticket.setCustomerId(customerId);
                loggerRemove.info("Ticket booked: " + ticket.getTicketId() + " for customer: " + customerId + " (Event: " + eventName + ")");
                saveTickets();

                // Notify vendors that space is now available (when a ticket has been booked)
                vendorCondition.signalAll();  // Notify vendors to potentially add new tickets.

                // Cleanup unused conditions customer conditions.
                cleanupUnusedConditions();
                return true;
            }
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            ticketLock.unlock();
        }
    }

    /**
     * This method make customers wait for a specific event ticket to become available.
     *
     * @param eventName The name of the event.
     * @throws InterruptedException If the thread is interrupted while waiting.
     */
    private void waitForSpecificEventTicket(String eventName) throws InterruptedException {
        ticketLock.lock();
        try {
            // If no ticket is available for the event, create a new condition for this event if not already created.
            consumerConditions.putIfAbsent(eventName, ticketLock.newCondition());
            // Increment the usage counter.
            eventUsageCount.put(eventName, eventUsageCount.getOrDefault(eventName, 0) + 1);
            // Now wait until the ticket for this event is available.
            consumerConditions.get(eventName).await();
        } finally {
            ticketLock.unlock();
        }
    }

    /**
     * This method finds an available ticket for a specific event.
     *
     * @param eventName The name of the event.
     * @return The available ticket, or null if no ticket is found.
     */
    private Ticket findAvailableTicket(String eventName) {
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
     * This method cleans up unused conditions for events with no waiting consumers.
     */
    private void cleanupUnusedConditions() {
        ticketLock.lock();
        try {
            // Iterate over the usage counter map
            Iterator<Map.Entry<String, Integer>> iterator = eventUsageCount.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Integer> entry = iterator.next();

                // Remove conditions for events with no waiting consumers
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
     * This method saves the current state of the ticket pool to the JSON file.
     */
    private void saveTickets() {
        ticketLock.lock();
        try (FileWriter writer = new FileWriter(TICKETS_FILE)) {
            gson.toJson(tickets, writer);
            loggerSave.info("Tickets saved to file.");
        } catch (IOException e) {
            loggerSave.error("Could not save tickets: " + e.getMessage());
        } finally {
            ticketLock.unlock();
        }
    }

    /**
     * This method displays the total available ticket count for each ticket released by the specific vendor,
     * to whom the vendor ID belongs to.
     * @param vendorId The unqiue identifier of the vendor.
     */
    public void viewAvailableTicketCountsByVendor(String vendorId) {
        ticketLock.lock();
        try {
            Map<String, Integer> ticketTypeCounts = getTicketCountsByVendor(vendorId);
            if (ticketTypeCounts.isEmpty()) {
                System.out.println("No available tickets found for vendor " + vendorId + ".");
            } else {
                System.out.println("Available tickets by vendor " + vendorId + ":");
                ticketTypeCounts.forEach((type, count) -> System.out.println(type + ": " + count));
            }
        } finally {
            ticketLock.unlock();
        }
    }

    /**
     * This is a helper method for the viewAvailableTicketCountsByVendor method. This extracts the
     * available ticket count for each event released by vendor.
     * @param vendorId The unique identifier of the vendor.
     * @return This returns a Map containing event names as keys and the total available ticket count for each event as
     * value.
     */
    private Map<String, Integer> getTicketCountsByVendor(String vendorId) {
        Map<String, Integer> ticketTypeCounts = new HashMap<>();

        for (Ticket ticket : tickets) {
            if (vendorId.equals(ticket.getVendorId()) && "Available".equals(ticket.getTicketStatus())) {
                String ticketType = ticket.getEventName(); // Using event name as ticket type
                ticketTypeCounts.put(ticketType, ticketTypeCounts.getOrDefault(ticketType, 0) + 1);
            }
        }
        return ticketTypeCounts;
    }

    /**
     * This method retrieves the sum of total available ticket count for each event in the ticket pool.
     */
    public void countAvailableTicketsByEvent() {
        ticketLock.lock();
        try {
            Map<String, Integer> availableTicketsByEvent = new HashMap<>();
            for (Ticket ticket : tickets) {
                if ("Available".equals(ticket.getTicketStatus())) {
                    String eventName = ticket.getEventName();
                    availableTicketsByEvent.put(eventName, availableTicketsByEvent.getOrDefault(eventName, 0) + 1);
                }
            }
            if (availableTicketsByEvent.isEmpty()) {
                System.out.println("No available tickets found in the ticket pool.");
            } else {
                System.out.println("Available tickets by event:");
                availableTicketsByEvent.forEach((eventName, count) -> System.out.println(eventName + ": " + count));
            }
        } finally {
            ticketLock.unlock();
        }
    }

    /**
     * This method retrieves the sum of total booked ticket count for each event in the ticket pool.
     */
    public void countBookedTicketsByEvent() {
        ticketLock.lock();
        try {
            Map<String, Integer> bookedTicketsByEvent = new HashMap<>();
            for (Ticket ticket : tickets) {
                if ("Booked".equals(ticket.getTicketStatus())) {
                    String eventName = ticket.getEventName();
                    bookedTicketsByEvent.put(eventName, bookedTicketsByEvent.getOrDefault(eventName, 0) + 1);
                }
            }
            if (bookedTicketsByEvent.isEmpty()) {
                System.out.println("No booked tickets found in the ticket pool.");
            } else {
                System.out.println("Booked tickets by event:");
                bookedTicketsByEvent.forEach((eventName, count) -> System.out.println(eventName + ": " + count));
            }
        } finally {
            ticketLock.unlock();
        }
    }


    /**
     * This method displays the total booked ticket count for each ticket purchased by the specific vendor,
     * to whom the customer ID belongs to.
     * @param customerId The unique identifier of the customer.
     */
    public void countBookedTicketsByCustomerId(String customerId) {
        ticketLock.lock();
        try {
            // Group the tickets by event and count the tickets booked by the specific customer
            Map<String, Long> ticketsByEvent = tickets.stream()
                    .filter(ticket -> "Booked".equals(ticket.getTicketStatus()) &&
                            customerId.equals(ticket.getCustomerId()))
                    .collect(Collectors.groupingBy(Ticket::getEventName, Collectors.counting()));

            if (ticketsByEvent.isEmpty()) {
                System.out.println("No tickets found for customer ID: " + customerId);
            } else {
                System.out.println("Tickets booked by customer ID: " + customerId);
                ticketsByEvent.forEach((event, count) ->
                        System.out.println("Event: " + event + ", Tickets Booked: " + count));
            }
        } finally {
            ticketLock.unlock();
        }
    }
}
