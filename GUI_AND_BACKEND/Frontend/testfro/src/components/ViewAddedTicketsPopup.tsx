// Real-time ticketing system application : Heshan Ratnaweera | UOW: w2082289 | IIT: 20222094
import React, { useEffect, useState } from "react";
import apiClient from "../api";

// Props interface for the ViewAddedTicketsPopup component.
interface ViewAddedTicketsPopupProps {
  vendorId: string; // ID of the vendor whose tickets are being displayed.
  onClose: () => void; // Callback to close the popup.
  showNotification: (message: string, isError?: boolean) => void; // Function to display notifications with option error flag.
}

// Functional component to view tickets added by a specific vendor.
const ViewAddedTicketsPopup: React.FC<ViewAddedTicketsPopupProps> = ({
  vendorId,
  onClose,
  showNotification,
}) => {
  // State to hold tickets data as a map of event names to ticket counts.
  const [tickets, setTickets] = useState<Record<string, number> | null>(null);
  const [loading, setLoading] = useState(true); // State to indicate if data is being loaded.

  // Fetch tickets added by the vendor on component mount or vendorId change.
  useEffect(() => {
    const fetchTickets = async () => {
      try {
        // Make API request to fetch tickets released by the vendor.
        const response = await apiClient.get(
          `/ticket-pool/available-tickets/vendor/${vendorId}`
        );
        setTickets(response.data); // Update state with the fetched data.
      } catch (error) {
        showNotification(
          "Failed to fetch tickets. Please try again later.",
          true
        );
      } finally {
        setLoading(false); // Mark loading as complete
      }
    };

    fetchTickets();
  }, [vendorId, showNotification]); // Dependency array ensures the effect runs when vendorId or showNotification changes.

  return (
    <div className="popup-overlay">
      <div className="popup-content">
        <h3>Tickets Added by Vendor: {vendorId} Available for Purchase</h3>
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
              <p>
                There are no tickets added by the vendor that are available for
                purchase.
              </p>
            )}
          </div>
        )}
        {/* Close button to dismiss the popup */}
        <button className="close-button" onClick={onClose}>
          Close
        </button>
      </div>
    </div>
  );
};

export default ViewAddedTicketsPopup;
