// Real-time ticketing system application : Heshan Ratnaweera | UOW: w2082289 | IIT: 20222094
import React, { useState } from "react";
import apiClient from "../api";

interface StimulateTicketOperationPopupProps {
  onClose: () => void; // Callback to close the popup
  showNotification: (message: string, isError: boolean) => void; // Callback to show notification
}

const StimulateTicketOperationPopup: React.FC<
  StimulateTicketOperationPopupProps
> = ({ onClose, showNotification }) => {
  const [numberOfUsers, setNumberOfUsers] = useState<string>(""); // Input state
  const [error, setError] = useState<string | null>(null); // Validation error state

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    if (/^\d{0,3}$/.test(value)) {
      setNumberOfUsers(value); // Allow only numbers up to 3 digits
      setError(null); // Clear any existing errors
    } else {
      setError("Please enter a valid number between 0 and 999.");
    }
  };

  const handleSubmit = async () => {
    const number = parseInt(numberOfUsers, 10);
    if (isNaN(number) || number < 0 || number > 999) {
      setError("Please enter a valid number between 0 and 999.");
      return;
    }
    // Make the request to the backend
    try {
      // Send the request to the backend
      const response = await apiClient.post(
        `/simulation/start`,
        null, // No body, as parameters are sent via URL query
        { params: { numberOfUsers: number } } // Add query parameters
      );

      // Show success notification with response data
      showNotification(response.data, false); // Green notification
      onClose(); // Close the popup on success
    } catch (error: any) {
      // Handle errors and show appropriate notifications
      if (error.response?.data) {
        showNotification(error.response.data, true); // Red notification
      } else {
        showNotification("An error occurred. Please try again.", true);
      }
    }
  };

  const handleReset = () => {
    setNumberOfUsers(""); // Clear the input
    setError(null); // Clear errors
  };

  return (
    <div className="popup-container">
      <div className="popup-tickets">
        <h2>Stimulate Ticket Operation of Vendors and Customers</h2>
        <div className="form-group">
          <label htmlFor="numberOfUsers">
            Number of Vendors and Customers:
          </label>
          <input
            id="numberOfUsers"
            type="text"
            value={numberOfUsers}
            onChange={handleInputChange}
            placeholder="Enter a number (0-999)"
          />
          {error && <p className="error">{error}</p>}
        </div>
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

export default StimulateTicketOperationPopup;
