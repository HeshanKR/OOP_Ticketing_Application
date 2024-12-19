//Real-Time Ticketing System CLI by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.

import java.util.Scanner;
import java.util.Set;

/**
 * This class represents a purchase request for tickets to a specific event.
 * Allows users (customers) to input event name and the number of tickets to book
 * while validating user inputs with retry logic.
 */
public class PurchaseRequest {
    private final String eventName; // Name of the event.
    private final int ticketsToBook; // Number of tickets to book.

    /**
     * This constructor constructs a PurchaseRequest with the specified event name and ticket quantity.
     *
     * @param eventName     the name of the event.
     * @param ticketsToBook the number of tickets to book.
     */
    public PurchaseRequest(String eventName, int ticketsToBook) {
        this.eventName = eventName;
        this.ticketsToBook = ticketsToBook;
    }

    // Getter methods.

    public String getEventName() {
        return eventName;
    }

    public int getTicketsToBook() {
        return ticketsToBook;
    }

    /**
     * This method prompts the user to create a purchase request by providing an event name
     * and the number of tickets to book. This also validates user input with retry logic.
     *
     * @param eventDetails a set of valid event names.
     * @return a PurchaseRequest object or null if the input is invalid after retries.
     */
    public static PurchaseRequest requestTickets(Set<String> eventDetails) {
        Scanner scanner = new Scanner(System.in);

        String eventName = promptForEventName(scanner, eventDetails);
        if (eventName == null) {
            System.out.println("Too many invalid attempts for event name, request canceled.");
            return null;

        }

        int ticketsToBook = promptForTicketQuantity(scanner);
        if (ticketsToBook == -1) {
            System.out.println("Too many invalid attempts for ticket quantity, request canceled.");
            return null;
        }

        System.out.println("Request successfully recorded: Event = " + eventName +
                ", Tickets to Book = " + ticketsToBook + "\n");

        return new PurchaseRequest(eventName, ticketsToBook);
    }

    /**
     * This method prompts the user to enter a valid event name from the given event details.
     * Retries up to 3 times if the input is invalid.
     *
     * @param scanner      the Scanner object for user input
     * @param eventDetails a set of valid event names
     * @return a valid event name or null if the user exceeds retry attempts
     */
    private static String promptForEventName(Scanner scanner, Set<String> eventDetails) {
        for (int attempts = 3; attempts > 0; attempts--) {
            System.out.print("Enter the event name: ");
            String inputEventName = scanner.nextLine().trim();

            if (eventDetails.contains(inputEventName)) {
                return inputEventName;
            } else {
                System.out.println("Invalid event name. Please choose from the following events:");
                eventDetails.forEach(System.out::println);
            }
        }
        return null;  // Return null if all attempts are exhausted.
    }


    /**
     * This method prompts the user to enter a valid ticket quantity.
     * Retries up to 3 times if the input is invalid.
     *
     * @param scanner the Scanner object for user input.
     * @return a valid ticket quantity or -1 if the user exceeds retry attempts.
     */
    private static int promptForTicketQuantity(Scanner scanner) {
        for (int attempts = 3; attempts > 0; attempts--) {
            System.out.print("Enter the number of tickets to book: ");
            try {
                int inputTicketsToBook = Integer.parseInt(scanner.nextLine().trim());

                if (inputTicketsToBook > 0) {
                    return inputTicketsToBook;
                } else {
                    System.out.println("Invalid quantity. Number of tickets must be greater than zero.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        return -1;  // Return -1 if all attempts are exhausted.
    }
}
