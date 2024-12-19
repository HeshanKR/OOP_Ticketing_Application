//Real-Time Ticketing System Backend by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.
package com.hkrw2082289.ticketing_system.controller;
import com.hkrw2082289.ticketing_system.helper.PurchaseRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * This Controller class is for handling purchase requests for customers in the ticketing system.
 * This class contains endpoints for creating a purchase request for customers.
 * This is class is used for only testing purposes to see if the purchase request works.
 */
@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    /**
     * This endpoint create a purchase request for tickets.
     *
     * This method accepts a payload containing customer details, number of tickets to book,
     * and the event name, and returns a PurchaseRequest object with the provided details.
     *
     * @param payload a map containing the customer ID, number of tickets to book, and event name.
     *                expected keys are: "customer_id", "ticketToBook", and "event_Name".
     * @return a ResponseEntity containing the created PurchaseRequest.
     */
    @PostMapping("/create")
    public ResponseEntity<PurchaseRequest> createPurchaseRequest(@RequestBody Map<String, Object> payload) {
        String customerId = (String) payload.get("customer_id");
        int ticketToBook = (int) payload.get("ticketToBook");
        String eventName = (String) payload.get("event_Name");
        // Create a new PurchaseRequest object with the extracted details.
        PurchaseRequest purchaseRequest = new PurchaseRequest(customerId, ticketToBook, eventName);
        // Return the created PurchaseRequest object in the response body.
        return ResponseEntity.ok(purchaseRequest);
    }
}
