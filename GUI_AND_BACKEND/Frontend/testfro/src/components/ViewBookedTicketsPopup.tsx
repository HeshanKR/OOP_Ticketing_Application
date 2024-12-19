// Real-time ticketing system application : Heshan Ratnaweera | UOW: w2082289 | IIT: 20222094
import React, { useEffect, useState } from "react";
import apiClient from "../api";

// Props interface for the ViewBookedTicketsPopup component.
interface ViewBookedTicketsPopupProps {
  customerId: string; // ID of the customer whose booked tickets are being displayed.
  onClose: () => void; // Callback function to close the popup.
  showNotification: (message: string, isError?: boolean) => void; // Function to display notifications to the user.
}

// Functional component to view tickets booked by a specific customer.
const ViewBookedTicketsPopup: React.FC<ViewBookedTicketsPopupProps> = ({
  customerId,
  onClose,
  showNotification,
}) => {
  // State to hold the list of tickets as a map of event names to ticket counts.
  const [tickets, setTickets] = useState<Record<string, number> | null>(null);
  const [loading, setLoading] = useState(true); // State to indicate if data is being fetched.

  // Fetch tickets booked by the customer when the component is mounted or customerId changes.
  useEffect(() => {
    const fetchTickets = async () => {
      try {
        // Make API request to get tickets booked by the customer.
        const response = await apiClient.get(
          `/ticket-pool/booked-tickets/customer/${customerId}`
        );
        setTickets(response.data); // Update state with fetched data.
      } catch (error) {
        // Show error notification if the API call fails.
        showNotification(
          "Failed to fetch tickets. Please try again later.",
          true
        );
      } finally {
        setLoading(false); // Mark loading as complete.
      }
    };

    fetchTickets();
  }, [customerId, showNotification]); // Dependency array ensures this effect runs when customerId or showNotification changes.

  return (
    <div className="popup-overlay">
      <div className="popup-content">
        <h3>All Tickets Booked by Customer: {customerId}</h3>
        {loading ? (
          <p>Loading...</p>
        ) : (
          <div className="scrollable-div">
            {tickets && Object.keys(tickets).length > 0 ? (
              Object.entries(tickets).map(([eventName, count]) => (
                <p key={eventName}>
                  {eventName} : {count}
                </p>
              ))
            ) : (
              <p>There are no tickets booked by the customer.</p>
            )}
          </div>
        )}
        <button className="close-button" onClick={onClose}>
          Close
        </button>
      </div>
    </div>
  );
};

export default ViewBookedTicketsPopup;
