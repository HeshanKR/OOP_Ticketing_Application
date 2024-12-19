// Real-time ticketing system application : Heshan Ratnaweera | UOW: w2082289 | IIT: 20222094
import React, { useState } from "react";
import apiClient from "../api";

// Define the props interface for SignUpVendorPopup component
interface SignUpVendorPopupProps {
  onClose: () => void; // Function to close the Sign-Up popup
  showNotification: (message: string, isError?: boolean) => void; // Function to show notifications with an optional error flag.
}

// Define the SignUpVendorPopup component.
const SignUpVendorPopup: React.FC<SignUpVendorPopupProps> = ({
  onClose,
  showNotification,
}) => {
  // State hooks for vendor ID, password, and loading status.
  const [vendorId, setVendorId] = useState("");
  const [password, setPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  // Function to handle vendor sign-up.
  const handleSignUp = async () => {
    // Validate the vendor ID (must be 4 letters followed by 3 digits).
    const vendorIdRegex = /^[A-Za-z]{4}\d{3}$/;
    if (!vendorIdRegex.test(vendorId)) {
      showNotification(
        "Vendor ID must be 4 letters followed by 3 digits.",
        true
      ); // Show error notification.
      return; // Exit if validation fails.
    }
    // Validate the password length (must be between 8 and 12 characters).
    if (password.length < 8 || password.length > 12) {
      showNotification("Password must be 8-12 characters long.", true);
      return; // Exit if validation fails.
    }

    // Set loading state to true to indicate an ongoing request.
    setIsLoading(true);
    try {
      // Make an API request to sign up the vendor.
      const response = await apiClient.post("/vendors/signup", {
        vendorId,
        password,
      });
      showNotification(response.data); // Show success notification with response data.
      onClose(); // Close the popup after successful sign-up.
    } catch (error: any) {
      const errorMessage =
        error.response?.data || "Failed to sign up. Please try again.";
      showNotification(errorMessage, true); // Show error notification.
    } finally {
      setIsLoading(false); // Set loading state to false after the API request completes.
    }
  };

  // Function to reset the input fields.
  const handleReset = () => {
    setVendorId("");
    setPassword("");
  };

  return (
    <div className="popup-container">
      <div className="popup-content ">
        <h2>Sign up as a Vendor</h2>
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
              value={vendorId}
              onChange={(e) => setVendorId(e.target.value)}
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
          {/*Form action buttons */}
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

export default SignUpVendorPopup;
