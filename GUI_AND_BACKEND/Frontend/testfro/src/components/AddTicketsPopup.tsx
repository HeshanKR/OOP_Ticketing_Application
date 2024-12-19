// Real-time ticketing system application : Heshan Ratnaweera | UOW: w2082289 | IIT: 20222094

import React, { useState } from "react";
import apiClient from "../api";

// Props interface for the AddTicketsPopup component
interface AddTicketsPopupProps {
  vendorId: string; // ID of the vendor
  onClose: () => void; // Callback to close the popup
  showNotification: (message: string, isError?: boolean) => void; // Function to display notifications
}

const AddTicketsPopup: React.FC<AddTicketsPopupProps> = ({
  vendorId,
  onClose,
  showNotification,
}) => {
  // State variables to manage form inputs
  const [eventName, setEventName] = useState("");
  const [price, setPrice] = useState<number | "">("");
  const [timeDuration, setTimeDuration] = useState("");
  const [date, setDate] = useState("");
  const [batchSize, setBatchSize] = useState<number | "">("");

  // A helper function to validate user inputs. Ensures all fields meet specific criteria before submission.
  const validateInputs = (): boolean => {
    if (!eventName || eventName.length > 50) {
      showNotification("Event Name must be between 1 and 50 characters.", true);
      return false;
    }
    if (!timeDuration || timeDuration.length > 50) {
      showNotification(
        "Time Duration must be between 1 and 50 characters.",
        true
      );
      return false;
    }
    if (
      !price ||
      !/^\d{1,8}(\.\d{1,2})?$/.test(price.toString()) ||
      parseFloat(price.toString()) <= 0
    ) {
      showNotification(
        "Price must be a positive number with up to 8 digits and 2 decimal places.",
        true
      );
      return false;
    }
    if (!date || new Date(date) < new Date("2024-01-01")) {
      showNotification("Date must be from January 1, 2024, onwards.", true);
      return false;
    }
    if (!batchSize || batchSize < 1 || batchSize > 100) {
      showNotification("Batch Size must be a number between 1 and 100.", true);
      return false;
    }
    return true;
  };

  // Handles form submission to create tickets by validating and sending data to the API
  const handleSubmit = async () => {
    if (!validateInputs()) return; // Abort if validation fails

    //Map values to API payload to be submitted for request
    const payload = {
      event_Name: eventName,
      price: parseFloat(price.toString()), // Ensure it's a floating-point value
      time_Duration: timeDuration,
      date: date,
      batch_Size: batchSize,
    };

    try {
      const response = await apiClient.post(
        `/vendors/${vendorId}/start-thread`, // API endpoint for ticket creation and release of tickets.
        payload
      );
      showNotification(response.data, false); // Green notification
      onClose(); // Close popup on success
    } catch (error: any) {
      if (error.response?.data) {
        showNotification(error.response.data, true); // Red notification
      } else {
        showNotification("An error occurred. Please try again.", true);
      }
    }
  };

  // Resets all form fields to their initial stat
  const handleReset = () => {
    setEventName("");
    setPrice("");
    setTimeDuration("");
    setDate("");
    setBatchSize("");
  };

  return (
    <div className="popup-container">
      <div className="popup-tickets">
        <h3>Start Releasing Tickets To System</h3>
        {/* Form input for event name */}
        <div className="form-group-tickets">
          <label>Event Name: </label>
          <input
            type="text"
            value={eventName}
            maxLength={50}
            onChange={(e) => setEventName(e.target.value)}
          />
        </div>
        {/* Form input for price */}
        <div className="form-group-tickets">
          <label>Price: </label>
          <input
            type="number"
            value={price}
            onChange={(e) => setPrice(Number(e.target.value))}
            placeholder="Max 8 digits, 2 decimals"
          />
        </div>
        {/* Form input for time duration */}
        <div className="form-group-tickets">
          <label>Time Duration: </label>
          <input
            type="text"
            value={timeDuration}
            maxLength={50}
            onChange={(e) => setTimeDuration(e.target.value)}
          />
        </div>
        {/* Form input for date */}
        <div className="form-group-tickets">
          <label>Date: </label>
          <input
            type="date"
            value={date}
            onChange={(e) => setDate(e.target.value)}
          />
        </div>
        {/* Form input for batch size */}
        <div className="form-group-tickets">
          <label>Ticket Batch Size: </label>
          <input
            type="number"
            value={batchSize}
            onChange={(e) => setBatchSize(Number(e.target.value))}
            placeholder="1-100"
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

export default AddTicketsPopup;
