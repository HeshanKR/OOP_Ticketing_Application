# Real-Time Ticketing System CLI : Heshan Ratnaweera | w2082289 | 20222094

## Overview

This is a Real-Time Ticketing System built to simulate the purchase and release of tickets in a multi-threaded environment. It is designed to allow multiple customers to request tickets simultaneously, while vendors release tickets into the pool concurrently. The system uses threads to simulate real-time operations, demonstrating concurrent ticket sales and releases in an efficient and realistic manner.

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

- **Ticket Pool**: A shared pool where tickets are stored, which can be accessed by customers for purchase and vendors for release of tickets.
- **Customer Simulation**: Simulates multiple customers purchasing tickets concurrently. Customers request tickets, and the system manages ticket booking in real-time.

- **Vendor Simulation**: Simulates multiple vendors releasing tickets into the pool at a specified rate. Vendors add batches of tickets at regular intervals.

- **Multithreading** : Both customers and vendors operate in separate threads, allowing simultaneous ticket purchasing and releasing.

- **Real-Time Ticketing** : The system mimics real-time processes, where customers request tickets while vendors continuously release tickets into the pool.

## Components

- **TicketPool**: Manages the shared pool of tickets. Customers retrieve tickets from here, and vendors release tickets into it.
- **Customer**: Represents a customer attempting to purchase tickets. Each customer runs in a separate thread to simulate concurrent ticket purchases.
- **Vendor**: Represents a vendor releasing tickets into the ticket pool. Vendors also run in separate threads to simulate concurrent ticket release.
- **StimulatePurchase**: Simulates customer purchases by creating customer threads, assigning purchase requests, and managing concurrent ticket requests.
- **StimulateRelease**: Simulates vendor ticket releases by creating vendor threads that release tickets into the pool at a specified rate.
- **PurchaseRequest**: Represents a request made by a customer to purchase a specific number of tickets for a particular event.
- **Ticket**: Represents an individual ticket with properties like ticket type, price, event, and release date.

## Prerequisites

- **Java**: The system is developed in Java and requires JDK 22 or later for compilation and execution.
- **IDE**: Any Java-compatible Integrated Development Environment (IDE) such as IntelliJ IDEA, Eclipse, or NetBeans.
- **Git Bash**: For executing git commands effortlessly, [Video to install git Bash](https://youtu.be/7BOrUHFu44A?si=KdPgQ3YHGRl2_3kp)
- **Gson** (for JSON parsing)
- **log4j** (for logging)

Before running the application, make sure to have the following external libraries downloaded and added to your project:

1. **Gson**: A library for working with JSON in Java.

   - Download from: [Gson GitHub Repository](https://github.com/google/gson)
   - Alternatively, you can download the jar file from Maven Central: [Gson on Maven Central](https://search.maven.org/artifact/com.google.code.gson/gson)
   - For Alternative support follow this video: [Click this](https://youtu.be/Qc9EfiepfWs?si=XeKcG9zxtKe7rlWp.)

2. **Log4j**: A library for logging purposes.
   - Download from: [Log4j GitHub Repository](https://github.com/apache/logging-log4j2)
   - Alternatively, you can download the jar file from Maven Central: [Log4j on Maven Central](https://search.maven.org/artifact/org.apache.logging.log4j/log4j-api)
   - For Alternative support follow this video: [Click this](https://youtu.be/gTG913LEzhY?si=m9zPsBHLDXfTBIGD)

To add these libraries to your project, follow the steps below based on your IDE:

- **For IntelliJ IDEA**: Right-click on `your project` > Open `Project Structure` > `Libraries` > `Add External JARs`
- **For Eclipse**: Right-click on `your project` > `Properties` > `Java Build Path` > `Add External JARs`

## Setup and Installation

### Step 1: Clone the Repository

Clone the repository to your local machine using the following command:

```
git clone https://github.com/Heshan-Ratn/CW_OOP_w2082289.git
```

### Step 2: Add all dependencies

Make sure all required libraries are added to your libraries in the project structure

you will need to add the `GSON` and `Log4j` libraries. In the [Prerequisites](#prerequisites) section follow the alternative support instructions to download and use both libraries.

### Step 2: Compile the Code

Navigate to the project directory and compile the Java files using your preferred method (via IDE or command line). If using the command line, you can use the javac command:

```
javac *.java
```

### Step 3: Run the Application

Run the main class to start the simulation. The main class will simulate ticket purchase and release in real-time. You can run the application through the command line or directly from your IDE.

This the command for Command Line:

```
java Main
```

## Usage

Once the application is running, the following will happen:

1. Ticket Release:

- Vendors will release tickets into the shared TicketPool at the rate defined in the StimulateRelease class.
- Each vendor runs in its own thread, releasing tickets concurrently.

2. Ticket Purchase:

- Customers will request tickets from the pool based on their purchase rate. Each customer runs in its own thread and requests a set number of tickets.
- If a customer successfully retrieves tickets, the system processes their purchase and updates the pool accordingly.

3. Multiple Simultaneous Operations:

- The system supports simultaneous operations, where multiple customers can try to purchase tickets and vendors can release tickets concurrently.

## Code Overview

- **TicketPool Class**: Handles ticket storage and provides synchronized methods for retrieving and adding tickets to ensure thread safety.
- **Customer Class**: Simulates customer ticket purchase behavior. It uses threads to simulate concurrent purchases from the pool. Tickets are purchased at specified intervals.
- **Vendor Class**: Simulates vendor ticket release behavior. It releases tickets into the pool at specified intervals.
- **StimulatePurchase Class**: Starts the customer ticket purchase simulation. It creates customer threads, assigns purchase requests, and starts the threads.
- **StimulateRelease Class**: Starts the vendor ticket release simulation. It creates vendor threads, assigns ticket batches, and starts the threads.

## Concurrency Considerations

The system is designed to handle concurrent operations using threads. Customer purchase requests and vendor ticket releases are processed in parallel to simulate real-time operations.
Proper synchronization techniques are used within the TicketPool class to avoid race conditions and ensure thread safety when accessing the shared ticket pool.

## Error Handling

- Interruptions in the threads (e.g., InterruptedException) are handled by throwing runtime exceptions to maintain the flow of the simulation.
- Further error handling can be added to improve robustness, such as retrying failed purchases or ticket releases.

## Future Enhancements

- **Dynamic Vendor and Customer Creation**: Allow dynamic configuration of the number of vendors and customers, based on user input.
- **Improved Ticket Handling**: Implement better error handling and retries when customers fail to retrieve tickets.

## Contributing

If you'd like to contribute to the development of this project, follow these steps:

1. **Fork the repository**: Click on the "Fork" button on the top-right corner of the repository page to create your own copy of the project.
2. **Clone your fork**: Clone your forked repository to your local machine using the command:

```
   git clone https://github.com/Heshan-Ratn/CW_OOP_w2082289.git
```

3. **Create a new branch**: Create a new branch for your changes:

```
   git checkout -b your-feature-branch
```

4. **Make changes**: Make the necessary changes or add features to the project.

5. **Commit your changes**: Commit your changes with a descriptive message:

```
   git commit -m "Add feature/bugfix/updates"
```

6. **Push to your fork**: Push the changes to your forked repository:

```
   git push origin your-feature-branch
```

7. **Create a pull request**: Once your changes are ready, go to the GitHub page for your fork and create a pull request to the main repository.

Please ensure that your contributions align with the project's purpose, follow good coding practices, and include relevant tests.

## Troubleshooting

If you encounter any issues while setting up or using the Real-Time Ticketing System, here are some common problems and their solutions:

### 1. **Missing Dependencies (Gson, log4j, etc.)**

**Problem:** The application requires external libraries like Gson and log4j, but these libraries are not automatically downloaded.

**Solution:**  
Ensure that all required external libraries are included in the **External Libraries** section of your project. If these libraries are missing, follow these steps:

1. Download the necessary libraries:
   - Gson
   - log4j
2. Manually add them to your project’s classpath or include them in the **External Libraries** section in your IDE.

### 2. **ClassNotFoundException or NoClassDefFoundError**

**Problem:** You might encounter a `ClassNotFoundException` or `NoClassDefFoundError` for classes like `Gson` or `log4j`.

**Solution:**  
This issue occurs if a required dependency is missing or not linked correctly. To resolve it:

- Double-check that the necessary libraries (Gson, log4j) are added to the project.
- If you're using an IDE, confirm that the libraries appear in the **External Libraries** section.
- Ensure that the dependencies are in the correct directory (e.g., `lib` folder) and are included in your classpath.

### 3. **Thread Synchronization Issues**

**Problem:** Customers and vendors may not be correctly synchronized, leading to race conditions or unexpected behavior during ticket purchases or releases.

**Solution:**  
If you notice threads conflicting or misbehaving, ensure proper synchronization in the `TicketPool` class. This can be done by:

- Using synchronized methods or blocks when accessing shared resources (like the ticket pool).
- Using concurrency utilities like `Locks` (e.g., `ReentrantLock`) if needed to manage access to shared resources more efficiently.

### 4. **Application Crashes or Unexpected Termination**

**Problem:** The application may crash or terminate unexpectedly during the simulation of purchases or ticket releases.

**Solution:**

- Check the logs or console for any exceptions or error messages. Common issues might relate to thread exceptions or memory overloads.
- If the crash happens during `multi-threaded` operations, verify that all threads are properly handled and that you’re managing resources correctly.

### 5. **Issues with Ticket Purchases and Releases**

**Problem:** The ticket purchase or release simulations may not be working as expected.

**Solution:**

- Verify that each `Customer` and `Vendor` has been properly configured with a `TicketPool` and a `PurchaseRequest`.
- Ensure that the `TicketPool` class can handle concurrent operations correctly.
- Confirm that each thread (customer or vendor) is properly initiated and is interacting with the `TicketPool` and other objects as intended.

### 6. **log4j Logging Issues**

**Problem:** If logs aren’t showing up or are incorrectly formatted, there might be an issue with the log4j configuration.

**Solution:**

- Ensure that your `log4j.properties` (or `log4j.xml`) file is correctly set up in the root directory.
- Make sure that the log level is properly configured for your needs (e.g., `INFO`, `ERROR`, `DEBUG`).
- Confirm that the output destination (console, file) is correctly specified.

### 7. **Performance Issues (Slow Execution)**

**Problem:** The system may be running slower than expected, especially if many customers or vendors are simulated.

**Solution:**

- If your application is lagging or experiencing performance issues, you may need to optimize thread management. Consider reducing the number of simultaneous customers or vendors to test performance in smaller chunks.
- Review the usage of synchronized blocks or locks to ensure that threads aren't waiting unnecessarily.

### 8. **Missing LICENSE File**

**Problem:** If you receive a warning or error regarding the absence of a `LICENSE` file, this may be due to a missing file in the project.

**Solution:**  
Ensure that the `LICENSE` file is located in the root directory of your project and contains the correct license information. If you're using the MIT License, ensure the contents of the LICENSE file follow the standard MIT License text.

## Acknowledgments

- **Gson** for JSON serialization/deserialization: [Gson GitHub](https://github.com/google/gson)
- **log4j** for logging: [log4j GitHub](https://github.com/apache/logging-log4j2)
- **Stack Overflow**, **ChatGPT**, and **GitHub** for troubleshooting and examples.
- Special thanks to Module Leader **Sir Guhanathan Poravi** for providing the opportunity to build a comprehensive real-time application.
- Special thanks to **Miss Buddhini Samarakkody**, LinkedIn course instructor, for her guidance on multithreading concepts through the LinkedIn course on Java threads.
- Special thanks to the **Coding with John** YouTube channel for expanding my knowledge on Java multithreading.
- Special thanks to **Defog Tech** YouTube channel for their guidance on how to implement the producer-consumer pattern.

## License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details.
