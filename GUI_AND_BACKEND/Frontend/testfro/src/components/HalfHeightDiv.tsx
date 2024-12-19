// Real-time ticketing system application : Heshan Ratnaweera | UOW: w2082289 | IIT: 20222094
import React, { ReactNode } from "react";

interface HalfHeightDivProps {
  children?: ReactNode; // children is optional, can be any valid React node.
}

const HalfHeightDiv: React.FC<HalfHeightDivProps> = ({ children }) => {
  // The component renders a div with a class name of 'half-height' and passes children inside it.
  return <div className="half-height">{children}</div>;
};

export default HalfHeightDiv;
