//Real-Time Ticketing System Backend by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.
package com.hkrw2082289.ticketing_system.controller;
import com.hkrw2082289.ticketing_system.model.TicketEntity;
import com.hkrw2082289.ticketing_system.repository.TicketRepository;
import com.hkrw2082289.ticketing_system.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * This is controller class for managing ticket-related operations such as finding all tickets from the
 * Ticket Repository and generating tickets to check if the ticket generation works.
 * Provides endpoints for retrieving all tickets and generating new tickets for an event.
 */
@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    private final TicketRepository ticketRepository;

    /**
     * This constructor is used to initialize the TicketController with the TicketRepository.
     *
     * @param ticketRepository the TicketRepository to be used for data access.
     */
    public TicketController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /**
     * This endpoint is used to retrieve all tickets from the database. This is used by the websockets in the frontend
     * to show the tickets in a table.
     * This method fetches all ticket records from the ticket repository.
     *
     * @return a list of all TicketEntity objects.
     */
    @GetMapping("all")
    public List<TicketEntity> getAllTickets() {
        return ticketRepository.findAll();
    }

    /**
     * This endpoint is used to generate a batch of tickets for a specific event.
     * This method creates a batch of tickets based on the provided vendor ID, event name, price,
     * time duration, date, and batch size, using the ticket service.
     *
     * @param payload a map containing the details required for generating tickets:
     *                - vendor_Id: the ID of the vendor.
     *                - event_Name: the name of the event.
     *                - price: the price of each ticket.
     *                - time_Duration: the time duration of the event.
     *                - date: the date of the event.
     *                - batch_Size: the number of tickets to generate.
     * @return a ResponseEntity containing the list of generated TicketEntity objects.
     */
    @PostMapping("/generate")
    public ResponseEntity<List<TicketEntity>> generateTickets(@RequestBody Map<String, Object> payload) {
        String vendorId = (String) payload.get("vendor_Id");
        String eventName = (String) payload.get("event_Name");
        double price = (double) payload.get("price");
        String timeDuration = (String) payload.get("time_Duration");
        String date = (String) payload.get("date");
        int batchSize = (int) payload.get("batch_Size");

        List<TicketEntity> tickets = ticketService.createTickets(vendorId, eventName, price,
                timeDuration, date, batchSize);
        return ResponseEntity.ok(tickets);
    }
}
