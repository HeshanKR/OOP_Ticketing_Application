// Real-time ticketing system application : Heshan Ratnaweera | UOW: w2082289 | IIT: 20222094
import React, { useState } from "react";
import apiClient from "../api";

interface PurchaseTicketsPopupProps {
  customerId: string; // Customer Id is passed as a prop.
  onClose: () => void; // Callback to handle popup closure.
  showNotification: (message: string, isError?: boolean) => void; // Function to display notifications.
}

const PurchaseTicketsPopup: React.FC<PurchaseTicketsPopupProps> = ({
  customerId,
  onClose,
  showNotification,
}) => {
  const [eventName, setEventName] = useState(""); // Holds the event name input.
  const [ticketsToBook, setTicketsToBook] = useState<number | "">(""); // Holds the number of tickets to book.

  // Reset function to clear form inputs.
  const handleReset = () => {
    setEventName("");
    setTicketsToBook("");
  };

  // Submit function to send the booking request.
  const handleSubmit = async () => {
    //Validates the event name.
    if (!eventName || ticketsToBook === "") {
      showNotification("Please fill in all fields before submitting.", true);
      return;
    }
    //validates the number of tickets to book.
    if (ticketsToBook < 0 || ticketsToBook > 100) {
      showNotification("Tickets to book must be between 0 and 100.", true);
      return;
    }

    //Map values to API payload to be submitted for request.
    const payload = {
      eventName,
      ticketToBook: ticketsToBook,
    };

    try {
      const response = await apiClient.post(
        `/customers/${customerId}/start-thread`, // Send data to backend API
        payload
      );
      showNotification(response.data, false); // Green notification for success
      onClose(); // Close the popup on success
    } catch (error: any) {
      const errorMessage =
        error.response?.data || "Failed to purchase tickets. Please try again.";
      showNotification(errorMessage, true); // Red notification for failure
    }
  };

  return (
    <div className="popup-container">
      <div className="popup-tickets">
        <h2>Start Purchasing New Tickets from System</h2>
        <div className="form-group-tickets">
          <label>Event Name: </label>
          <input
            type="text"
            value={eventName}
            onChange={(e) => setEventName(e.target.value)}
            placeholder="Enter event name"
          />
        </div>
        <div className="form-group-tickets">
          <label>Total Tickets to Book (0-100): </label>
          <input
            type="number"
            value={ticketsToBook}
            onChange={(e) => setTicketsToBook(Number(e.target.value) || "")}
            placeholder="Enter total tickets"
            min="0"
            max="100"
          />
        </div>
        {/* Buttons for submit, reset, and close actions */}
        <div className="popup-buttons">
          <button className="button" onClick={handleSubmit}>
            Submit
          </button>
          <button className="button" onClick={handleReset}>
            Reset
          </button>
          <button className="close-button" onClick={onClose}>
            Close
          </button>
        </div>
      </div>
    </div>
  );
};

export default PurchaseTicketsPopup;
