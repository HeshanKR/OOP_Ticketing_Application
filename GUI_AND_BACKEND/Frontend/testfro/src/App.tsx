// Real-time ticketing system application : Heshan Ratnaweera | UOW: w2082289 | IIT: 20222094

import React, { useEffect, useRef, useState } from "react";
import { connectWebSocket } from "./websocket";
import apiClient from "./api";
import "./index.css";
import "./App.css";
import FullWhiteScreen from "./components/Fullwhitescreen";
import HalfHeightDiv from "./components/HalfHeightDiv";
import HalfWidthDiv from "./components/HalfWidthDiv";
import SidePlanelDiv from "./components/SidePlanelDiv";
import PopUpConfigWindow from "./components/PopUpConfigWindow";
import SetAdminCredentialsPopup from "./components/SetAdminCredentialsPopup";
import EditConfigurationSettingsPopup from "./components/EditConfigurationSettingsPopup";
import AvailableTicketsPopup from "./components/AvailableTicketsPopup";
import BookedTicketsPopup from "./components/BookedTicketsPopup";
import LoginPopup from "./components/LoginPopup";
import SignUpPopup from "./components/SignUpPopup";
import SignInPopup from "./components/SignInPopup";
import StimulateTicketOperationPopup from "./components/StimulateTicketOperationPopup";
import TicketTable from "./components/TicketTable";
import LogDisplay from "./components/LogDisplay";
import { Client } from "@stomp/stompjs";

//This is the interface defining the shape of a ticket object.
interface Ticket {
  ticketId: number;
  eventName: string;
  price: number;
  timeDuration: string;
  date: string;
  vendorId: string;
  ticketStatus: string;
  customerId?: string | null;
}

const App: React.FC = () => {
  // Button texts for dynamically added buttons.
  const buttonTexts = [
    "Configure Settings",
    "Login",
    "View All Available Tickets",
    "View All Booked Tickets",
    "Start System",
    "Stop System",
    "Stimulate Ticket Operations",
    "Exit Program",
  ];

  // State variables for managing UI state and data of the application.
  const [isPopupOpen, setIsPopupOpen] = useState(false); // Pop-up for configuration settings.
  const [isAdminPopupOpen, setIsAdminPopupOpen] = useState(false); // Pop-up for admin credentials.
  const [isConfigEditPopupOpen, setIsConfigEditPopupOpen] = useState(false); // Pop-up for editing configurations.
  const [isConfigViewPopupOpen, setIsConfigViewPopupOpen] = useState(false); // Pop-up for viewing configurations.
  const [configData, setConfigData] = useState<any>(null); // State for holding configuration data.
  const [notification, setNotification] = useState<string | null>(null); // State for global notifications messages.
  const [notificationClass, setNotificationClass] = useState<string | null>(
    null
  ); // CSS class for notifications (error/success)

  const [isAvailableTicketsPopupOpen, setIsAvailableTicketsPopupOpen] =
    useState(false); // Pop-up for available tickets window.
  const [availableTicketsData, setAvailableTicketsData] = useState<Record<
    string,
    number
  > | null>(null);

  const [isBookedTicketsPopupOpen, setIsBookedTicketsPopupOpen] =
    useState(false); // Pop-up for booked tickets window
  const [bookedTicketsData, setBookedTicketsData] = useState<Record<
    string,
    number
  > | null>(null);

  const [tickets, setTickets] = useState<Ticket[]>([]); // Ticket data displayed in the table.
  const [logMessages, setLogMessages] = useState<string[]>([]); // Log messages received via WebSocket.
  const webSocketRef = useRef<Client | null>(null); // Reference to the WebSocket client.

  const [isStimulatePopupOpen, setIsStimulatePopupOpen] = useState(false); // Pop-up for simulate vendors and customers window

  const [isLoginPopupOpen, setIsLoginPopupOpen] = useState(false); // State for Login Popup
  const [isSignUpPopupOpen, setIsSignUpPopupOpen] = useState(false); // State for Sign-Up Popup
  const [isSignInPopupOpen, setIsSignInPopupOpen] = useState(false); // State for Sign-In Popup

  // Functions for opening and closing pop-ups windows.
  const handleOpenPopup = () => setIsPopupOpen(true);
  const handleClosePopup = () => setIsPopupOpen(false);

  const handleOpenAdminPopup = () => setIsAdminPopupOpen(true);
  const handleCloseAdminPopup = () => setIsAdminPopupOpen(false);

  const handleOpenConfigEditPopup = () => setIsConfigEditPopupOpen(true);
  const handleCloseConfigEditPopup = () => setIsConfigEditPopupOpen(false);

  const handleCloseAvailableTicketsPopup = () => {
    setIsAvailableTicketsPopupOpen(false);
  };

  const handleCloseBookedTicketsPopup = () => {
    setIsBookedTicketsPopupOpen(false);
  };

  const handleCloseConfigViewPopup = () => setIsConfigViewPopupOpen(false);

  const handleOpenLoginPopup = () => setIsLoginPopupOpen(true); // Open Login Popup
  const handleCloseLoginPopup = () => setIsLoginPopupOpen(false); // Close Login Popup

  const handleOpenSignUpPopup = () => {
    setIsLoginPopupOpen(false); // Hide Login Popup
    setIsSignUpPopupOpen(true); // Show Sign-Up Popup
  };

  const handleCloseSignUpPopup = () => {
    setIsSignUpPopupOpen(false); // Hide Sign-Up Popup
    setIsLoginPopupOpen(true); // Restore Login Popup
  };
  const handleOpenSignInPopup = () => {
    setIsLoginPopupOpen(false); // Hide Login Popup
    setIsSignInPopupOpen(true); // Show Sign-In Popup
  };

  const handleCloseSignInPopup = () => {
    setIsSignInPopupOpen(false); // Hide Sign-In Popup
    setIsLoginPopupOpen(true); // Restore Login Popup
  };

  const handleOpenStimulatePopup = () => setIsStimulatePopupOpen(true);
  const handleCloseStimulatePopup = () => setIsStimulatePopupOpen(false);

  //Function that displayed the configuration setting to the client.
  const handleOpenConfigViewPopup = () => {
    setIsConfigViewPopupOpen(true);
    // Fetch configuration data when the "View Configuration Settings" pop-up opens
    apiClient
      .get("/configuration/view-configuration")
      .then((response) => {
        setConfigData(response.data); // Set the fetched config data in state
      })
      .catch((error) => {
        console.error("Error fetching configuration:", error);
        showNotification("Failed to load configuration data.", true); // Show error notification if data could not be fetched.
      });
  };

  // function that handle how available tickets are displayed to the client.
  const handleOpenAvailableTicketsPopup = () => {
    setIsAvailableTicketsPopupOpen(true);
    // Fetch data about "Available" tickets in the system.
    apiClient
      .get("/ticket-pool/available-tickets/event")
      .then((response) => {
        setAvailableTicketsData(response.data); // Update state with the fetched data.
      })
      .catch((error) => {
        console.error("Error fetching available tickets:", error);
        setAvailableTicketsData(null); // Reset the data in case of an error
      });
  };

  //function that displays booked tickets in the system to the clients.
  const handleOpenBookedTicketsPopup = () => {
    setIsBookedTicketsPopupOpen(true);
    // Fetch data about "Booked" tickets in the system.
    apiClient
      .get("/ticket-pool/booked-tickets/event") // Endpoint for booked tickets.
      .then((response) => {
        setBookedTicketsData(response.data); // Update state with the fetched data.
      })
      .catch((error) => {
        console.error("Error fetching booked tickets:", error);
        setBookedTicketsData(null); // Reset the data in case of an error
      });
  };

  //function to handle notifications showed to clients.
  const showNotification = (message: string, isError: boolean = false) => {
    setNotification(message);

    // Apply conditional class based on isError value
    const newNotificationClass = isError ? "error" : "success";
    setNotificationClass(newNotificationClass); // Update notification class

    // Clear notification after 5 seconds
    setTimeout(() => {
      setNotification(null);
      setNotificationClass(null); // Reset class after timeout
    }, 5000);
  };

  //function that handles the admin stop of the system.
  const handleStopSystem = () => {
    apiClient
      .post("/admin/stop-all-activity")
      .then((response) => {
        console.log(response.data);
        showNotification(
          "All ticket operations have been stopped, click the 'Start System' button to start the ticket operations again."
        );
      })
      .catch((error) => {
        console.error("Error stopping the system:", error);
        showNotification("Failed to stop the system. Please try again.", true);
      });
  };

  //function that handles the admin resume of the system.
  const handleStartSystem = () => {
    apiClient
      .post("/admin/resume-all-activity")
      .then((response) => {
        console.log(response.data);
        showNotification("All ticket operations have been resumed.");
      })
      .catch((error) => {
        console.error("Error resuming the system:", error);
        showNotification(
          "Failed to resume the system. Please try again.",
          true
        );
      });
  };
  // WebSocket and ticket data fetching and log messages.
  useEffect(() => {
    //fetching real-time ticket pool data using this function.
    const fetchTickets = async () => {
      try {
        const response = await apiClient.get("/tickets/all");
        if (response.data.length === 0) {
          setTickets([]);
          showNotification("No tickets available in the pool.");
        } else {
          setTickets(response.data);
        }
      } catch (error) {
        console.error("Error fetching tickets:", error);
        showNotification("Failed to load tickets.", true);
      }
    };

    fetchTickets();

    // Connect WebSocket only if not already connected
    if (!webSocketRef.current) {
      webSocketRef.current = connectWebSocket(
        (newTickets: Ticket[]) => {
          setTickets(newTickets);
        },
        (logMessage: string) => {
          console.log("Received log message:", logMessage); // debug log message.
          //setting the log messages display data.
          setLogMessages((prevMessages) => {
            if (!prevMessages.includes(logMessage)) {
              return [...prevMessages, logMessage];
            }
            return prevMessages;
          });
        }
      );
    }

    return () => {
      // Cleanup WebSocket connection on component unmount
      if (webSocketRef.current) {
        webSocketRef.current.deactivate();
        webSocketRef.current = null;
      }
    };
  }, []);

  return (
    <div className="App app-container" style={{ minWidth: "514px" }}>
      <FullWhiteScreen>
        <HalfWidthDiv
          buttonTexts={buttonTexts}
          onButtonClick={(buttonText) => {
            if (buttonText === "Configure Settings") handleOpenPopup();
            if (buttonText === "View All Available Tickets")
              handleOpenAvailableTicketsPopup();
            if (buttonText === "View All Booked Tickets")
              handleOpenBookedTicketsPopup();
            if (buttonText === "Stop System") handleStopSystem();
            if (buttonText === "Start System") handleStartSystem();
            if (buttonText === "Login") handleOpenLoginPopup();
            if (buttonText === "Stimulate Ticket Operations")
              handleOpenStimulatePopup();
            if (buttonText === "Exit Program") {
              // Attempt to close the window
              const canClose = window.confirm(
                "Are you sure you want to exit the program? This will close the webpage."
              );
              if (canClose) {
                window.close();
              }
            }
          }}
        />

        <SidePlanelDiv>
          <HalfHeightDiv>
            <LogDisplay messages={logMessages} />
          </HalfHeightDiv>
          <HalfHeightDiv>
            <TicketTable tickets={tickets} />
          </HalfHeightDiv>
        </SidePlanelDiv>
      </FullWhiteScreen>

      {/* Pop-up components for different functionalities */}

      {/* Added tickets popup */}
      {isAvailableTicketsPopupOpen && (
        <AvailableTicketsPopup
          ticketsData={availableTicketsData}
          onClose={handleCloseAvailableTicketsPopup}
        />
      )}
      {/* Booked Tickets popup */}
      {isBookedTicketsPopupOpen && (
        <BookedTicketsPopup
          ticketsData={bookedTicketsData}
          onClose={handleCloseBookedTicketsPopup}
        />
      )}
      {/* Main popup for configuration feature*/}
      {isPopupOpen && (
        <PopUpConfigWindow
          onClose={handleClosePopup}
          onSetAdminCredentials={handleOpenAdminPopup}
          onEditConfigSetting={handleOpenConfigEditPopup}
          onViewConfigSettings={handleOpenConfigViewPopup}
        />
      )}

      {/* Edit admin credentials popup */}
      {isAdminPopupOpen && (
        <SetAdminCredentialsPopup
          onClose={handleCloseAdminPopup}
          showNotification={showNotification}
        />
      )}
      {/* Configuration edit popup */}
      {isConfigEditPopupOpen && (
        <EditConfigurationSettingsPopup
          onClose={handleCloseConfigEditPopup}
          showNotification={showNotification}
        />
      )}

      {/* Configuration view popup */}
      {isConfigViewPopupOpen && (
        <PopUpConfigWindow
          onClose={handleCloseConfigViewPopup}
          text="View Configuration Settings"
          configData={configData} // Only pass configData to the view configuration pop-up
        />
      )}

      {/* Login popup */}
      {isLoginPopupOpen && (
        <LoginPopup
          onSignUp={handleOpenSignUpPopup}
          onSignIn={handleOpenSignInPopup}
          onClose={handleCloseLoginPopup}
        />
      )}

      {/* Signup popup */}
      {isSignUpPopupOpen && (
        <SignUpPopup
          onClose={handleCloseSignUpPopup}
          showNotification={showNotification}
        />
      )}

      {/* SignIn popup */}
      {isSignInPopupOpen && (
        <SignInPopup
          onClose={handleCloseSignInPopup}
          showNotification={showNotification}
        />
      )}

      {/* Global notification bar */}
      {notification && (
        <div className={`notification-bar ${notificationClass}`}>
          {notification}
        </div>
      )}

      {/* simulation pop-up */}
      {isStimulatePopupOpen && (
        <StimulateTicketOperationPopup
          onClose={handleCloseStimulatePopup}
          showNotification={showNotification}
        />
      )}
    </div>
  );
};

export default App;
