//Real-Time Ticketing System CLI by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The Customer class represents a customer entity that interacts with the shared Ticket Pool to purchase tickets.
 * This class supports sign-up, sign-in, and ticket purchasing functionalities which includes starting and stopping
 * purchasing threads. It uses Gson for storing and retrieving customer data and includes multi-threading
 * capabilities for ticket purchasing.
 */
public class Customer implements Runnable {

    // Customer attributes
    private String customerId; // Unique identifier for the customer.
    private String password;  // Customer's password.
    private TicketPool ticketPool; // Shared ticket pool.
    private AtomicBoolean purchasingTickets = new AtomicBoolean(true); // Consumer-level stop flag.
    private static AtomicBoolean adminStopAllPurchases = new AtomicBoolean(false); // Admin-level stop flag.
    private int customerRetrievalRate;  // Rate of ticket retrieval in milliseconds.
    private static final String CUSTOMER_FILE = "customer.json";  // Customer data storage file.
    private static final int MAX_ATTEMPTS = 3; // Number of attempts provided for user inputs.

    // Loggers in Customer class
    private static final Logger logger = LogManager.getLogger(Customer.class);
    private static final Logger loggerRun = LogManager.getLogger("CustomerRun");

    private static final ReentrantLock customerLock = new ReentrantLock(); // Lock for customer file operations.
    private PurchaseRequest purchaseRequest; // Customer's purchase request.

    /**
     * The Constructor for creating a customer instance for sign-up or sign-in purposes.
     *
     * @param customerId Customer's unique ID
     * @param password Customer's password
     */
    public Customer(String customerId, String password) {
        this.customerId = customerId;
        this.password = password;
    }


    /**
     * The overload Constructor for initializing a customer for ticket purchasing.
     *
     * @param customerId Customer's unique ID.
     * @param password Customer's password.
     * @param ticketPool Shared ticket pool.
     * @param purchaseRate Ticket retrieval rate in milliseconds.
     */
    public Customer(String customerId, String password, TicketPool ticketPool, int purchaseRate) {
        this.customerId = customerId;
        this.password = password;
        this.ticketPool = ticketPool;
        this.customerRetrievalRate = purchaseRate;
    }

    // All Getters and setters.

    public String getPassword() {
        return password;
    }

    public void setTicketPool(TicketPool ticketPool) {
        this.ticketPool = ticketPool;
    }

    public void setCustomerRetrievalRate(int purchaseRate) {
        this.customerRetrievalRate = purchaseRate;
    }

    public void setPurchaseRequest(PurchaseRequest purchaseRequest) {
        this.purchaseRequest = purchaseRequest;
    }

    public String getCustomerId() {
        return customerId;
    }

    /**
     * This method prompts the user to enter valid customer credentials.
     *
     * @return An array containing customer ID and password
     */
    public static String[] promptCustomerCredentials() {
        Scanner scanner = new Scanner(System.in);
        String customerId, password;

        for (int attempts = 0; attempts < MAX_ATTEMPTS; attempts++) {
            System.out.print("Enter Customer ID (7 characters, last 3 must be digits): ");
            customerId = scanner.nextLine().trim();

            if (!isValidCustomerId(customerId)) {
                System.out.println("Invalid Customer ID. Please ensure it is 7 characters with the last 3 as digits.");
                continue;
            }

            System.out.print("Enter Password (8-12 characters): ");
            password = scanner.nextLine().trim();

            if (!isValidPassword(password)) {
                System.out.println("Invalid Password. It must be between 8-12 characters.");
                continue;
            }

            return new String[]{customerId, password};
        }

        System.out.println("Maximum attempts reached. Returning to main menu.");
        return new String[]{"", ""};
    }

    /**
     * This method validates a given customer ID based on specific format requirements.
     *
     * @param customerId Customer ID to validate.
     * @return True if valid, otherwise false.
     */
    private static boolean isValidCustomerId(String customerId) {
        return customerId.length() == 7 && Pattern.matches("^[A-Za-z0-9_]{4}[0-9]{3}$", customerId);
    }
    /**
     * This method validates a given password based on length constraints.
     *
     * @param password Password to validate.
     * @return True if valid, otherwise false.
     */
    private static boolean isValidPassword(String password) {
        return password.length() >= 8 && password.length() <= 12;
    }


    /**
     * This method allows a new customer to sign up so he/she can also buy tickets from the system.
     *
     * @return True if sign-up is successful, otherwise false.
     */
    public static boolean signUp() {
        customerLock.lock();
        try {
            String[] newCustomerCredentials = promptCustomerCredentials();
            if (newCustomerCredentials[0].isEmpty() && newCustomerCredentials[1].isEmpty()) {
                return false;
            }
            List<CustomerData> customers = loadCustomers();
            // Check if customerId is already taken
            for (CustomerData customer : customers) {
                if (customer.getCustomerId().equals(newCustomerCredentials[0])) {
                    logger.info("Customer ID already taken : "+newCustomerCredentials[0]+" .");
                    System.out.println("Customer ID already taken.");
                    return false;
                }
            }
            customers.add(new CustomerData(newCustomerCredentials[0], newCustomerCredentials[1]));
            logger.info("Vendor sign up successful, vendor was registered under Vendor ID  : "+
                    newCustomerCredentials[0]+ " .");
            return saveCustomers(customers);
        } finally {
            customerLock.unlock();
        }
    }

    /**
     * This method allows an existing customer to sign in.
     *
     * @return A Customer object if sign-in is successful, otherwise null
     */
    public static Customer signIn() {
        customerLock.lock();
        try {
            String[] credentials = promptCustomerCredentials();
            if (credentials[0].isEmpty() && credentials[1].isEmpty()) {
                System.out.println("Returning to main menu: all sign-in attempts failed!");
                return null;
            }
            List<CustomerData> customers = loadCustomers();
            if (customers.isEmpty()) {
                logger.error("Customer database is empty!");
                System.out.println("Customer database is empty!");
                return null;
            }
            for (CustomerData customer : customers) {
                if (customer.getCustomerId().equals(credentials[0]) && customer.getPassword().equals(credentials[1])) {
                    logger.info("Customer signed in successfully! with vendor ID: "+ credentials[0]);
                    System.out.println("Customer signed in successfully.");
                    return new Customer(credentials[0], credentials[1]);
                }
            }
            System.out.println("Sign-in failed: Incorrect ID or password.");
            logger.error("Sign in failed: Incorrect ID or password.");
            return null;
        } finally {
            customerLock.unlock();
        }
    }

    /**
     * This method loads customer data from the storage file for customer data.
     *
     * @return A list of customers.
     */
    private static List<CustomerData> loadCustomers() {
        customerLock.lock();
        try (FileReader reader = new FileReader(CUSTOMER_FILE)) {
            Gson gson = new Gson();
            Type customerListType = new TypeToken<List<CustomerData>>() {}.getType();
            List<CustomerData> customers = gson.fromJson(reader, customerListType);
            return customers != null ? customers : new ArrayList<>();
        } catch (IOException e) {
            System.out.println("Error loading customers: " + e.getMessage());
            return new ArrayList<>();
        } finally {
            customerLock.unlock();
        }
    }

    /**
     * This method saves customer data to the storage file for customer data.
     *
     * @param customers List of customers to save
     * @return True if successful, otherwise false
     */
    private static boolean saveCustomers(List<CustomerData> customers) {
        customerLock.lock();
        try (FileWriter writer = new FileWriter(CUSTOMER_FILE)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(customers, writer);
            System.out.println("Customer saved successfully.");
            return true;
        } catch (IOException e) {
            System.out.println("Error saving customer: " + e.getMessage());
            return false;
        } finally {
            customerLock.unlock();
        }

    }

    /**
     * Inner class representing customer data for storage and retrieval.
     */
    private static class CustomerData {
        private final String customerId;  // Customer's unique ID.
        private final String password; // Customer's password.

        public CustomerData(String customerId, String password) {
            this.customerId = customerId;
            this.password = password;
        }
        public String getCustomerId() {
            return customerId;
        }
        public String getPassword() {
            return password;
        }
    }


    /**
     * Runnable method to purchase tickets based on requested event and quantity.
      */
    @Override
    public void run() {

        // Extract details from the PurchaseRequest object
        String eventName = purchaseRequest.getEventName();
        int ticketsToBook = purchaseRequest.getTicketsToBook();

        for (int i = 0; i < ticketsToBook; i++) {
            if (shouldStop(1, eventName)) return; // Case 1: general sleep

            boolean purchased = ticketPool.removeTicket(eventName, customerId);
            if (purchased) {
                loggerRun.info("Ticket purchased by " + customerId + " (Event: " + eventName + ")");
            }

            try {
                //Brief break between purchases of each ticket.
                TimeUnit.MILLISECONDS.sleep(customerRetrievalRate);
                // Check the stop condition again after the sleep
                if (shouldStop(2,eventName)) return; // Case 3: Stopped after sleep
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                loggerRun.error("Ticket purchasing interrupted for consumer: " + customerId +
                        " (Event: " + eventName + ")");
                return;
            }
        }
        loggerRun.info("Ticket purchasing completed for consumer: " + customerId + " (Event: " + eventName + ")");
    }

    /**
     * This method checks whether ticket purchasing should stop.
     *
     * @param logCase Specifies the logging case
     * @param eventName The name of the event
     * @return True if purchasing should stop, otherwise false
     */
    private boolean shouldStop(int logCase, String eventName) {
        if (!purchasingTickets.get() || adminStopAllPurchases.get()) {
            if (logCase == 1) {
                loggerRun.info("Ticket purchasing stopped for consumer: " + customerId +
                        " (Event: " + eventName + ")");
            }  else if (logCase == 2) {
                loggerRun.info("Ticket purchasing stopped after sleep for consumer: " + customerId +
                        " (Event: " + eventName + ")");
            }
            return true;
        }
        return false;
    }



    /**
     * Admin-level method to stop all ticket purchases for all customers.
     */
    public static void stopAllPurchases() {
        adminStopAllPurchases.set(true);
    }

    /**
     * Admin-level method to resume all ticket purchases for all customers.
     */
    public static void resumeAllPurchases() {
        adminStopAllPurchases.set(false);
    }

    /**
     * Stops ticket purchasing for individual customers.
     */
    public void stopPurchasingTickets() {
        purchasingTickets.set(false);
    }

    /**
     * resume ticket purchasing for individual customers.
     */
    public void resumePurchasingTickets() {
        purchasingTickets.set(true);
    }


    /**
     * This method checks the current ticket purchasing status of an individual customer before starting the purchase activities for that
     * customer again.
     *
     * @return True if purchasing is active, otherwise false
     */
    public boolean checkPurchaseStatus() {
        if (adminStopAllPurchases.get()) {
            System.out.println("Ticket purchase paused by admin for all customer.\n");
            return false;
        } else if (!purchasingTickets.get()) {
            System.out.println("Ticket purchase paused by the customer: " + customerId+"\n");
            return false;
        } else {
            System.out.println("Ticket purchase is active for customer: " + customerId+"\n");
            return true;
        }
    }
}

