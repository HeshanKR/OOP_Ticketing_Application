//Real-Time Ticketing System Backend by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.
package com.hkrw2082289.ticketing_system.controller;
import com.hkrw2082289.ticketing_system.model.Configuration;
import com.hkrw2082289.ticketing_system.service.ConfigurationService;
import com.hkrw2082289.ticketing_system.utils.ResponseFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * This is the Controller for managing configuration-related operations in the real-time ticketing system.
 * This provides endpoints for viewing and updating system configurations as well as updating admin credentials.
 */
@RestController
@RequestMapping("/api/configuration")
public class ConfigurationController {

    /**
     * This is the service class to handle configuration-related operations.
     */
    @Autowired
    private ConfigurationService configurationService;

    /**
     * This endpoint is used to retrieves the current system configuration.
     *
     * @return a {@link ResponseEntity} containing the {@link Configuration} object.
     */
    @GetMapping("/view-configuration")
    public ResponseEntity<Configuration> viewConfiguration() {
        return ResponseEntity.ok(configurationService.viewConfiguration());
    }

    /**
     * This endpoint is used to make Updates to the admin credentials for the system configuration.
     *
     * @param payload a map containing the old and new admin credentials:
     *                {
     *                  "oldConfigAdminUser": Current admin username
     *                  "oldConfigAdminPassword": Current admin password
     *                  "newConfigAdminUser": New admin username
     *                  "newConfigAdminPassword": New admin password
     *                }
     * @return a {@link ResponseEntity} containing a success or error message.
     */
    @PutMapping("/update-admin-credentials")
    public ResponseEntity<String> updateAdminCredentials(@RequestBody Map<String, String> payload) {
        String oldUser = payload.get("oldConfigAdminUser");
        String oldPassword = payload.get("oldConfigAdminPassword");
        String newUser = payload.get("newConfigAdminUser");
        String newPassword = payload.get("newConfigAdminPassword");
        ResponseFinder message = configurationService.updateAdminCredentials(oldUser, oldPassword, newUser,
                newPassword);
        if (!message.isSuccess()) {
            return ResponseEntity.badRequest().body(message.getMessage());
        }
        return ResponseEntity.ok(message.getMessage());

    }
    /**
     * This endpoint is used to make Updates to ticket-related settings in the system configuration.
     *
     * @param payload a map containing the admin credentials and ticket settings:
     *                {
     *                  "configAdminUser": Admin username
     *                  "configAdminPassword": Admin password
     *                  "ticketReleaseRate": Ticket release rate (double)
     *                  "customerRetrievalRate": Customer retrieval rate (double)
     *                  "maxTicketCapacity": Maximum ticket capacity (integer)
     *                }
     * @return a {@link ResponseEntity} containing a success or error message.
     */
    @PutMapping("/update-ticket-settings")
    public ResponseEntity<String> updateTicketSettings(@RequestBody Map<String, Object> payload) {
        String adminUser = (String) payload.get("configAdminUser");
        String adminPassword = (String) payload.get("configAdminPassword");
        Double releaseRate = Double.valueOf(payload.get("ticketReleaseRate").toString());
        Double retrievalRate = Double.valueOf(payload.get("customerRetrievalRate").toString());
        Integer maxCapacity = (Integer) payload.get("maxTicketCapacity");
        ResponseFinder message = configurationService.updateTicketSettings(adminUser, adminPassword, releaseRate,
                retrievalRate, maxCapacity);
        if (!message.isSuccess()) {
            return ResponseEntity.badRequest().body(message.getMessage());
        }
        return ResponseEntity.ok(message.getMessage());
    }
}




