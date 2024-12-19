// Real-time ticketing system application : Heshan Ratnaweera | UOW: w2082289 | IIT: 20222094

import axios from "axios";

// This Create a reusable instance of the Axios HTTP client with pre-configured settings
const apiClient = axios.create({
  baseURL: "http://localhost:8080/api", // Base URL for all API requests. Update this to the actual API endpoint in production.
});

export default apiClient; // Export the configured Axios instance for use throughout the application.

