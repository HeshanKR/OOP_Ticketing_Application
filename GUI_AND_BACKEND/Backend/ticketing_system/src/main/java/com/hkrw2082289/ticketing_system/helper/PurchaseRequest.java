//Real-Time Ticketing System Backend by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.
package com.hkrw2082289.ticketing_system.helper;
import lombok.Data;

/**
 * This is a helper class used when threads are to be run by customers,
 * this is helping customers organize their ticket purchase request.
 */

@Data
public class PurchaseRequest {

    /**
     * This is the unique identifier for the customer making the purchase.
     */
    private String customerId;

    /**
     * This is used to store the number of tickets the customer wants to book.
     */
    private int ticketToBook;

    /**
     * This stores the name of the event for which tickets are being booked.
     */
    private String eventName;

    /**
     * Constructs a new {@code PurchaseRequest} instance with the specified details.
     *
     * @param customerId   the unique identifier for the customer.
     * @param ticketToBook the number of tickets the customer wants to book.
     * @param eventName    the name of the event for which tickets are being booked.
     */
    public PurchaseRequest(String customerId, int ticketToBook, String eventName) {
        this.customerId = customerId;
        this.ticketToBook = ticketToBook;
        this.eventName = eventName;
    }
}
