//Real-Time Ticketing System Backend by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.
package com.hkrw2082289.ticketing_system.controller;
import com.hkrw2082289.ticketing_system.service.VendorService;
import com.hkrw2082289.ticketing_system.utils.ResponseFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * This controller class for managing vendor-related operations such as sign up, sign in, and starting/stopping vendor
 * ticket release threads.
 * This provides endpoints for signing-up,signing in, and managing vendor ticket release threads.
 */
@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    @Autowired
    private VendorService vendorService;

    /**
     * This endpoint is for vendor sign-up.
     *
     * This method handles the sign-up process for vendors by accepting their vendorId and password,
     * calling the service method to register the vendor, and returning a response based on the result.
     *
     * @param payload a map containing the vendorId and password for the vendor.
     * @return a ResponseEntity containing a success or error message based on the sign-up process result.
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signUpVendor(@RequestBody Map<String, String> payload) {
        String vendorId = payload.get("vendorId");
        String password = payload.get("password");
        ResponseFinder message = vendorService.signUpVendor(vendorId, password);
        if (!message.isSuccess()) {
            return ResponseEntity.badRequest().body(message.getMessage());
        }
        return ResponseEntity.ok(message.getMessage());
    }

    /**
     * This endpoint is for vendor sign-in.
     *
     * This method handles the sign-in process for vendors by accepting their vendorId and password,
     * calling the service method to authenticate the vendor, and returning a response based on the result.
     *
     * @param payload a map containing the vendorId and password for the vendor.
     * @return a ResponseEntity containing a ResponseFinder object with the result of the sign-in process.
     */
    @PostMapping("/signin")
    public ResponseEntity<ResponseFinder> signInVendor(@RequestBody Map<String, String> payload) {
        String vendorId = payload.get("vendorId");
        String password = payload.get("password");
        ResponseFinder response = vendorService.signInVendor(vendorId, password);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * This endpoint is for starting a thread for releasing tickets for a specific vendor.
     *
     * This method allows starting a new vendor-specific thread by accepting a vendorId and additional payload data
     * needed to release tickets to the shared ticket pool by calling the service method to start the thread,
     * and returning a response based on the result.
     *
     * @param vendorId the ID of the vendor who wants to start a thread.
     * @param payload additional data required to start the thread such as event name,price,time duration, date, and
     *               ticket batch size.
     * @return a ResponseEntity containing a success or error message based on the operation's result.
     */
    @PostMapping("/{vendorId}/start-thread")
    public ResponseEntity<String> startVendorThread(
            @PathVariable String vendorId,
            @RequestBody Map<String, Object> payload) {
        ResponseFinder message = vendorService.startVendorThread(vendorId,payload);
        if (message.isSuccess()) {
            return ResponseEntity.ok(message.getMessage());
        } else {
            return ResponseEntity.badRequest().body(message.getMessage());
        }
    }

    /**
     * This endpoint is for stopping all ticket release threads for a specific vendor.
     *
     * This method allows stopping all threads for a specific vendor by accepting a vendorId and calling the service
     * method to terminate the threads. The response depends on the success or failure of the operation.
     *
     * @param vendorId the ID of the vendor whose threads need to be stopped.
     * @return a ResponseEntity containing a success or error message based on the operation's result.
     */
    @PostMapping("/{vendorId}/stop-thread")
    public ResponseEntity<String> stopVendorThread(@PathVariable String vendorId) {
        ResponseFinder message = vendorService.stopAllThreadsOfVendor(vendorId);
        if (message.isSuccess()) {
            return ResponseEntity.ok(message.getMessage());
        } else {
            return ResponseEntity.badRequest().body(message.getMessage());
        }
    }
}

