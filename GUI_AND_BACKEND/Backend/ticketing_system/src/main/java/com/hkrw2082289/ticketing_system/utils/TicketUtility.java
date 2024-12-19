//Real-Time Ticketing System Backend by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.
package com.hkrw2082289.ticketing_system.utils;
import com.hkrw2082289.ticketing_system.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class is functioning as a utility class to help as a mediator between the two classes:
 * TicketPoolService and ConfigurationService
 * This class provides methods to access the Ticket Repository which contains all Ticket data stored in it.
 * The only functionality this class provides is counting row of Ticket data with ticket_status set to "Available" in
 * the Ticket Repository and returning the count value to the caller.
 */

@Component
public class TicketUtility {

    private final TicketRepository ticketRepository;

    /**
     * This Constructs a {@link TicketUtility} instance with the provided {@link TicketRepository}.
     *
     * @param ticketRepository the {@link TicketRepository} used for accessing ticket data.
     */
    @Autowired
    public TicketUtility(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /**
     * This counts the number of available tickets in the system.
     *
     * This method filters the tickets by their ticket_status and counts how many have the status "Available".
     *
     * @return the number of tickets that are available
     */
    public long countAvailableTickets() {
        return ticketRepository.findAll().stream()
                .filter(ticket -> "Available".equals(ticket.getTicketStatus()))
                .count();
    }
}

