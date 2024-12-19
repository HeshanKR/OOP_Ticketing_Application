# Real-Time Ticketing System Backend : Heshan Ratnaweera | w2082289 | 20222094

## Overview

This folder contains the backend implementation of the Real-Time Ticketing System. The backend is built using Spring Boot and is integrated with a MySQL database for data storage and retrieval. It provides REST API endpoints for the frontend developed in React (using vite@4.1.0) and employs WebSockets for real-time updates.

## Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Setup and Installation](#setup-and-installation)
- [Usage](#usage)
- [REST API Endpoints](rest-api-endpoints)
- [Technologies used](technologies-used)
- [Future Enhancements](#future-enhancements)
- [Contributing](#contributing)
- [Troubleshooting](#troubleshooting)
- [Acknowledgments](#acknowledgments)
- [License](#license)

## Features

- **User Management**: Create and manage users such as customers and vendors.

- **Ticket Management**: Vendors can release tickets, and customers can purchase tickets.

- **Real-Time Updates**: Real-time ticket availability and purchase updates via WebSockets.

- **Data Persistence**: Data is stored in a MySQL database using JPA and Hibernate.

- **RESTful APIs**: Provides endpoints for frontend integration.

## Prerequisites

Ensure the following are installed before setting up the backend:

- Java 17 or higher

- Maven

- MySQL for your device : [Watch video to get started](https://youtu.be/Sfvpgu9ID2Q?si=e7rH7kAGTBwRduGR)

- Postman Desktop agent : This is needed for testing the REST API Endpoints: [postman agent download](https://www.postman.com/downloads/postman-agent/)

## Setup and Installation

1. Clone the repository:

```
git clone https://github.com/Heshan-Ratn/CW_OOP_w2082289.git
cd GUI_AND_BACKEND
cd Backend
cd ticketing_system
```

2. Configure the MySQL database:

- Create a MySQL database named `ticketing_system`.

```
CREATE DATABASE ticketing_system;
```

- Update the database credentials in `application.properties` located in the `src/main/resources` directory:

```
    spring.datasource.url=jdbc:mysql://localhost:3306/ticketing_system
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    spring.jpa.hibernate.ddl-auto=update
```

3. Build and run the application:

```
    mvn spring-boot:run
```

4. Access the application:

- API Base URL: `http://localhost:8080`

## Usage

### REST API Endpoint

| Method | Endpoint                                                | Description                                                                                                                         |
| ------ | ------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------- |
| POST   | `/api/admin/stop-all-activity`                          | To Stop all ticket operations in the system.                                                                                        |
| POST   | `/api/admin/stop-all-activity`                          | To Resume all ticket operations in the system.                                                                                      |
| GET    | `/api/configuration/view-configuration`                 | To View Configuration settings.                                                                                                     |
| PUT    | `/api/configuration/update-admin-credentials`           | To update Admin credentials in the system.                                                                                          |
| PUT    | `/api/configuration/update-ticket-settings`             | To update configuration setting of the system.                                                                                      |
| GET    | `/api/ticket-pool/available-tickets/vendor/{vendorId}`  | To view the Ticket with "Available" status released by a certain vendor grouped by events.                                          |
| GET    | `/api/ticket-pool/available-tickets/event`              | To view all Available tickets count for each event.                                                                                 |
| GET    | `/api/ticket-pool/booked-tickets/event`                 | To view all Booked tickets count for each event.                                                                                    |
| GET    | `/api/ticket-pool/booked-tickets/customer/{customerId}` | To view the Ticket with "Booked" status released by a certain customer grouped by events.                                           |
| GET    | `/api/tickets/all`                                      | To view all tickets stored in the database table `ticketpool`.                                                                      |
| GET    | `/api/tickets/generate`                                 | To generate a List of TicketEntities by providing some details.                                                                     |
| POST   | `/api/purchases/create`                                 | To generate a Purchase Request to book tickets by providing some details.                                                           |
| POST   | `/api/simulation/start-vendor`                          | To generate vendors up to a certain number specified and start ticket Releases.                                                     |
| POST   | `/api/simulation/start-customer`                        | To generate customers up to a certain number specified and start ticket purchases.                                                  |
| POST   | `/api/simulation/start`                                 | To generate vendors and customers up to a certain number specified and start ticket Releases and ticket Purchases at the same time. |
| POST   | `/api/customers/signup`                                 | To sign up new customers to the system.                                                                                             |
| POST   | `/api/customers/signin`                                 | To sign in customers to the system.                                                                                                 |
| POST   | `/api/customers/{customerId}/start-thread`              | To start threads for ticket purchase for a certain customer.                                                                        |
| POST   | `/api/customers/{customerId}/stop-thread`               | To stop threads for ticket purchase for a certain customer.                                                                         |
| POST   | `/api/vendors/signup`                                   | To Sign up vendors to the system.                                                                                                   |
| POST   | `/api/vendors/signin`                                   | To Sign in vendors to the system.                                                                                                   |
| POST   | `/api/vendors/{vendorId}/start-thread`                  | To start threads for ticket release for a certain vendor.                                                                           |
| POST   | `/api/vendors/{vendorId}/stop-thread`                   | To stop threads for ticket release for a certain vendor.                                                                            |

The REST API Endpoint were tested through `postman` and proven to be fully functional.

## Technologies Used

- **Spring Boot 3.4.0**: Backend framework

- **MySQL**: Relational database management

- **Spring WebSocket**: Real-time communication

- **Spring Data JPA**: Database interaction

- **Slf4j**: Logging

- **Lombok**: Boilerplate code reduction

- **postman**: Used for REST API Endpoint testing.

## Troubleshooting

1. Database Connection Issues:

- Ensure MySQL is running and accessible.

- Verify the `application.properties` file for correct credentials.

2. Port Conflicts:

- If port 8080 is already in use, modify the server.port property in application.properties.

```
server.port=your_preferred_port
```

- However, changing this url would impact the frontend connection, if you do wish to change then other configurations of the frontend file must be adjusted to work with the new port.

3. Dependency Issues:

- Run `mvn clean install` to rebuild dependencies.

4. WebSocket Connection Fails:

- Ensure the WebSocket URL matches the server's WebSocket endpoint.

## Future Enhancements

- **Enhanced Analytics Dashboard**: Provide vendors with insights into ticket sales and customer behavior.
- **Payment Gateway Integration**: Enable secure online payments.
- **Multi-Language Support**: Add support for multiple languages.

## Contributing

Contributions are welcome! To contribute:

1. Fork the repository.

2. Create a feature branch: `git checkout -b feature-name`

3. Commit your changes: `git commit -m 'Add some feature'`

4. Push to the branch: `git push origin feature-name`

5. Open a pull request.

## Acknowledgments

- **Spring Boot Documentation**: [Spring Boot Reference](https://spring.io/projects/spring-boot)

- **MySQL Documentation**: [MySQL Official Docs](https://dev.mysql.com/doc/)

- **Stack Overflow**, **Chatgpt** and **GitHub** Community for troubleshooting tips.

- Special thanks to Module-Leader **Sir Guhanathan Poravi** for encouraging the development of advanced applications.

- Special thanks to Module-Leader **Amigoscode** and **FreeCodeCamp** for the guidance provided in developing spring boot applications.

## License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details.
