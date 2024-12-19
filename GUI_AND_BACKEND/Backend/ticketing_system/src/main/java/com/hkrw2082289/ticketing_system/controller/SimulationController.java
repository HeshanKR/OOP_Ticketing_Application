//Real-Time Ticketing System Backend by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.
package com.hkrw2082289.ticketing_system.controller;
import com.hkrw2082289.ticketing_system.utils.CustomerSimulation;
import com.hkrw2082289.ticketing_system.utils.VendorSimulation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import static com.hkrw2082289.ticketing_system.model.Customer.isAdminStopAllPurchases;
import static com.hkrw2082289.ticketing_system.model.Vendor.isAdminStopAllRelease;

/**
 * This controller class is for managing simulation operations of both vendors and customers.
 * This provides endpoints to simulate vendor and customer activities, including ticket releases and purchases.
 */
@RestController
@RequestMapping("/api/simulation")
public class SimulationController {

    @Autowired
    private VendorSimulation vendorSimulation;

    @Autowired
    private CustomerSimulation customerSimulation;

    /**
     * This endpoint is used to start a simulation for vendors adding tickets.
     * This method simulates ticket release activities for the specified number of vendors.
     *
     * @param numberOfVendors the number of vendors to simulate.
     * @return a ResponseEntity containing a message indicating the simulation has started.
     */
    @PostMapping("/start-vendor")
    public ResponseEntity<String> startSimulationForVendor(@RequestParam int numberOfVendors) {
        vendorSimulation.simulateVendorsAddingTickets(numberOfVendors);
        return ResponseEntity.ok("Simulation started for " + numberOfVendors + " vendors.");
    }

    /**
     * This endpoint is used to start a simulation for customers purchasing tickets.
     * This method simulates ticket purchase activities for the specified number of customers.
     *
     * @param numberOfCustomers the number of customers to simulate.
     * @return a ResponseEntity containing a message indicating the simulation has started.
     */
    @PostMapping("/start-customer")
    public ResponseEntity<String> startSimulationForCustomer(@RequestParam int numberOfCustomers) {
        customerSimulation.simulateCustomerThreads(numberOfCustomers);
        return ResponseEntity.ok("Customer simulation started for " + numberOfCustomers + " customers.");
    }

    /**
     * This endpoint is used to start a combined simulation for vendors and customers.
     * This method simulates ticket release activities for vendors and ticket purchase activities for customers,
     * both for the specified number of users. If both vendor and customer operations are stopped by admin, the
     * simulation will not start.
     *
     * @param numberOfUsers the number of vendors and customers to simulate.
     * @return a ResponseEntity indicating whether the simulation was started or stopped by admin.
     */
    @PostMapping("/start")
    public ResponseEntity<String> startSimulation(@RequestParam int numberOfUsers) {
        if(isAdminStopAllRelease() && isAdminStopAllPurchases()){
            return ResponseEntity.badRequest().body(
                    "All Vendor and Customer ticket operations have been stopped by Admin.");
        }
        vendorSimulation.simulateVendorsAddingTickets(numberOfUsers);
        customerSimulation.simulateCustomerThreads(numberOfUsers);
        return ResponseEntity.ok("Customer simulation started for " + numberOfUsers + " customers and vendors.");
    }
}
