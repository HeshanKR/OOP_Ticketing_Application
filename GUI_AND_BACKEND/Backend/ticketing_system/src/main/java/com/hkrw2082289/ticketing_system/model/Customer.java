//Real-Time Ticketing System Backend by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.
package com.hkrw2082289.ticketing_system.model;
import com.hkrw2082289.ticketing_system.helper.PurchaseRequest;
import com.hkrw2082289.ticketing_system.service.TicketPoolService;
import jakarta.persistence.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class represents a customer in the ticketing system.
 *
 * This class implements the {@link Runnable} interface to simulate a customer making purchases concurrently.
 * It handles the customer's authentication details, the number of tickets they wish to purchase, and
 * interacts with the {@link TicketPoolService} to book tickets.
 */
@Data
@Entity
@Table(name = "customers")
public class Customer implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(Customer.class);

    /**
     * An atomic flag is used to control global purchase stopping functionality.
     * This is used to stop all customer purchases globally.
     */
    private static final AtomicBoolean adminStopAllPurchases = new AtomicBoolean(false);

    /**
     * This is the unique identifier of the customer.
     * This value is used for authentication and is unique for each customer.
     */
    @Id
    @Column(name = "customer_id", nullable = false, unique = true, length = 7)
    private String customerId;

    /**
     * This is the password of the customer for authentication.
     * This value is required and cannot be null.
     */
    @Column(nullable = false, length = 12)
    private String password;

    /**
     * This is the rate at which the customer retrieves tickets from the pool.
     * This value is transient and is not stored in the database.
     */
    @Transient
    private double customerRetrievalRate;

    /**
     * This is the purchase request containing the number of tickets the customer wants to book
     * and the event name for which the tickets are being booked.
     * This value is transient and is not stored in the database.
     */
    @Transient
    private PurchaseRequest purchaseRequest;

    /**
     * This is the service class used to interact with the ticket pool for ticket booking.
     * This value is transient and is not stored in the database.
     */
    @Transient
    private TicketPoolService ticketPoolService;

    /**
     * This executes the customer's thread, attempting to book the specified number of tickets for a certain event.
     * The customer attempts to book tickets until the specified batch size is reached or until
     * a global stop is enabled or the thread is interrupted.
     * This class logs details related to each customer thread created to purchase tickets.
     */
    @Override
    public void run() {
        logger.info("Thread started for Customer ID: {} with purchase batch size: {} (Thread ID: {})",
                customerId, purchaseRequest != null ? purchaseRequest.getTicketToBook(): 0,
                Thread.currentThread().getId());

        if (ticketPoolService == null) {
            logger.error("TicketPoolService is not set. Cannot book tickets to the pool.");
            return;
        }
        try{
            String eventName = purchaseRequest.getEventName();
            int ticketsToBook = purchaseRequest.getTicketToBook();

            int ticketbooked = 0;
            for (int i = 0; i < ticketsToBook; i++) {
                // Check if global stop flag is enabled.
                if (adminStopAllPurchases.get()) {
                    logger.info("Global stop enabled. Customer {} thread will terminate. (Thread ID: {})",
                            customerId, Thread.currentThread().getId());
                    break;
                }

                if (Thread.currentThread().isInterrupted()) {
                    logger.info("Thread for Customer ID: {} was interrupted. (Thread ID: {})", customerId,
                            Thread.currentThread().getId());
                    break;
                }
                Object[] booked = ticketPoolService.removeTicket(eventName, customerId);
                if ((boolean)booked[0]) {
                    ticketbooked++;
                    logger.info("Successfully booked ticket ID: {} to the pool." +
                            "Ticket No. of ticked booked in batch:{} ", booked[1],ticketbooked);
                } else {
                    logger.warn("Failed to book ticket ID: {} to the pool.", booked[1]);
                }
                Thread.sleep((long) customerRetrievalRate);
            }
            logger.info("Thread:{} for customer with ID:{} finished executing",
                    Thread.currentThread().getId(), customerId);
        }
        catch (InterruptedException e) {
            logger.error("Thread for Customer {} was interrupted. (Thread ID: {})", customerId,
                    Thread.currentThread().getId());
            Thread.currentThread().interrupt();
        }
        logger.info("Thread completed for Vendor ID: {} (Thread ID: {})", customerId, Thread.currentThread().getId());
    }

    // Methods to manage the adminStopAllPurchases flag

    /**
     * This method checks if the global stop flag is enabled, preventing all purchases.
     *
     * @return true if global stop is enabled, false otherwise.
     */
    public static boolean isAdminStopAllPurchases() {
        return adminStopAllPurchases.get();
    }

    /**
     * This method is used to enable the global stop flag, preventing all purchases from occurring.
     */
    public static void enableStopAllPurchases() {
        adminStopAllPurchases.set(true);
        logger.info("Global stop for all purchases enabled.");
    }

    /**
     * This method is used to disable the global stop flag, allowing purchases to resume.
     */
    public static void disableStopAllPurchases() {
        adminStopAllPurchases.set(false);
        logger.info("Global stop for all purchases disabled.");
    }
}
