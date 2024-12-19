// Real-time ticketing system application : Heshan Ratnaweera | UOW: w2082289 | IIT: 20222094
import React from "react";

interface LoginPopupProps {
  onSignUp: () => void; // Function to open the Sign-Up Popup
  onSignIn: () => void; // Function to open the Sign-In Popup
  onClose: () => void; // Function to close the Login Popup
}

const LoginPopup: React.FC<LoginPopupProps> = ({
  onSignUp,
  onSignIn,
  onClose,
}) => {
  return (
    <div className="popup-container">
      <div className="popup">
        <h3>Login</h3>
        <div className="popup-buttons">
          <button className="button" onClick={onSignUp}>
            Sign Up
          </button>
          <button className="button" onClick={onSignIn}>
            Sign In
          </button>
          <button className="close-button" onClick={onClose}>
            Close
          </button>
        </div>
      </div>
    </div>
  );
};

export default LoginPopup;
