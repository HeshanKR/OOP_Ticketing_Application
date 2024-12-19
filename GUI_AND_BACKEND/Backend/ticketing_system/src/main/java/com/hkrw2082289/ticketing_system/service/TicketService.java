//Real-Time Ticketing System Backend by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.
package com.hkrw2082289.ticketing_system.service;
import com.hkrw2082289.ticketing_system.model.TicketEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This service class is used to conduct ticket creation related operations.
 *
 * This service provides functionality to create tickets in batches for an event with the help
 * of TicketEntity class.
 */
@Service
public class TicketService {

    /**
     * This method creates a batch of tickets for a specified event using the provided details.
     *
     * @param vendorId the ID of the vendor creating the tickets.
     * @param eventName the name of the event for which tickets are being created.
     * @param price the price of each ticket.
     * @param timeDuration the time duration of the event.
     * @param date the date of the event (in yyyy-MM-dd format).
     * @param batchSize the number of tickets to create in the batch.
     * @return a {@link List} of {@link TicketEntity} representing the created tickets.
     */
    public List<TicketEntity> createTickets(String vendorId, String eventName,
                                            double price, String timeDuration,
                                            String date, int batchSize) {
        // Generate the list of tickets using the static method from TicketEntity
        List<TicketEntity> ticketEntities = TicketEntity.generateTicketBatch(vendorId, eventName,
                price, timeDuration, date, batchSize);
        return ticketEntities;
    }
}

