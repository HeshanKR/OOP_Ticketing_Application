// Real-time ticketing system application : Heshan Ratnaweera | UOW: w2082289 | IIT: 20222094
import React, { ReactNode } from "react";

interface FullWhiteScreenProps {
  children: ReactNode; // Defines the type for child elements.
}

const FullWhiteScreen: React.FC<FullWhiteScreenProps> = ({ children }) => {
  return (
    <div className="fullscreen">
      {children} {/* Render the children passed to the component */}
    </div>
  );
};

export default FullWhiteScreen;
