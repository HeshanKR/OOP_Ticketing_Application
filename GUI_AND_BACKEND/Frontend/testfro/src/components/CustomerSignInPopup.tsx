// Real-time ticketing system application : Heshan Ratnaweera | UOW: w2082289 | IIT: 20222094
import React, { useState } from "react";
import apiClient from "../api";
import CustomerMenu from "./CustomerMenu";

// Interface for the props passed to CustomerSignInPopup component.
interface CustomerSignInPopupProps {
  onClose: () => void; // Callback to close the sign-in popup.
  showNotification: (message: string, isError?: boolean) => void; // Function to show notifications (success/error).
}

const CustomerSignInPopup: React.FC<CustomerSignInPopupProps> = ({
  onClose,
  showNotification,
}) => {
  // State variables to manage the form inputs and control the visibility of the customer menu
  const [customerId, setCustomerId] = useState("");
  const [password, setPassword] = useState("");
  const [showCustomerMenu, setShowCustomerMenu] = useState(false);

  // Function to validate inputs for customer ID and password.
  const validateInputs = (): boolean => {
    const customerIdRegex = /^[A-Za-z]{4}[0-9]{3}$/; // Customer ID format: 4 letters + 3 digits
    const passwordRegex = /^.{8,12}$/; // Password length: 8-12 characters

    // Check if customer ID matches the regex pattern.
    if (!customerIdRegex.test(customerId)) {
      showNotification(
        "Invalid Customer ID: Must be 4 letters followed by 3 digits.",
        true
      );
      return false;
    }
    // Check if password matches the regex pattern.
    if (!passwordRegex.test(password)) {
      showNotification(
        "Invalid Password: Must be between 8-12 characters.",
        true
      );
      return false;
    }
    return true;
  };

  // Function to handle form submission (sign-in process).
  const handleSubmit = async () => {
    // Validate inputs before proceeding with the API request.
    if (!validateInputs()) return;

    try {
      // Send a POST request to sign in the customer with the entered customer ID and password.
      const response = await apiClient.post("/customers/signin", {
        customerId,
        password,
      });

      // If sign-in is successful.
      if (response.data.success) {
        const extractedCustomerId = response.data.data.customerId; // Extract customerId from response.
        setCustomerId(extractedCustomerId); // Update the customerId state.
        showNotification(response.data.message, false); // Display success notification.
        setShowCustomerMenu(true); // Display the CustomerMenu after successful sign-in.
      } else {
        // If sign-in fails, display error message.
        showNotification(response.data.message, true);
      }
    } catch (error: any) {
      // Handle API or network errors.
      if (
        error.response &&
        error.response.data &&
        error.response.data.message
      ) {
        showNotification(error.response.data.message, true); // Display error message from API response.
      } else {
        showNotification("Error: Unable to sign in. Please try again.", true); // Generic error message.
      }
    }
  };

  return (
    <>
      <div className="popup-container">
        <div className="popup">
          <h3>Sign In as Customer</h3>
          {/* Input fields for customer ID and password */}
          <div className="form-group">
            <label>Username: </label>
            <input
              type="text"
              value={customerId}
              onChange={(e) => setCustomerId(e.target.value)}
              maxLength={7} // Limit input length to 7 characters (4 letters + 3 digits).
            />
          </div>
          <div className="form-group">
            <label>Password: </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              maxLength={12} // Limit input length to 12 characters.
            />
          </div>
          {/* Buttons for form submission, reset, and closing the popup */}
          <div className="popup-buttons">
            <button className="button" onClick={handleSubmit}>
              Submit
            </button>
            <button
              className="button"
              onClick={() => {
                setCustomerId(""); // Reset customerId state.
                setPassword(""); // Reset password state.
              }}
            >
              Reset
            </button>
            <button className="close-button" onClick={onClose}>
              Close
            </button>
          </div>
        </div>
      </div>

      {/* Conditionally render the CustomerMenu if sign-in is successful */}
      {showCustomerMenu && (
        <CustomerMenu
          customerId={customerId} // Pass the customerId to CustomerMenu.
          onClose={() => setShowCustomerMenu(false)} // Handle menu closure.
          showNotification={showNotification} // Pass showNotification function.
        />
      )}
    </>
  );
};

export default CustomerSignInPopup;
