//Real-Time Ticketing System Backend by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.
package com.hkrw2082289.ticketing_system.utils;
import com.hkrw2082289.ticketing_system.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * This class simulates customer interactions by creating multiple customer threads to perform actions like sign-up
 * and initiating other customer-related processes like purchasing tickets concurrently from the shared pool of tickets.
 *
 * This class provides functionality to simulate a large number of customers performing actions in parallel.
 * It utilizes threads to simulate concurrent customer behavior and logs the results of these actions.
 */
@Component
public class CustomerSimulation {

    @Autowired
    private CustomerService customerService;

    private static final Logger logger = LoggerFactory.getLogger(CustomerSimulation.class);

    /**
     * This simulates customer interactions by creating and starting threads for each customer.
     * Each customer signs up and then performs purchasing actions of tickets concurrently in separate threads.
     *
     * @param numberOfCustomers the number of customers to simulate.
     */
    public void simulateCustomerThreads(int numberOfCustomers) {
        logger.info("Starting customer simulation with {} customers.", numberOfCustomers);

        List<Thread> customerThreads = new ArrayList<>();
        for (int i = 1; i <= numberOfCustomers; i++) {
            String customerId = "cust" + String.format("%03d", i);
            String password = "password" + i;

            ResponseFinder signUpResult = customerService.signUpCustomer(customerId, password);
            logger.debug("Sign-up result for {}: {}", customerId, signUpResult.getMessage());
        }

        for (int i = 1; i <= numberOfCustomers; i++) {
            String customerId = "cust" + String.format("%03d", i);
            Map<String, Object> payload = generatePayload(i,customerId);

            Thread customerThread = new Thread(() -> {
                ResponseFinder result = customerService.startCustomerThread(customerId, payload);
                logger.debug("Thread result for {}: {}", customerId, result.getMessage());
            });

            customerThreads.add(customerThread);
            customerThread.start();
        }

        logger.info("Simulation started. Customer threads are running concurrently.");
    }

    /**
     * Used to Generate a payload that map for customer actions, including event name and ticket booking information.
     *
     * @param vendorIndex the index of the vendor for the event (This is used as demo ticket will have a Ticket name
     *                    which will have the suffix of the generated vendor ID).
     * @param customerId the unique identifier of the customer.
     * @return a map containing event-related data for the customer.
     */
    private Map<String, Object> generatePayload(int vendorIndex, String customerId) {
        String vendorId = generateVendorId(vendorIndex);
        Map<String, Object> payload = new HashMap<>();
        payload.put("eventName", "Event_" + vendorId);
        payload.put("ticketToBook", 10);
        logger.trace("Generated payload for {}: {}", customerId, payload);
        return payload;
    }

    /**
     * Used to generate a vendor ID based on the provided index.
     *
     * @param index the index of the vendor.
     * @return a formatted vendor ID string.
     */
    private String generateVendorId(int index) {
        return "VEND" + String.format("%03d", index);
    }
}
