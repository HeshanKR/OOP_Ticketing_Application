// Real-time ticketing system application : Heshan Ratnaweera | UOW: w2082289 | IIT: 20222094

import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

//This is the URL of the WebSocket server endpoint
const SOCKET_URL = 'http://localhost:8080/ws';


/**
 * Establishes a WebSocket connection using the STOMP protocol.
 * 
 * @param onTicketUpdate Callback to handle ticket updates received from the server.
 * @param onLogMessage Callback to handle log messages received from the server.
 * @returns The STOMP client instance, allowing further control (e.g., disconnect).
 */
export const connectWebSocket = (
  onTicketUpdate: (data: any) => void,
  onLogMessage: (data: any) => void
) => {
   //This Createa a new STOMP client with configuration options.
  const client = new Client({
    brokerURL: SOCKET_URL, // WebSocket broker URL (used when SockJS is not required).
    connectHeaders: {}, // Custom headers sent during the WebSocket handshake
    debug: (str) => console.log(str), // Debug callback for STOMP messages.
    reconnectDelay: 5000, // Reconnection delay in milliseconds after disconnection.
    webSocketFactory: () => new SockJS(SOCKET_URL), // Use SockJS for compatibility with browsers that lack native WebSocket support
    onConnect: () => {
      console.log('Connected to WebSocket');

      // Subscribe to the /topic/ticketpool for ticket updates
      client.subscribe('/topic/ticketpool', (message) => {
        try {
          const ticketData = JSON.parse(message.body);
          onTicketUpdate(ticketData);  // Callback for ticket update
        } catch (error) {
          console.error('Error parsing WebSocket message for ticket pool:', error);
        }
      });

      // Subscribe to the /topic/logs for log messages
      client.subscribe('/topic/logs', (message) => {
        try {
          // Assuming the log message is a plain text string                 
          const logMessage = message.body; 
          onLogMessage(logMessage);  // Invoke the log message callback
        } catch (error) {
          console.error('Error processing WebSocket message for logs:', error);
        }
      });
    },
    onDisconnect: () => console.log('Disconnected from WebSocket'), // Callback triggered on disconnection.
    onStompError: (frame) => {
      console.error('STOMP error: ', frame); // Handle protocol-level STOMP errors
    },
  });

  // This activate the client to establish the connection
  client.activate();
  return client; // Return the client instance for external management
};

