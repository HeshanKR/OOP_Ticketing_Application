//Real-Time Ticketing System Backend by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.
package com.hkrw2082289.ticketing_system.controller;
import com.hkrw2082289.ticketing_system.service.CustomerService;
import com.hkrw2082289.ticketing_system.utils.ResponseFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * This Controller class is for managing customer-related operations such as sign up, sign in, and starting/stopping
 * customer ticket purchasing threads.
 * This provides endpoints for signing-up,signing in, and managing customer ticket purchase threads.
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * This endpoint is for customer sign-up.
     *
     * This method handles the sign-up process for customers by accepting their customerId and password,
     * calling the service method to register the customer, and returning a response based on the result.
     *
     * @param payload a map containing the customerId and password for the customer.
     * @return a ResponseEntity containing a success or error message based on the sign-up process result.
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signUpCustomer(@RequestBody Map<String, String> payload) {
        String customerId = payload.get("customerId");
        String password = payload.get("password");
        ResponseFinder message = customerService.signUpCustomer(customerId, password);
        if (!message.isSuccess()) {
            return ResponseEntity.badRequest().body(message.getMessage());
        }
        return ResponseEntity.ok(message.getMessage());
    }

    /**
     * This endpoint for customer sign-in.
     *
     * This method handles the sign-in process for customers by accepting their customerId and password,
     * calling the service method to authenticate the customer, and returning a response based on the result.
     *
     * @param payload a map containing the customerId and password for the customer.
     * @return a ResponseEntity containing a ResponseFinder object with the result of the sign-in process.
     */
    @PostMapping("/signin")
    public ResponseEntity<ResponseFinder> signInCustomer(@RequestBody Map<String, String> payload) {
        String customerId = payload.get("customerId");
        String password = payload.get("password");
        ResponseFinder response = customerService.signInCustomer(customerId, password);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * This endpoint is for starting a thread for a specific customer to purchase a specific event.
     *
     * This method allows starting a new customer-specific thread by accepting a customerId and additional payload data
     * related to ticket purchase request, calling the service method to start the thread,
     * and returning a response based on the result.
     *
     * @param customerId the ID of the customer who wants to start a thread for purchasing tickets.
     * @param payload additional data required to start the thread such as event name and no. of tickets to book.
     * @return a ResponseEntity containing a success or error message based on the operation's result.
     */
    @PostMapping("/{customerId}/start-thread")
    public ResponseEntity<String> startCustomerThread(
            @PathVariable String customerId,
            @RequestBody Map<String, Object> payload) {
        ResponseFinder message = customerService.startCustomerThread(customerId,payload);
        if (message.isSuccess()) {
            return ResponseEntity.ok(message.getMessage());
        } else {
            return ResponseEntity.badRequest().body(message.getMessage());
        }
    }


    /**
     * This endpoint for stopping all threads for a specific customer ID.
     *
     * This method allows stopping all customer-specific threads by accepting a customerId and calling the service
     * method to terminate the threads. The response depends on the success or failure of the operation.
     *
     * @param customerId the ID of the customer whose ticket purchase threads need to be stopped.
     * @return a ResponseEntity containing a success or error message based on the operation's result.
     */
    @PostMapping("/{customerId}/stop-thread")
    public ResponseEntity<String> stopCustomerThread(@PathVariable String customerId) {
        ResponseFinder message = customerService.stopAllThreadsOfCustomer(customerId);
        if (message.isSuccess()) {
            return ResponseEntity.ok(message.getMessage());
        } else {
            return ResponseEntity.badRequest().body(message.getMessage());
        }
    }
}

