# Real-Time Ticketing System Frontend : Heshan Ratnaweera | w2082289 | 20222094

A real-time ticketing system built using React, TypeScript, and Vite, featuring multiple user roles (Admin, Vendor, Customer) and providing functionalities such as ticket purchasing, ticket releasing, and configuration management. The system uses REST API for communication and WebSockets for real-time updates.

## Table of Contents

- [Features](#features)
- [Components](#components)
- [Prerequisites](#prerequisites)
- [Setup and Installation](#setup-and-installation)
- [Usage](#usage)
- [Code Overview](#code-overview)
- [Concurrency Considerations](#concurrency-considerations)
- [Error Handling](#error-handling)
- [Future Enhancements](#future-enhancements)
- [Contributing](#contributing)
- [Troubleshooting](#troubleshooting)
- [Acknowledgments](#acknowledgments)
- [License](#license)

## Features

- **Admin Configuration**: Set admin credentials, update system configuration, and view settings.

- **User Authentication**: Login and sign-up functionality for vendors and customers.

- **Real-Time Ticket Management**: Purchase and release tickets in real-time using REST API endpoint routing.

- **Role-Based Access**:

  - **Customer**: View all available tickets, purchase tickets, view all booked tickets and tickets booked by oneself, stop purchases.
  - **Vendor**: View all available tickets and available tickets released by oneself, release tickets, stop ticket releases.

- **Interactive Popups**: Various modals for different tasks like ticket management, viewing logs, and configuring settings.

- **WebSocket Integration**: Real-time updates for ticket data and logs.

## Components

- **Popups**: A set of modals for user authentication, ticket management, and system configuration.

- **TicketTable**: Real-time ticket data updates using WebSockets.

- **LogDisplay**: Displays log messages for ticket operations like booking and releasing tickets in real-time using websockets.

- **Menu Components**: Separate menus for Customer and Vendor, each offering specific functionalities like purchasing tickets, releasing tickets, etc.

## Prerequisites

Ensure the following are installed:

- **Node.js** (version 14 or higher) - [node.js](https://nodejs.org/en)
- **npm** (Node Package Manager) - [npm](https://docs.npmjs.com/getting-started)
- **Vite** (for building and running the application) - [Vite](https://vite.dev/)
- **Backend API and WebSocket Server** running at `http://localhost:8080` (or update base URLs accordingly).

To get instructions on how download and install Node.js with npm : [Click this](https://youtu.be/TZpH-qdaBg0?si=QyAS_BmJXtxostbZ)

To get instructions on how download and install Vite: [Click This](https://youtu.be/8vh5dmBaVQw?si=WGyD307psVkmImQY)

## Setup and Installation

To run the project locally, follow these steps:

1. Clone the repository:

```
git clone https://github.com/Heshan-Ratn/CW_OOP_w2082289.git
cd GUI_AND_BACKEND
cd Frontend
cd testfro
```

2. Install dependencies: Ensure you have Node.js and npm installed. Then, run:

```
npm install
```

If an error occurs try doing this:

- set your node.js folder path in environment variables by opening `Edit the system environment variables`. [watch video](https://youtu.be/uLPnqX9xnoA?si=4I-bdh2Sf5kNZ6Md)

- `Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass` use this to Bypass execution policy.

- Then try the command again.

3. Start the application: Run the development server using Vite:

```
npm run dev
```

This will start the app at `http://localhost:5173`.

4. **Backend API and WebSocket Server**: Ensure the backend server is running at `http://localhost:8080`.

## Usage

1. Open the application in your browser `(http://localhost:5173)`.

2. Set-up configuration setting if needed (previous setting are loaded on webpage load, default configuration loaded if database fails).

3. Admin can set system configuration settings, change admin credentials, view configuration settings, view available tickets, view booked tickets, stop all ticket operation, and resume all ticket operation in the system.

4. Sign in or sign up as a Vendor or Customer.

5. Vendors can release tickets, stop their ticket releases, view other available tickets, and view available tickets they have released.

6. Customers can purchase tickets, view their bookings, view other available tickets, and stop their purchases.

7. The system provides real-time updates on available tickets and logs through WebSockets.

## Code Overview

### Key Components:

- **App.tsx**: The main entry point of the application, routing users to appropriate components.

- **Popup Components**: Modular React components used for user authentication (Login, SignUp), ticket management, and viewing configuration settings.

- **WebSocket Service**: Manages real-time updates in the frontend with the server using SockJS and STOMP.

- **API Service**: Uses Axios to interact with the backend REST API for ticket and user management.

## Concurrency Considerations

- **Real-Time Data Handling**: WebSocket ensures that ticket data and logs are updated in real-time without manual refresh in the frontend.

- **Ticket Availability**: Handling concurrency during ticket purchasing by customers and ticket releasing by vendors. This system ensures proper synchronization via WebSocket updates and REST API endpoint.

## Error Handling

- **API Errors**: Errors from API calls are handled using Axios interceptors with user-friendly error messages.

- **WebSocket Errors**: WebSocket connection issues and message parsing errors are logged with specific error messages in console for easier debugging.

- **Form Validation**: Various popups (like login, sign-up, and ticket management forms) handle input validation, showing error messages for invalid inputs.

## Future Enhancements

- **Scalability**: Optimizing WebSocket handling to support large-scale deployments.

- **UI Enhancements**: Additional styling improvements and responsive design tweaks.

- **Unit Testing**: Implementing tests for key components and services using Jest and React Testing Library.

## Contributing

1. Fork the repository.
2. Create a new branch (git checkout -b feature/your-feature).
3. Make your changes.
4. Commit your changes (git commit -am 'Add new feature').
5. Push to your branch (git push origin feature/your-feature).
6. Create a pull request with a description of your changes.

## Troubleshooting

- **WebSocket Not Connecting**: Ensure the WebSocket server is running at `http://localhost:8080/ws`.
- **API Errors**: Check if the backend API is running at `http://localhost:8080/api`. If not, ensure it is properly started.
- **CORS Issues**: If facing cross-origin issues, configure the backend to allow requests from `http://localhost:5173` (the frontend).

## Acknowledgments

- **React**: For providing an easy-to-use, component-based UI framework.
- **Vite**: For enabling fast development and builds.
- **Axios & WebSocket**: For efficient API and real-time data handling.
- **SockJS & STOMP**: For WebSocket communication.
- **Stack Overflow**, **Chatgpt** and **GitHub** Community for troubleshooting tips.
- Special thanks to Module-Leader **Sir Guhanathan Poravi** for encouraging the development of advanced applications.
- Special thanks to **Programming with Mosh** and **Bro Code** Youtube channels for their amazing courses on React.

## License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details.
