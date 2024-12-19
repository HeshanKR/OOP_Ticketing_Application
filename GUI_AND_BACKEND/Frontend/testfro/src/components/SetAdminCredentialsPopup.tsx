// Real-time ticketing system application : Heshan Ratnaweera | UOW: w2082289 | IIT: 20222094
import React, { useState } from "react";
import apiClient from "../api";

interface SetAdminCredentialsPopupProps {
  onClose: () => void; // Callback function to close the popup.
  showNotification: (message: string, isError: boolean) => void; // Function to show notifications with error flag.
}

const SetAdminCredentialsPopup: React.FC<SetAdminCredentialsPopupProps> = ({
  onClose,
  showNotification,
}) => {
  // State to store form data for admin credentials, including old and new admin credentials.
  const [formData, setFormData] = useState({
    oldConfigAdminUser: "",
    oldConfigAdminPassword: "",
    newConfigAdminUser: "",
    newConfigAdminPassword: "",
  });

  // Handle form input changes and update state based on input field.
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value.slice(0, 50), // Limit input to 50 characters
    }));
  };

  // Reset the form data to initial empty state.
  const handleReset = () => {
    setFormData({
      oldConfigAdminUser: "",
      oldConfigAdminPassword: "",
      newConfigAdminUser: "",
      newConfigAdminPassword: "",
    });
  };

  // Handle form submission to send the updated admin credentials to the backend.
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault(); // Prevent default form submission behavior.
    // Check if all form fields are filled.
    if (
      !formData.oldConfigAdminUser ||
      !formData.oldConfigAdminPassword ||
      !formData.newConfigAdminUser ||
      !formData.newConfigAdminPassword
    ) {
      showNotification("All fields are required.", true); // Show error notification if fields are empty.
      return;
    }

    // Send a PUT request to update the admin credentials in the backend.
    try {
      const response = await apiClient.put(
        "/configuration/update-admin-credentials", // API endpoint to update credentials.
        formData // Send the form data as the payload.
      );
      showNotification(`Success: ${response.data}`, false); // Show success notification on successful update.
      onClose(); // Close the pop-up on success
    } catch (error: any) {
      console.error("Error updating admin credentials:", error); // Log the error to console.
      // Show an error notification if the update fails.
      showNotification(
        error.response?.data || "Failed to update admin credentials",
        true // Flag the notification as an error.
      );
    }
  };

  return (
    <div className="popup-overlay">
      <div className="popup-content">
        <h2>Set Admin Credentials</h2>
        <form onSubmit={handleSubmit}>
          <div>
            <label>Old Admin Username:</label>
            <input
              type="text"
              name="oldConfigAdminUser"
              value={formData.oldConfigAdminUser}
              onChange={handleChange}
              required
            />
          </div>
          <div>
            <label>Old Admin Password:</label>
            <input
              type="password"
              name="oldConfigAdminPassword"
              value={formData.oldConfigAdminPassword}
              onChange={handleChange}
              required
            />
          </div>
          <div>
            <label>New Admin Username:</label>
            <input
              type="text"
              name="newConfigAdminUser"
              value={formData.newConfigAdminUser}
              onChange={handleChange}
              required
            />
          </div>
          <div>
            <label>New Admin Password:</label>
            <input
              type="password"
              name="newConfigAdminPassword"
              value={formData.newConfigAdminPassword}
              onChange={handleChange}
              required
            />
          </div>
          {/* Form action buttons */}
          <div className="form-actions">
            <button className="button" type="submit">
              Submit
            </button>
            <button className="button" type="button" onClick={handleReset}>
              Reset
            </button>
            <button className="button-close" type="button" onClick={onClose}>
              Close
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default SetAdminCredentialsPopup;
