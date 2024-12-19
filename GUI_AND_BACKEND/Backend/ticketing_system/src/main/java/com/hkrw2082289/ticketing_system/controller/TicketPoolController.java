//Real-Time Ticketing System Backend by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.
package com.hkrw2082289.ticketing_system.controller;
import com.hkrw2082289.ticketing_system.service.TicketPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * This controller class is for retrieving ticket pool data related to tickets.
 * This provides endpoints for retrieving available and booked ticket counts by vendor, event, and customer.
 */
@RestController
@RequestMapping("api/ticket-pool")
public class TicketPoolController {

    private final TicketPoolService ticketPool;

    /**
     * This constructor constructs a TicketPoolController with the provided TicketPoolService class.
     *
     * @param ticketPool the service used for ticket pool data retrieval.
     */
    @Autowired
    public TicketPoolController(TicketPoolService ticketPool) {
        this.ticketPool = ticketPool;
    }

    /**
     * This endpoint is used to get the count of available tickets grouped by event for a specific vendor.
     *
     * @param vendorId the ID of the vendor for which the available ticket count is requested.
     * @return a ResponseEntity containing the count of available tickets grouped by event for the given vendor.
     */
    @GetMapping("/available-tickets/vendor/{vendorId}")
    public ResponseEntity<?> viewAvailableTicketCountsByVendor(@PathVariable String vendorId) {
        return ResponseEntity.ok(ticketPool.viewAvailableTicketCountsByVendor(vendorId));
    }

    /**
     * This endpoint is used to get the count of available tickets for all events grouped by event name.
     *
     * @return a ResponseEntity containing the count of available tickets for each event.
     */
    @GetMapping("/available-tickets/event")
    public ResponseEntity<?> countAvailableTicketsByEvent() {
        return ResponseEntity.ok(ticketPool.countAvailableTicketsByEvent());
    }

    /**
     * This endpoint is used to get the count of booked tickets for all events grouped by event name.
     *
     * @return a ResponseEntity containing the count of booked tickets for each event.
     */
    @GetMapping("/booked-tickets/event")
    public ResponseEntity<?> countBookedTicketsByEvent() {
        return ResponseEntity.ok(ticketPool.countBookedTicketsByEvent());
    }

    /**
     * This endpoint is used to get the count of booked tickets grouped by event for a specific customer.
     *
     * @param customerId the ID of the customer for which the booked ticket count is requested.
     * @return a ResponseEntity containing the count of booked tickets grouped by event for the given customer.
     */
    @GetMapping("/booked-tickets/customer/{customerId}")
    public ResponseEntity<?> countBookedTicketsByCustomerId(@PathVariable String customerId) {
        return ResponseEntity.ok(ticketPool.countBookedTicketsByCustomerId(customerId));
    }
}

