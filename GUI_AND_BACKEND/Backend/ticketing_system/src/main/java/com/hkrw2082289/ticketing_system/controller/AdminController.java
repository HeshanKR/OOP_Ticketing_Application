//Real-Time Ticketing System Backend by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.
package com.hkrw2082289.ticketing_system.controller;
import com.hkrw2082289.ticketing_system.service.CustomerService;
import com.hkrw2082289.ticketing_system.service.VendorService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

/**
 * This is the Controller for admin-level operations to manage the real-time ticketing system.
 * This provides endpoints to stop and resume all activities for customers and vendors in the ticketing system.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    /**
     * This endpoint stops all customer and vendor activities in the system.
     * This method enables a global stop, preventing customers from purchasing tickets
     * and vendors from releasing tickets.
     *
     * @return a ResponseEntity containing a confirmation message.
     */
    @PostMapping("/stop-all-activity")
    public ResponseEntity<String> stopAllActivity() {
        CustomerService.enableStopAllPurchases();
        VendorService.enableStopAllRelease();
        return ResponseEntity.ok("All customer and vendor threads have been stopped.");
    }

    /**
     * This endpoint resumes all customer and vendor activities in the system.
     * This method disables the global stop, allowing customers to purchase tickets
     * and vendors to release tickets again.
     *
     * @return a ResponseEntity containing a confirmation message.
     */
    @PostMapping("/resume-all-activity")
    public ResponseEntity<String> resumeAllActivity() {
        CustomerService.disableStopAllPurchases();
        VendorService.disableStopAllRelease();
        return ResponseEntity.ok("All customer and vendor threads have been resumed.");
    }
}
