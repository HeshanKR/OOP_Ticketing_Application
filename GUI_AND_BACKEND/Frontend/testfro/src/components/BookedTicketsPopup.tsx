// Real-time ticketing system application : Heshan Ratnaweera | UOW: w2082289 | IIT: 20222094
import React from "react";

// Define the interface for the props that the BookedTicketsPopup component.
interface BookedTicketsPopupProps {
  ticketsData: Record<string, number> | null; // Booked tickets data as key-value pairs.
  onClose: () => void; // Function to close the popup.
}

// The BookedTicketsPopup functional component.
const BookedTicketsPopup: React.FC<BookedTicketsPopupProps> = ({
  ticketsData,
  onClose,
}) => {
  return (
    <div className="popup-overlay">
      <div className="popup-content">
        <h2>All Booked Tickets Stored in the System</h2>
        {/* Scrollable div to display the ticket records if they are present */}
        <div className="scrollable-div">
          {ticketsData && Object.keys(ticketsData).length > 0 ? (
            Object.entries(ticketsData).map(([event, count], index) => (
              <p key={index}>
                {event}: {count}
              </p>
            ))
          ) : (
            // Fallback message if no ticket records are available.
            <p>There are no booked ticket records in the system yet.</p>
          )}
        </div>
        {/*close button for closing the window. */}
        <button className="close-button" onClick={onClose}>
          Close
        </button>
      </div>
    </div>
  );
};

export default BookedTicketsPopup;
