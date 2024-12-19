//Real-Time Ticketing System CLI by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class represents a ticket for an event with details such as ticket ID, event name, price,
 * time duration, date, vendor ID, ticket status, and customer ID.
 */
public class Ticket {
    private String ticketId;  // Unique identifier for the ticket.
    private String eventName; // Name of the event.
    private double price; // Price of the ticket.
    private String timeDuration; // Duration of the event.
    private String date;  // Date of the event.
    private String vendorId; // Vendor ID associated with the ticket.
    private String ticketStatus; // Status of the ticket ( Available / Booked ).
    private String customerId; // ID of the customer who purchased the ticket.

    // Using a single instance of Scanner
    private static Scanner scanner = new Scanner(System.in);

    /**
     * This constructor constructs a Ticket object with the specified details.
     *
     * @param ticketId     Unique identifier for the ticket.
     * @param eventName    Name of the event.
     * @param price        Price of the ticket.
     * @param timeDuration Duration of the event.
     * @param date         Date of the event (in YYYY-MM-DD format).
     * @param vendorId     Vendor ID associated with the ticket.
     * @param ticketStatus Status of the ticket ( Available / Booked ).
     * @param customerId   ID of the customer who purchased the ticket.
     */
    public Ticket(String ticketId, String eventName,
                  double price, String timeDuration,
                  String date, String vendorId,
                  String ticketStatus,
                  String customerId) {
        this.ticketId = ticketId;
        this.eventName = eventName;
        this.price = price;
        this.timeDuration = timeDuration;
        this.date = date;
        this.vendorId = vendorId;
        this.ticketStatus = ticketStatus;
        this.customerId = customerId;
    }

    //The Getters and Setters.

    public String getTicketId() {
        return ticketId;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public String getEventName() {
        return eventName;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getVendorId() {
        return vendorId;
    }

    public String getCustomerId() {
        return customerId;
    }


    /**
     * This Overload method creates a batch of tickets for a vendor.
     *
     * @param vendorId     Vendor ID associated with the tickets.
     * @param batchSize    Number of tickets to create.
     * @param eventName    Name of the event.
     * @param price        Price of each ticket.
     * @param timeDuration Duration of the event.
     * @param date         Date of the event (in YYYY-MM-DD format).
     * @return a list of tickets created for the vendor.
     */
    public static List<Ticket> createTicketsForVendor(String vendorId, int batchSize,
                                                      String eventName, double price,
                                                      String timeDuration, String date) {
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 1; i <= batchSize; i++) {
            String ticketId = vendorId + "-" + i;  // Generate a unique ticket ID
            tickets.add(new Ticket(ticketId, eventName, price, timeDuration, date, vendorId, "Available",
                    "Not Set"));
        }
        return tickets;
    }


    /**
     * This method creates a batch of tickets for a vendor based on user input.
     *
     * @param vendorId Vendor ID associated with the tickets.
     * @return a list of tickets created for the vendor.
     */
    public static List<Ticket> createTicketsForVendor(String vendorId) {
        // These methods are used to gather validated inputs.
        String eventName = getValidatedStringInput("Enter event name: ",
                "Event name cannot be empty.");
        double price = getValidatedDoubleInput("Enter ticket price: ",
                "Price must be greater than zero.");
        String timeDuration = getValidatedStringInput("Enter time duration (e.g., '2 hours'): ",
                "Time duration cannot be empty.");
        String date = getValidatedDateInput("Enter event date (e.g., '2024-11-10'): ");
        int batchSize = getValidatedIntInput("Enter batch size: ",
                "Batch size must be greater than zero.");

        // This is used to create a list of tickets for the vendor
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 1; i <= batchSize; i++) {
            String ticketId = vendorId + "-" + i;  // Generate a unique ticket ID
            tickets.add(new Ticket(ticketId, eventName, price, timeDuration, date, vendorId,
                    "Available", "Not Set"));
        }
        return tickets;
    }

    /**
     * This used to validate and gets a non-empty string inputs from the user.
     *
     * @param prompt       Message prompt for the user
     * @param errorMessage Error message to display for invalid input
     * @return a valid string input
     */
    private static String getValidatedStringInput(String prompt, String errorMessage) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;  // Return input if valid.
            }
            System.out.println(errorMessage);  // Print error message and retry.
        }
    }

    /**
     * This method is used to validate and get positive double input from the user.
     *
     * @param prompt       Message prompt for the user
     * @param errorMessage Error message to display for invalid input
     * @return a valid positive double input.
     */
    private static double getValidatedDoubleInput(String prompt, String errorMessage) {
        double input;
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextDouble()) {
                input = scanner.nextDouble();
                if (input > 0) {
                    scanner.nextLine(); // Consume newline left by nextDouble().
                    return input;  // Return input if valid.
                }
                System.out.println(errorMessage);  // Print error message and retry.
            } else {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.next();  // Consume invalid input.
            }
        }
    }


    /**
     * This method validates and gets a date input in the format 'YYYY-MM-DD'.
     *
     * @param prompt Message prompt for the user
     * @return a valid date input as a string
     */
    private static String getValidatedDateInput(String prompt) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (isValidDate(input)) {
                return input;  // Return input if valid.
            }
            System.out.println("Invalid date format. Please enter the date in the format 'YYYY-MM-DD'.");
        }
    }

    /**
     * This method checks if the given date is in a valid 'YYYY-MM-DD' format.
     *
     * @param date Date string to validate
     * @return true if the date is valid, false otherwise
     */
    private static boolean isValidDate(String date) {
        try {
            // Parse the date using ISO_LOCAL_DATE format (yyyy-MM-dd).
            LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
            return true;  // Valid format and logical date.
        } catch (DateTimeParseException e) {
            return false;  // Invalid date.
        }
    }

    /**
     * This method validates and gets a positive integer input from the user.
     *
     * @param prompt       Message prompt for the user
     * @param errorMessage Error message to display for invalid input
     * @return a valid positive integer input
     */
    private static int getValidatedIntInput(String prompt, String errorMessage) {
        int input = 0;
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                input = scanner.nextInt();
                if (input > 0) {
                    scanner.nextLine(); // Consume newline left by nextInt().
                    return input;  // Return input if valid.
                }
                System.out.println(errorMessage);  // Print error message and retry.
            } else {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.next();  // Consume invalid input.
            }
        }
    }
}
