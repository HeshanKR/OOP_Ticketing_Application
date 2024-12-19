// Real-time ticketing system application : Heshan Ratnaweera | UOW: w2082289 | IIT: 20222094
import React, { ReactNode } from "react";

interface SidePlanelDivProps {
  children?: ReactNode; // children is optional, can be any valid React node.
}

const SidePlanelDiv: React.FC<SidePlanelDivProps> = ({ children }) => {
  return <div className="side-planel">{children}</div>;
};

export default SidePlanelDiv;
