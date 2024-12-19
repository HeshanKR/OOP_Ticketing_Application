// Real-time ticketing system application : Heshan Ratnaweera | UOW: w2082289 | IIT: 20222094
import React, { useState } from "react";
import SignUpVendorPopup from "./SignUpVendorPopup";
import SignUpCustomerPopup from "./SignUpCustomerPopup";

interface SignUpPopupProps {
  onClose: () => void; // Function to close the Sign-Up Popup.
  showNotification: (message: string, isError?: boolean) => void; // Function to show notifications with optional error flag.
}

const SignUpPopup: React.FC<SignUpPopupProps> = ({
  onClose,
  showNotification,
}) => {
  // State variables to manage whether the Vendor or Customer sign-up popups are open.
  const [isVendorSignUpOpen, setIsVendorSignUpOpen] = useState(false);
  const [isCustomerSignUpOpen, setIsCustomerSignUpOpen] = useState(false);

  return (
    <>
      <div className="popup-container">
        <div className="popup">
          <h3>Sign Up</h3>
          <div className="popup-buttons">
            <button
              className="button"
              onClick={() => setIsVendorSignUpOpen(true)} // Open the Vendor sign-up popup
            >
              Sign up as Vendor
            </button>
            <button
              className="button"
              onClick={() => setIsCustomerSignUpOpen(true)}
            >
              Sign up as Customer
            </button>
            <button className="close-button" onClick={onClose}>
              Close
            </button>
          </div>
        </div>
      </div>
      {/* Conditionally render the Vendor Sign-Up popup if the state is true */}
      {isVendorSignUpOpen && (
        <SignUpVendorPopup
          onClose={() => setIsVendorSignUpOpen(false)} // Close the vendor sign-up popup
          showNotification={showNotification}
        />
      )}

      {isCustomerSignUpOpen && (
        <SignUpCustomerPopup
          onClose={() => setIsCustomerSignUpOpen(false)} // Close the Customer sign-up popup
          showNotification={showNotification}
        />
      )}
    </>
  );
};

export default SignUpPopup;
