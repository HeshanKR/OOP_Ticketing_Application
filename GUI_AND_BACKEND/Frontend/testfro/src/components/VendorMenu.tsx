// Real-time ticketing system application : Heshan Ratnaweera | UOW: w2082289 | IIT: 20222094
import React, { useEffect, useRef, useState } from "react";
import AddTicketsPopup from "./AddTicketsPopup";
import ViewAddedTicketsPopup from "./ViewAddedTicketsPopup";
import AvailableTicketsPopup from "./AvailableTicketsPopup";
import apiClient from "../api";
import TicketTable from "./TicketTable";
import LogDisplay from "./LogDisplay";
import { Client } from "@stomp/stompjs";
import { connectWebSocket } from "../websocket";

interface VendorMenuProps {
  vendorId: string; // Unique identifier for the vendor.
  onClose: () => void; // Callback to handle menu closure.
  showNotification: (message: string, isError?: boolean) => void; // Function to show notifications, optionally marking as an error.
}

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

const VendorMenu: React.FC<VendorMenuProps> = ({
  vendorId,
  onClose,
  showNotification,
}) => {
  // State for controlling popups and storing ticket data.
  const [showAddTicketsPopup, setShowAddTicketsPopup] = useState(false);

  const [showViewAddedTicketsPopup, setShowViewAddedTicketsPopup] =
    useState(false);

  const [showAvailableTicketsPopup, setShowAvailableTicketsPopup] =
    useState(false);
  const [availableTicketsData, setAvailableTicketsData] = useState<Record<
    string,
    number
  > | null>(null);

  // State for managing tickets and log.
  const [tickets, setTickets] = useState<Ticket[]>([]);
  const [logMessages, setLogMessages] = useState<string[]>([]);
  const webSocketRef = useRef<Client | null>(null); // Ref to manage WebSocket connection.

  // Fetch available tickets and open the respective popup.
  const handleViewAvailableTickets = async () => {
    try {
      // Make an API request to view available.
      const response = await apiClient.get(
        `/ticket-pool/available-tickets/event`
      );
      setAvailableTicketsData(response.data);
      setShowAvailableTicketsPopup(true);
    } catch (error) {
      showNotification(
        "Failed to fetch available tickets. Please try again.",
        true
      );
    }
  };

  const handleStopTicketRelease = async () => {
    try {
      // Make an API request to start releasing tickets.
      const response = await apiClient.post(`/vendors/${vendorId}/stop-thread`);
      // Show success notification
      showNotification(response.data, false); // Green background success message.
    } catch (error: any) {
      // Extract the error message if available
      const errorMessage =
        error.response?.data || "Failed to stop ticket release. Try again.";
      // Show error notification
      showNotification(errorMessage, true); // Red background success message.
    }
  };

  // Fetch tickets and establish WebSocket connection on mount.
  useEffect(() => {
    const fetchTickets = async () => {
      try {
        // Make an API request to fetch ticket updates.
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

    // Connect WebSocket only if not already connected.
    if (!webSocketRef.current) {
      webSocketRef.current = connectWebSocket(
        (newTickets: Ticket[]) => {
          setTickets(newTickets); // Update tickets on receiving new data.
        },
        (logMessage: string) => {
          console.log("Received log message:", logMessage); // Debug log message.
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
        webSocketRef.current.deactivate();
        webSocketRef.current = null;
      }
    };
  }, []);

  return (
    <div className="container-menu">
      <div className="user-menu">
        <h3>Vendor Menu For: {vendorId}</h3>
        <button className="button" onClick={() => setShowAddTicketsPopup(true)}>
          Start Adding Tickets
        </button>
        <button
          className="button"
          onClick={() => setShowViewAddedTicketsPopup(true)}
        >
          View All My Added Tickets
        </button>
        <button className="button" onClick={handleViewAvailableTickets}>
          View Other Available Tickets
        </button>
        <button className="button" onClick={handleStopTicketRelease}>
          Stop Ticket Release
        </button>
        <button className="close-button" onClick={onClose}>
          Close
        </button>
      </div>
      <div className="side-panel-overlay">
        <div className="half-height-overlay">
          <LogDisplay messages={logMessages}></LogDisplay>
        </div>
        <div className="half-height-overlay">
          <TicketTable tickets={tickets}></TicketTable>
        </div>
      </div>
      {/* Conditional rendering for popups */}
      {showAddTicketsPopup && (
        <AddTicketsPopup
          vendorId={vendorId}
          onClose={() => setShowAddTicketsPopup(false)}
          showNotification={showNotification}
        />
      )}

      {showViewAddedTicketsPopup && (
        <ViewAddedTicketsPopup
          vendorId={vendorId}
          onClose={() => setShowViewAddedTicketsPopup(false)}
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

export default VendorMenu;
