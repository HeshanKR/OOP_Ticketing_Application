//Real-Time Ticketing System Backend by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.
package com.hkrw2082289.ticketing_system.service;
import com.hkrw2082289.ticketing_system.helper.PurchaseRequest;
import com.hkrw2082289.ticketing_system.model.Customer;
import com.hkrw2082289.ticketing_system.repository.CustomerRepository;
import com.hkrw2082289.ticketing_system.utils.ResponseFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Service class for managing customer-related operations in the ticketing system.
 * Handles customer sign-up, sign-in, and thread-based purchase requests.
 */
@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Used to Map threads with customer IDs from which they were created.
     */
    private final Map<String, List<Thread>> customerThreads = new ConcurrentHashMap<>();

    private final TicketPoolService ticketPoolService;

    private static final String CUSTOMER_ID_REGEX = "^[a-zA-Z]{4}\\d{3}$";

    @Autowired
    private ConfigurationService configurationService;

    // Lock instance for controlling access for methods to maintain data integrity.
    private static final ReentrantLock customerLock = new ReentrantLock();

    /**
     * Constructor for injecting the TicketPoolService dependency as it is needed to making purchases of tickets.
     *
     * @param ticketPoolService the service responsible for managing ticket pools.
     */
    public CustomerService(TicketPoolService ticketPoolService) {
        this.ticketPoolService = ticketPoolService;
    }

    /**
     * This method is used to registers a new customer in the Ticket system.
     *
     * @param customerId the unique identifier for the customer.
     * @param password   the customer's password.
     * @return a {@link ResponseFinder} object indicating success or failure of the sign-up process.
     */
    public ResponseFinder signUpCustomer(String customerId, String password) {
        customerLock.lock();
        try {
            if (!customerId.matches(CUSTOMER_ID_REGEX)) {
                return new ResponseFinder(false,
                        "Error: Customer ID must be in the format of 4 letters followed by 3 digits.");
            }
            if (password.length() < 8 || password.length() > 12) {
                return new ResponseFinder(false,
                        "Error: Password must be between 8 and 12 characters.");
            }
            if (customerRepository.existsByCustomerId(customerId)) {
                return new ResponseFinder(false,"Error: Customer ID already exists.");
            }
            Customer customer = new Customer();
            customer.setCustomerId(customerId);
            customer.setPassword(password);

            customerRepository.save(customer);
            return new ResponseFinder(true,
                    String.format("Success: Sign-up was successful, Here is your Vendor ID: '%s'.", customerId));
        }
        finally {
            customerLock.unlock();
        }
    }

    /**
     * This method is used to authenticates a customer using their ID and password.
     *
     * @param customerId the unique identifier for the customer.
     * @param password   the customer's password.
     * @return a {@link ResponseFinder} object indicating success or failure of the sign-in process.
     */
    public ResponseFinder signInCustomer(String customerId, String password) {
        customerLock.lock();
        try {
            Customer customer = customerRepository.findByCustomerIdAndPassword(customerId, password);
            if (customer != null) {
                return new ResponseFinder(true,
                        String.format("Success: Sign-in successful," +
                                " Customer ID: '%s'.", customer.getCustomerId()), customer);
            } else {
                return new ResponseFinder(false, "Error: Invalid customer ID or password.", null);
            }
        }
        finally {
            customerLock.unlock();
        }
    }

    /**
     * This method starts a new thread for handling a customer's ticket purchase request.
     *
     * @param customerId the unique identifier for the customer.
     * @param payload    a map containing the purchase details, such as event name and ticket quantity.
     * @return a {@link ResponseFinder} object indicating success or failure the Ticket purchase request.
     */
    public ResponseFinder startCustomerThread(String customerId, Map<String, Object> payload) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);

        if (optionalCustomer.isEmpty()) {
            return new ResponseFinder(false,"Error: Customer ID " + customerId +
                    " does not exist in the database.");
        }

        String eventName = (String) payload.get("eventName");
        int ticketToBook = (int) payload.get("ticketToBook");

        PurchaseRequest purchaseRequest = new PurchaseRequest(customerId,ticketToBook,eventName);
        double customerRetrievalRate = configurationService.viewConfiguration().getCustomerRetrievalRate();
        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setPurchaseRequest(purchaseRequest);
        customer.setCustomerRetrievalRate(customerRetrievalRate);
        customer.setTicketPoolService(ticketPoolService);

        Thread customerThread = new Thread(customer);
        customerThreads.computeIfAbsent(customerId, k -> new ArrayList<>()).add(customerThread);
        customerThread.start();

        if(isAdminStopAllPurchases()) {
            return new ResponseFinder(false, String.format("Error: System has been stopped by Admin." +
                    " Sorry, your ticket purchase request for '%s' has been denied",eventName));
        }
        else{
            return new ResponseFinder(true,
                    String.format("Success: Thread started for CustomerID: %s with event '%s' and " +
                            "purchase request batch size %d. ", customerId, eventName,ticketToBook));
        }
    }

    /**
     * This method is used stop all ticket purchasing threads associated with a given customer ID.
     *
     * @param customerId the unique identifier for the customer.
     * @return a {@link ResponseFinder} object indicating success or failure of stopping the purchase request for
     * tickets.
     */
    public ResponseFinder stopAllThreadsOfCustomer(String customerId){
        List<Thread> threads = customerThreads.get(customerId);

        if (threads == null || threads.isEmpty()) {
            return new ResponseFinder(false,
                    "Error: No active threads found for vendor ID: " + customerId);
        }
        for (Thread thread : threads) {
            thread.interrupt();
        }
        customerThreads.remove(customerId);
        return new ResponseFinder(true,
                "Success: All threads for customer ID: " + customerId + " have been interrupted.");
    }

    /**
     * This method checks if the global stop for ticket purchases is enabled.
     *
     * @return {@code true} if the global stop is enabled, otherwise {@code false}.
     */
    public  boolean isAdminStopAllPurchases() {
        customerLock.lock();
        try {
            return Customer.isAdminStopAllPurchases();
        } finally {
            customerLock.unlock();
        }
    }

    /**
     * This method enables the global stop for all customer purchases.
     */
    public static void enableStopAllPurchases() {
        customerLock.lock();
        try {
            Customer.enableStopAllPurchases();
        } finally {
            customerLock.unlock();
        }
    }

    /**
     * This method disables the global stop for all customer purchases.
     */
    public static void disableStopAllPurchases() {
        customerLock.lock();
        try {
            Customer.disableStopAllPurchases();
        } finally {
            customerLock.unlock();
        }
    }
}
