// Real-time ticketing system application : Heshan Ratnaweera | UOW: w2082289 | IIT: 20222094
import React, { useEffect, useRef } from "react";

// Define the interface for the component props.
interface LogDisplayProps {
  messages: string[]; // Array of log messages to display.
}

// Functional component definition.
const LogDisplay: React.FC<LogDisplayProps> = ({ messages }) => {
  // Ref to track the end of the log list for smooth scrolling.
  const logEndRef = useRef<HTMLDivElement | null>(null);

  // Scroll to the bottom whenever the messages change.
  useEffect(() => {
    if (logEndRef.current) {
      logEndRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [messages]); // This effect runs whenever messages change.

  return (
    <div className="log-display">
      <ul>
        {messages.map((msg, index) => (
          <li key={index}>{msg}</li>
        ))}
      </ul>
      {/* Invisible element to ensure the view scrolls to the end */}
      <div ref={logEndRef}></div>
    </div>
  );
};

export default LogDisplay;
