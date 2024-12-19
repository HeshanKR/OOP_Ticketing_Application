// Real-time ticketing system application : Heshan Ratnaweera | UOW: w2082289 | IIT: 20222094

import React, { useEffect, useRef, useState } from "react";
import AvailableTicketsPopup from "./AvailableTicketsPopup";
import ViewBookedTicketsPopup from "./ViewBookedTicketsPopup";
import PurchaseTicketsPopup from "./PurchaseTicketsPopup";
import apiClient from "../api";
import { Client } from "@stomp/stompjs";
import { connectWebSocket } from "../websocket";
import LogDisplay from "./LogDisplay";
import TicketTable from "./TicketTable";

// Interface for props passed to CustomerMenu component
interface CustomerMenuProps {
  customerId: string; // Customer ID passed as a prop
  onClose: () => void; // Callback to handle menu closure
  showNotification: (message: string, isError?: boolean) => void; // Function to display notifications.
}

// Interface defining the structure of a Ticket object
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

// CustomerMenu component: Main UI for customer ticket management.
const CustomerMenu: React.FC<CustomerMenuProps> = ({
  customerId,
  onClose,
  showNotification,
}) => {
  // State variables to control visibility of various popups and store ticket data.
  const [showPurchaseTicketsPopup, setShowPurchaseTicketsPopup] =
    useState(false);
  const [showViewBookedTicketsPopup, setShowViewBookedTicketsPopup] =
    useState(false);

  const [showAvailableTicketsPopup, setShowAvailableTicketsPopup] =
    useState(false);
  const [availableTicketsData, setAvailableTicketsData] = useState<Record<
    string,
    number
  > | null>(null); // Stores available tickets data.

  // Function to handle viewing available tickets
  const handleViewAvailableTickets = async () => {
    try {
      const response = await apiClient.get(
        `/ticket-pool/available-tickets/event`
      );
      setAvailableTicketsData(response.data); // Update available tickets data
      setShowAvailableTicketsPopup(true); // Show available tickets popup
    } catch (error) {
      showNotification(
        "Failed to fetch available tickets. Please try again.",
        true // Display error notification
      );
    }
  };

  // Function to stop ticket purchase (can be triggered by the user)
  const handleStopPurchase = async () => {
    try {
      const response = await apiClient.post(
        `/customers/${customerId}/stop-thread`
      );
      showNotification(response.data, false); // Green notification for success
    } catch (error: any) {
      const errorMessage =
        error.response?.data || "Failed to stop ticket purchase. Try again.";
      showNotification(errorMessage, true); // Red notification for error
    }
  };

  // State variable to store the list of tickets.
  const [tickets, setTickets] = useState<Ticket[]>([]);
  // State variable for storing log messages received from WebSocket.
  const [logMessages, setLogMessages] = useState<string[]>([]);
  const webSocketRef = useRef<Client | null>(null); // Reference for WebSocket client.

  // useEffect hook to fetch tickets data and set up WebSocket connection
  useEffect(() => {
    const fetchTickets = async () => {
      try {
        const response = await apiClient.get("/tickets/all");
        if (response.data.length === 0) {
          setTickets([]);
          showNotification("No tickets available in the pool.");
        } else {
          setTickets(response.data);
        }
      } catch (error) {
        console.error("Error fetching tickets:", error);
        showNotification("Failed to load tickets.", true);
      }
    };

    fetchTickets();

    // Connect WebSocket only if not already connected
    if (!webSocketRef.current) {
      webSocketRef.current = connectWebSocket(
        (newTickets: Ticket[]) => {
          setTickets(newTickets);
        },
        (logMessage: string) => {
          console.log("Received log message:", logMessage);
          //setting the log messages display data.
          setLogMessages((prevMessages) => {
            if (!prevMessages.includes(logMessage)) {
              return [...prevMessages, logMessage];
            }
            return prevMessages;
          });
        }
      );
    }

    return () => {
      // Cleanup WebSocket connection on component unmount
      if (webSocketRef.current) {
        webSocketRef.current.deactivate(); // Deactivate WebSocket connection.
        webSocketRef.current = null;
      }
    };
  }, []);

  return (
    <div className="container-menu">
      <div className="user-menu">
        <h3>Customer Menu For: {customerId}</h3>
        <button
          className="button"
          onClick={() => setShowPurchaseTicketsPopup(true)}
        >
          Purchase New Tickets
        </button>
        <button
          className="button"
          onClick={() => setShowViewBookedTicketsPopup(true)}
        >
          View All My Booked Tickets
        </button>
        <button className="button" onClick={handleViewAvailableTickets}>
          View Other Available Tickets
        </button>
        <button className="button" onClick={handleStopPurchase}>
          Stop Purchase of Tickets
        </button>
        <button className="close-button" onClick={onClose}>
          Close
        </button>
      </div>
      <div className="side-panel-overlay">
        {/* Display logs and ticket information side by side */}
        <div className="half-height-overlay">
          <LogDisplay messages={logMessages}></LogDisplay>
        </div>
        <div className="half-height-overlay">
          <TicketTable tickets={tickets}></TicketTable>
        </div>
      </div>
      {/* Conditional rendering for popups */}
      {showPurchaseTicketsPopup && (
        <PurchaseTicketsPopup
          customerId={customerId}
          onClose={() => setShowPurchaseTicketsPopup(false)}
          showNotification={showNotification}
        />
      )}

      {showViewBookedTicketsPopup && (
        <ViewBookedTicketsPopup
          customerId={customerId}
          onClose={() => setShowViewBookedTicketsPopup(false)}
          showNotification={showNotification}
        />
      )}

      {showAvailableTicketsPopup && (
        <AvailableTicketsPopup
          ticketsData={availableTicketsData}
          onClose={() => setShowAvailableTicketsPopup(false)}
        />
      )}
    </div>
  );
};

export default CustomerMenu;
