//Real-Time Ticketing System Backend by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.
package com.hkrw2082289.ticketing_system.model;
import com.hkrw2082289.ticketing_system.service.TicketPoolService;
import jakarta.persistence.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class represents a vendor in the ticketing system.
 *
 * This class implements the {@link Runnable} interface to simulate a vendor adding tickets to the ticket
 * pool concurrently.
 * It handles the vendor's authentication details, ticket batch management,
 * and interacts with the {@link TicketPoolService} to add tickets to the shared pool of tickets.
 */
@Data
@Entity
@Table(name = "vendors")
public class Vendor implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(Vendor.class);

    /**
     * An atomic flag to control ticket release globally.
     * This is used to stop all ticket releases by vendors.
     */
    private static final AtomicBoolean adminStopAllRelease = new AtomicBoolean(false);

    /**
     * This is the unique identifier of the vendor.
     * This value is used for authentication and is unique for each vendor.
     */
    @Id
    @Column(name = "vendor_id", nullable = false, unique = true, length = 7)
    private String vendorId;

    /**
     * This is the password of the vendor for authentication.
     * This value is required and cannot be null.
     */
    @Column(nullable = false, length = 12)
    private String password;

    /**
     * This is the rate at which the vendor releases tickets to the pool.
     * This value is transient and is not stored in the database.
     */
    @Transient
    private double ticketReleaseRate;

    /**
     * This is the batch of tickets that the vendor is releasing to the pool.
     * This value is transient and is not stored in the database.
     */
    @Transient
    private List<TicketEntity> ticketBatch;

    /**
     * This is the service class used to interact with the ticket pool for ticket management.
     * This value is transient and is not stored in the database.
     */
    @Transient
    private TicketPoolService ticketPoolService;

    /**
     * This executes the vendor's thread, which releases tickets to the ticket pool.
     * The vendor releases tickets until the batch size is reached or until
     * a global stop is enabled or the thread is interrupted.
     * This class logs details related to each vendor thread created to add tickets.
     */
    @Override
    public void run() {
        logger.info("Thread started for Vendor ID: {} with ticket batch size: {} (Thread ID: {})",
                vendorId, ticketBatch != null ? ticketBatch.size() : 0, Thread.currentThread().getId());
        if (ticketPoolService == null) {
            logger.error("TicketPoolService is not set. Cannot add tickets to the pool.");
            return;
        }
        int ticketaddedcount =0;
        try {
            for (TicketEntity ticket : ticketBatch) {
                // Check if global stop flag is enabled
                if (adminStopAllRelease.get()) {
                    logger.info("Global stop enabled. Vendor {} thread will terminate. (Thread ID: {})",
                            vendorId, Thread.currentThread().getId());
                    break;
                }
                if (Thread.currentThread().isInterrupted()) {
                    logger.info("Thread for Vendor ID: {} was interrupted. (Thread ID: {})",
                            vendorId, Thread.currentThread().getId());
                    break;
                }
                logger.info("Processing ticket with event name: {} for Vendor: {}", ticket.getEventName(), vendorId);
                boolean added = ticketPoolService.addTicket(ticket);

                if (added) {
                    ticketaddedcount++;
                    logger.info("Successfully added ticket ID: {} of event: {} to the pool." +
                            " Ticket number in the batch: {}", ticket.getTicketId(),
                            ticket.getEventName(),ticketaddedcount);
                } else {
                    logger.warn("Failed to add ticket ID: {} to the pool." +
                            " The ticket generated has a duplicate ticket ID.", ticket.getTicketId());
                }
                Thread.sleep((long) ticketReleaseRate);
            }
            logger.info("Thread:{} for vendor with ID:{} finished executing", Thread.currentThread().getId(), vendorId);
        } catch (InterruptedException e) {
            logger.error("Thread for Vendor {} was interrupted. (Thread ID: {})", vendorId,
                    Thread.currentThread().getId());
            Thread.currentThread().interrupt();
        }
        logger.info("Thread completed for Vendor ID: {} (Thread ID: {})", vendorId, Thread.currentThread().getId());
    }

    // Methods to manage the adminStopAllRelease flag

    /**
     * This method checks if the global stop flag is enabled, preventing all ticket releases by vendors.
     *
     * @return true if global stop is enabled, false otherwise
     */
    public static boolean isAdminStopAllRelease() {
        return adminStopAllRelease.get();
    }

    /**
     * The method is used to enable the global stop flag, preventing all ticket releases by vendors.
     */
    public static void enableStopAllRelease() {
        adminStopAllRelease.set(true);
        logger.info("Global stop for all Ticket Release enabled.");
    }

    /**
     * This method is used to disable the global stop flag, allowing ticket releases by vendors to resume.
     */
    public static void disableStopAllRelease() {
        adminStopAllRelease.set(false);
        logger.info("Global stop for all Ticket Release disabled.");
    }
}
