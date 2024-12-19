//Real-Time Ticketing System CLI by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.

import com.google.gson.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This is the configuration management class for handling ticketing system settings.
 * Includes functionality for setting admin credentials, updating ticket-related configurations, displaying
 * configuration setting, and persisting/loading data to/from JSON files.
 */
public class Configuration {

    //Configuration class attributes
    private static final ReentrantLock lock = new ReentrantLock(); //Lock to maintain synchronized operations.
    private long totalAvailableTickets; // The total number of tickets with ticket status as "Available".
    private double ticketReleaseRate; // Rate of ticket release in milliseconds.
    private double customerRetrievalRate;// Rate of ticket retrieval in milliseconds.
    private long maxTicketCapacity; // Max capacity of available tickets in the shared ticket pool.
    private String configAdminUser; // Username of system admin.
    private String configAdminPassword; //password of system admin

    /**
     * Default constructor initializes configuration with default values.
     */
    public Configuration() {
        this(5000, 7000, 200); // Calls the parameterized constructor.
    }

    /**
     * Parameterized constructor to initialize configuration with specified values.
     *
     * @param ticketReleaseRate     Initial ticket release rate (in ms).
     * @param customerRetrievalRate Initial customer retrieval rate (in ms).
     * @param maxTicketCapacity     Initial maximum ticket capacity.
     */
    public Configuration(double ticketReleaseRate, double customerRetrievalRate, long maxTicketCapacity) {
        this.totalAvailableTickets = loadTotalAvailableTickets();
        this.ticketReleaseRate = Math.max(ticketReleaseRate, 0);
        this.customerRetrievalRate = Math.max(customerRetrievalRate, 0);
        this.maxTicketCapacity = Math.max(maxTicketCapacity, totalAvailableTickets);
        this.configAdminUser = "AdminSuper";
        this.configAdminPassword = "heshanplus1";
    }

    /**
     * @return The current ticket release rate (in ms).
     */
    public double getTicketReleaseRate() {
        return ticketReleaseRate;
    }

    /**
     * @return The current customer retrieval rate (in ms).
     */
    public double getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    /**
     * @return The maximum ticket capacity.
     */
    public long getMaxTicketCapacity() {
        return maxTicketCapacity;
    }

    /**
     * This method updates the admin credentials after validating the current credentials.
     * Prompts the user for current credentials and new credentials.
     */
    public void setAdminCredentials() {
        lock.lock();
        try {
            String[] currentCredentials = promptUserForCredentials("Enter current admin username: ",
                    "Enter current admin password: ");
            if (authenticate(currentCredentials[0], currentCredentials[1])) {
                System.out.println("Authentication successful! Enter new admin credentials:");
                String[] newCredentials = promptUserForCredentials("Enter new admin username: ",
                        "Enter new admin password: ");
                this.configAdminUser = newCredentials[0];
                this.configAdminPassword = newCredentials[1];
                saveConfiguration("Config.json");
                System.out.println("Admin credentials updated successfully.\n");
            } else {
                System.out.println("Incorrect credentials. Unable to update admin details.\n");
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * This method is used to authenticate the admin credentials.
     *
     * @param username The username to authenticate.
     * @param password The password to authenticate.
     * @return true if the credentials match, false otherwise.
     */
    public boolean authenticate(String username, String password) {
        lock.lock();
        try {
            return this.configAdminUser.equals(username) && this.configAdminPassword.equals(password);
        } finally {
            lock.unlock();
        }
    }

    /**
     * This method prompts the user for credentials.
     *
     * @param usernamePrompt The prompt for entering username.
     * @param passwordPrompt The prompt for entering password.
     * @return An array containing the entered username and password.
     */
    public String[] promptUserForCredentials(String usernamePrompt, String passwordPrompt) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(usernamePrompt);
        String username = scanner.nextLine();
        System.out.print(passwordPrompt);
        String password = scanner.nextLine();
        return new String[]{username, password};
    }

    /**
     * This prompts the user to update the configuration settings after authentication.
     */
    public void promptUserForConfigurationUpdate() {
        lock.lock();
        try {
            Scanner scanner = new Scanner(System.in);

            String[] credentials = promptUserForCredentials("Enter admin username: ",
                    "Enter admin password: ");

            if (authenticate(credentials[0], credentials[1])) {
                System.out.println("Authentication successful! Enter new configuration values.");

                // Ask for configuration settings with exception handling for invalid data types
                double ticketReleaseRate = getValidDoubleInput(scanner,
                        "Enter new ticket release rate (in ms): ");
                double customerRetrievalRate = getValidDoubleInput(scanner,
                        "Enter new customer retrieval rate (in ms): ");
                long maxTicketCapacity = getValidLongInput(scanner, "Enter new max ticket capacity: ");

                // Update configuration with the user's inputs
                updateConfiguration(ticketReleaseRate, customerRetrievalRate, maxTicketCapacity);
            } else {
                System.out.println("Incorrect credentials. Configuration update denied.\n");
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * This method prompts the user for a valid double input.
     *
     * @param scanner The scanner to read input.
     * @param prompt  The prompt to display to the user.
     * @return A valid double value entered by the user.
     */
    private double getValidDoubleInput(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return scanner.nextDouble();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a valid number.");
                scanner.nextLine();
            }
        }
    }

    /**
     * This method prompts the user for a valid long input.
     *
     * @param scanner The scanner to read input.
     * @param prompt  The prompt to display to the user.
     * @return A valid long value entered by the user.
     */
    private long getValidLongInput(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return scanner.nextLong();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a valid number.");
                scanner.nextLine();
            }
        }
    }

    /**
     * This method updates the configuration settings.
     *
     * @param ticketReleaseRate     The new ticket release rate (in ms).
     * @param customerRetrievalRate The new customer retrieval rate (in ms).
     * @param maxTicketCapacity     The new maximum ticket capacity.
     */
    public void updateConfiguration(double ticketReleaseRate, double customerRetrievalRate, long maxTicketCapacity) {
        lock.lock();
        try {
            if (ticketReleaseRate < 0 || customerRetrievalRate < 0 || maxTicketCapacity < loadTotalAvailableTickets()) {
                System.out.println("Invalid input: Rates must be non-negative," +
                        " and max capacity cannot be lower than total available tickets.");
                return;
            }
            this.totalAvailableTickets = loadTotalAvailableTickets();
            this.ticketReleaseRate = ticketReleaseRate;
            this.customerRetrievalRate = customerRetrievalRate;
            this.maxTicketCapacity = maxTicketCapacity;

            System.out.println("\nConfiguration updated successfully!");
            saveConfiguration("Config.json");
            System.out.println("Configuration changes have been auto-saved.\n");
        } finally {
            lock.unlock();
        }
    }

    /**
     * This method loads the total available tickets from the "Tickets.json" file.
     *
     * @return The count of available tickets.
     */
    public long loadTotalAvailableTickets() {
        lock.lock();
        try {
            long availableCount = 0;
            try (Reader reader = new FileReader("Tickets.json")) {
                JsonElement element = JsonParser.parseReader(reader);
                if (element.isJsonArray()) {
                    JsonArray tickets = element.getAsJsonArray();
                    for (JsonElement ticketElement : tickets) {
                        JsonObject ticket = ticketElement.getAsJsonObject();
                        if ("Available".equals(ticket.get("ticketStatus").getAsString())) {
                            availableCount++;
                        }
                    }
                } else {
                    System.out.println("Tickets.json does not contain a valid JSON array.");
                }
            } catch (IOException e) {
                System.out.println("Error loading tickets: " + e.getMessage());
            }
            return availableCount;
        } finally {
            lock.unlock();
        }
    }

    /**
     * This method displays the current configuration settings.
     */
    public void displayConfiguration() {
        lock.lock();
        try {
            updateTotalAvailableTickets();
            System.out.printf("Total Available Tickets: %d%n", totalAvailableTickets);
            System.out.printf("Ticket Release Rate: %.2f ms%n", ticketReleaseRate);
            System.out.printf("Customer Retrieval Rate: %.2f ms%n", customerRetrievalRate);
            System.out.printf("Max Ticket Capacity: %d%n", maxTicketCapacity);
        } finally {
            lock.unlock();
        }
    }

    /**
     * This method updates the total available tickets count by reloading the data from "Tickets.json".
     */
    public void updateTotalAvailableTickets() {
        lock.lock();
        try {
            this.totalAvailableTickets = loadTotalAvailableTickets();
        } finally {
            lock.unlock();
        }
    }

    /**
     * This method saves the current configuration settings to a specified JSON file.
     *
     * @param fileName The name of the file to save the configuration.
     */
    public void saveConfiguration(String fileName) {
        lock.lock();
        try {
            try (FileWriter writer = new FileWriter(fileName)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(this, writer);
                System.out.println("Configuration saved to " + fileName);
            } catch (IOException e) {
                System.out.println("Error saving configuration: " + e.getMessage());
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * This method loads configuration settings from a specified JSON file.
     *
     * @param fileName The name of the file to load the configuration.
     * @return A new Configuration object with loaded settings, or a default configuration if an error occurs.
     */
    public Configuration loadConfiguration(String fileName) {
        lock.lock();
        try {
            try (Reader reader = new FileReader(fileName)) {
                Gson gson = new Gson();
                Configuration config = gson.fromJson(reader, Configuration.class);
                this.totalAvailableTickets = loadTotalAvailableTickets();
                this.ticketReleaseRate = Math.max(config.ticketReleaseRate, 0);
                this.customerRetrievalRate = Math.max(config.customerRetrievalRate, 0);
                this.maxTicketCapacity = Math.max(config.maxTicketCapacity, config.totalAvailableTickets);
                this.configAdminUser = config.configAdminUser;
                this.configAdminPassword = config.configAdminPassword;
                System.out.println("Configuration loaded from " + fileName);
                return config;
            } catch (IOException e) {
                System.out.println("Error loading configuration: " + e.getMessage());
                return new Configuration();
            }
        } finally {
            lock.unlock();
        }
    }
}
