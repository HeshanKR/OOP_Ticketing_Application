// Real-time ticketing system application : Heshan Ratnaweera | UOW: w2082289 | IIT: 20222094
import React from "react";

//This is the interface defining the shape of a ticket object.
interface Ticket {
  ticketId: number;
  eventName: string;
  price: number;
  timeDuration: string;
  date: string;
  vendorId: string;
  ticketStatus: string;
  customerId?: string | null;
}

//Props for the TicketTable component.
interface TicketTableProps {
  tickets: Ticket[]; //List of tickets to be displayed in the table.
}

const TicketTable: React.FC<TicketTableProps> = ({ tickets }) => {
  return (
    <table className="ticket-table">
      <thead>
        <tr>
          <th>Ticket ID</th>
          <th>Event Name</th>
          <th>Price</th>
          <th>Duration</th>
          <th>Date</th>
          <th>Vendor ID</th>
          <th>Status</th>
          <th>Customer ID</th>
        </tr>
      </thead>
      <tbody>
        {tickets.length === 0 ? (
          <tr>
            <td colSpan={8}>No tickets available</td>{" "}
            {/* Display message when no tickets */}
          </tr>
        ) : (
          tickets.map((ticket) => (
            <tr key={ticket.ticketId}>
              <td>{ticket.ticketId}</td>
              <td>{ticket.eventName}</td>
              <td>{ticket.price}</td>
              <td>{ticket.timeDuration}</td>
              <td>{new Date(ticket.date).toLocaleDateString()}</td>
              <td>{ticket.vendorId}</td>
              <td>{ticket.ticketStatus}</td>
              <td>{ticket.customerId || "N/A"}</td>
            </tr>
          ))
        )}
      </tbody>
    </table>
  );
};

export default TicketTable;
