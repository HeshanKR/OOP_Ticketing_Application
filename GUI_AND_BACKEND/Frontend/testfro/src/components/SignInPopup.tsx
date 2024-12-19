// Real-time ticketing system application : Heshan Ratnaweera | UOW: w2082289 | IIT: 20222094
import React, { useState } from "react";
import CustomerSignInPopup from "./CustomerSignInPopup";
import VendorSignInPopup from "./VendorSignInPopup";

// Define the types of props that will be passed to this component.
interface SignInPopupProps {
  onClose: () => void; // Callback function to close the sign-in popup.
  showNotification: (message: string, isError?: boolean) => void; // Function to display notifications (with optional error flag)
}

const SignInPopup: React.FC<SignInPopupProps> = ({
  onClose,
  showNotification,
}) => {
  // State to manage whether to show the Customer or Vendor sign-in popup.
  const [showCustomerSignIn, setShowCustomerSignIn] = useState(false);
  const [showVendorSignIn, setShowVendorSignIn] = useState(false);

  return (
    <div className="popup-container">
      <div className="popup">
        <h3>Sign In</h3>
        <div className="popup-buttons">
          <button className="button" onClick={() => setShowVendorSignIn(true)}>
            Sign in as Vendor
          </button>
          <button
            className="button"
            onClick={() => setShowCustomerSignIn(true)}
          >
            Sign in as Customer
          </button>
          <button className="close-button" onClick={onClose}>
            Close
          </button>
        </div>
      </div>
      {/* Conditionally render CustomerSignInPopup if the showCustomerSignIn state is true */}

      {showCustomerSignIn && (
        <CustomerSignInPopup
          onClose={() => setShowCustomerSignIn(false)}
          showNotification={showNotification}
        />
      )}

      {showVendorSignIn && (
        <VendorSignInPopup
          onClose={() => setShowVendorSignIn(false)}
          showNotification={showNotification}
        />
      )}
    </div>
  );
};

export default SignInPopup;
