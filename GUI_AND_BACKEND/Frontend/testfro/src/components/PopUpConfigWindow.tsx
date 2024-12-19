// Real-time ticketing system application : Heshan Ratnaweera | UOW: w2082289 | IIT: 20222094
import React from "react";

interface PopUpConfigWindowProps {
  onClose: () => void; // Function to close the pop-up window
  onSetAdminCredentials?: () => void; // Function to open the admin credentials pop-up
  onEditConfigSetting?: () => void; // Function to open the edit config setting pop-up
  onViewConfigSettings?: () => void; // Function to open the view config setting pop-up
  text?: string; // Text to display in the pop-up window (for other popups)
  configData?: any; // Configuration data to display in the view config pop-up
}

const PopUpConfigWindow: React.FC<PopUpConfigWindowProps> = ({
  onClose,
  onSetAdminCredentials,
  onEditConfigSetting,
  onViewConfigSettings,
  text,
  configData,
}) => {
  const buttonTexts = [
    "Set Admin Credentials",
    "Edit Configuration Settings",
    "View Configuration Settings",
  ];

  return (
    <div className="popup-overlay">
      <div className="popup-content">
        <h2>{text || "Configure Settings"}</h2>
        {onSetAdminCredentials && (
          <button onClick={onSetAdminCredentials} className="button">
            {buttonTexts[0]}
          </button>
        )}
        {onEditConfigSetting && (
          <button onClick={onEditConfigSetting} className="button">
            {buttonTexts[1]}
          </button>
        )}
        {onViewConfigSettings && (
          <button onClick={onViewConfigSettings} className="button">
            {buttonTexts[2]}
          </button>
        )}

        {/* Only display config data in the view config pop-up */}
        {configData && (
          <div>
            <h3>Configuration Settings</h3>
            <p>Total Available Tickets: {configData.totalAvailableTickets}</p>
            <p>Ticket Release Rate: {configData.ticketReleaseRate}</p>
            <p>Customer Retrieval Rate: {configData.customerRetrievalRate}</p>
            <p>Max Ticket Capacity: {configData.maxTicketCapacity}</p>
          </div>
        )}

        <button onClick={onClose} className="button-close">
          Close
        </button>
      </div>
    </div>
  );
};

export default PopUpConfigWindow;
