// Real-time ticketing system application : Heshan Ratnaweera | UOW: w2082289 | IIT: 20222094
import React from "react";

// Define the interface for the component props.
interface HalfWidthDivProps {
  buttonTexts?: string[]; // Optional prop for button texts
  onButtonClick: (buttonText: string) => void; // Function to handle button clicks
}

// Functional component definition.
const HalfWidthDiv: React.FC<HalfWidthDivProps> = ({
  buttonTexts,
  onButtonClick,
}) => {
  return (
    <div className="half-width">
      {buttonTexts ? (
        buttonTexts.map((text, index) => (
          <button
            key={index}
            className={text === "Exit Program" ? "button-close" : "button"}
            onClick={() => onButtonClick(text)} // Pass the button text to the handler
          >
            {text}
          </button>
        ))
      ) : (
        <p>No buttons here!</p>
      )}
    </div>
  );
};

export default HalfWidthDiv;
