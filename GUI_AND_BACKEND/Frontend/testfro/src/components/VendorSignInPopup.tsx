// Real-time ticketing system application : Heshan Ratnaweera | UOW: w2082289 | IIT: 20222094
import React, { useState } from "react";
import apiClient from "../api";
import VendorMenu from "./VendorMenu";

// Props interface for the VendorSignInPopup component.
interface VendorSignInPopupProps {
  onClose: () => void; // Callback to handle popup closure.
  showNotification: (message: string, isError?: boolean) => void; // Function to show notifications, optionally marking as an error.
}

// Functional component for Vendor Sign-In Popup.
const VendorSignInPopup: React.FC<VendorSignInPopupProps> = ({
  onClose,
  showNotification,
}) => {
  // State variables to manage vendor credentials and menu visibility.
  const [vendorId, setVendorId] = useState(""); // Vendor ID input value.
  const [password, setPassword] = useState(""); // Password input value.
  const [showVendorMenu, setShowVendorMenu] = useState(false); // Determines if VendorMenu should be displayed.

  // Validates the Vendor ID and Password inputs.
  const validateInputs = (): boolean => {
    const vendorIdRegex = /^[A-Za-z]{4}[0-9]{3}$/; // Format: 4 letters followed by 3 digits.
    const passwordRegex = /^.{8,12}$/; // Password length: 8-12 characters.

    // Validate if vendor ID is correct according to the format.
    if (!vendorIdRegex.test(vendorId)) {
      showNotification(
        "Invalid Vendor ID: Must be 4 letters followed by 3 digits.",
        true
      );
      return false;
    }
    // Validate if password is correct according to the format.
    if (!passwordRegex.test(password)) {
      showNotification(
        "Invalid Password: Must be between 8-12 characters.",
        true
      );
      return false;
    }
    return true;
  };

  // Handles the sign-in process.
  const handleSubmit = async () => {
    if (!validateInputs()) return; // Return early if inputs are invalid.

    try {
      //Make an API request to sign in vendors.
      const response = await apiClient.post("/vendors/signin", {
        vendorId,
        password,
      });

      if (response.data.success) {
        const extractedVendorId = response.data.data.vendorId; // Extract vendorId from response.
        setVendorId(extractedVendorId); // Set vendorId state.
        showNotification(response.data.message, false); // Show success notification.
        setShowVendorMenu(true); // Open the Vendor Menu.
      } else {
        showNotification(response.data.message, true); // Show error notification.
      }
    } catch (error: any) {
      if (
        error.response &&
        error.response.data &&
        error.response.data.message
      ) {
        showNotification(error.response.data.message, true);
      } else {
        showNotification("Error: Unable to sign in. Please try again.", true);
      }
    }
  };

  return (
    <>
      <div className="popup-container">
        <div className="popup">
          <h3>Sign In as Vendor</h3>
          <div className="form-group">
            <label>Username: </label>
            <input
              type="text"
              value={vendorId}
              onChange={(e) => setVendorId(e.target.value)}
              maxLength={7}
            />
          </div>
          <div className="form-group">
            <label>Password: </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              maxLength={12}
            />
          </div>
          <div className="popup-buttons">
            <button className="button" onClick={handleSubmit}>
              Submit
            </button>
            <button
              className="button"
              onClick={() => {
                setVendorId("");
                setPassword("");
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

      {/* Render VendorMenu if sign-in is successful */}
      {showVendorMenu && (
        <VendorMenu
          vendorId={vendorId}
          onClose={() => setShowVendorMenu(false)} // Handle menu closure
          showNotification={showNotification}
        />
      )}
    </>
  );
};

export default VendorSignInPopup;
