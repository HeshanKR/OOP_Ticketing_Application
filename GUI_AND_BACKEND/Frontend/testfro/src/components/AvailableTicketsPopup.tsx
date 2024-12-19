// Real-time ticketing system application : Heshan Ratnaweera | UOW: w2082289 | IIT: 20222094
import React from "react";

// Props interface for the AvailableTicketsPopup component
interface AvailableTicketsPopupProps {
  ticketsData: Record<string, number> | null; // Ticket data as a key-value pair
  onClose: () => void; // Function to close the popup
}

// Functional component to display available tickets in a popup.
const AvailableTicketsPopup: React.FC<AvailableTicketsPopupProps> = ({
  ticketsData,
  onClose,
}) => {
  return (
    <div className="popup-overlay">
      {/* Main container for the popup */}
      <div className="popup-content">
        <h2>All The Tickets Available For Purchase</h2>
        {/* Scrollable container for ticket data to be displayed in it */}
        <div className="scrollable-div">
          {ticketsData && Object.keys(ticketsData).length > 0 ? (
            Object.entries(ticketsData).map(([event, count], index) => (
              <p key={index}>
                {event}: {count}
              </p>
            ))
          ) : (
            // If no tickets are available, show a fallback message
            <p>No tickets available in the system</p>
          )}
        </div>
        {/* Close button to close the popup */}
        <button className="close-button" onClick={onClose}>
          Close
        </button>
      </div>
    </div>
  );
};

export default AvailableTicketsPopup;
