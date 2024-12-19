// Real-time ticketing system application : Heshan Ratnaweera | UOW: w2082289 | IIT: 20222094
import React, { useState } from "react";
import apiClient from "../api";

// Define the types of props that will be passed to this component.
interface SignUpCustomerPopupProps {
  onClose: () => void; // Function to close the sign-up popup.
  showNotification: (message: string, isError?: boolean) => void; // Function to display notifications (with optional error flag).
}

const SignUpCustomerPopup: React.FC<SignUpCustomerPopupProps> = ({
  onClose,
  showNotification,
}) => {
  // State to manage the customer ID, password, and loading status.
  const [customerId, setCustomerId] = useState("");
  const [password, setPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  // Function to handle customer sign-up.
  const handleSignUp = async () => {
    // Regular expression to validate the customer ID format (4 letters followed by 3 digits).
    const customerIdRegex = /^[A-Za-z]{4}\d{3}$/;
    // Validate the customer ID forma
    if (!customerIdRegex.test(customerId)) {
      showNotification(
        "Customer ID must be 4 letters followed by 3 digits.",
        true // Show error notification.
      );
      return;
    }
    // Validate the password length (must be between 8 and 12 characters)
    if (password.length < 8 || password.length > 12) {
      showNotification("Password must be 8-12 characters long.", true); // Show error notification.
      return;
    }

    // Set loading state to true while making the request.
    setIsLoading(true);
    try {
      // Send a POST request to the server to sign up the customer.
      const response = await apiClient.post("/customers/signup", {
        customerId,
        password,
      });
      showNotification(response.data); // Show success notification with the response data.
      onClose(); // Close popup on success
    } catch (error: any) {
      // In case of an error, display the error message or a fallback message.
      const errorMessage =
        error.response?.data || "Failed to sign up. Please try again.";
      showNotification(errorMessage, true); // Show error notification.
    } finally {
      // Set loading state back to false after the request completes (success or failure).
      setIsLoading(false);
    }
  };

  // Function to reset the form fields to their initial state.
  const handleReset = () => {
    setCustomerId("");
    setPassword("");
  };

  return (
    <div className="popup-container">
      <div className="popup-content ">
        <h2>Sign up as a Customer</h2>
        <form
          onSubmit={(e) => {
            e.preventDefault();
            handleSignUp();
          }}
        >
          <div>
            <label>Username (4 letters + 3 digits):</label>
            <input
              type="text"
              value={customerId}
              onChange={(e) => setCustomerId(e.target.value)}
              maxLength={7}
              required
            />
          </div>
          <div>
            <label>Password (8-12 characters):</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          {/* Container for action buttons*/}
          <div className="form-actions">
            <button className="button" type="submit" disabled={isLoading}>
              {isLoading ? "Signing Up..." : "Submit"}
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

export default SignUpCustomerPopup;
