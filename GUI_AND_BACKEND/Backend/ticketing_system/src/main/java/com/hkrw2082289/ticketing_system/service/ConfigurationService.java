//Real-Time Ticketing System Backend by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.
package com.hkrw2082289.ticketing_system.service;
import com.hkrw2082289.ticketing_system.model.Configuration;
import com.hkrw2082289.ticketing_system.repository.ConfigurationRepository;
import com.hkrw2082289.ticketing_system.utils.ResponseFinder;
import com.hkrw2082289.ticketing_system.utils.TicketUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This is the service class used to manage the configuration of the ticketing system.
 *
 * This service provides methods to retrieve or create default configuration, update admin credentials,
 * and modify ticket-related settings. It interacts with the {@link ConfigurationRepository} and
 * {@link TicketUtility} for persistence and ticket utility operations.
 */
@Service
public class ConfigurationService {

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private TicketUtility ticketUtility;

    /**
     * This constructor retrieves the current configuration or creates a default one if none exists.
     *
     * @return the current or newly created default {@link Configuration}.
     */
    private Configuration getOrCreateDefaultConfiguration() {
        return configurationRepository.findAll()
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    Configuration defaultConfig = new Configuration();
                    defaultConfig.setTicketReleaseRate(1.0);
                    defaultConfig.setCustomerRetrievalRate(1.0);
                    defaultConfig.setMaxTicketCapacity(100);
                    defaultConfig.setConfigAdminUser("admin");
                    defaultConfig.setConfigAdminPassword("admin123");
                    return configurationRepository.save(defaultConfig);
                });
    }

    /**
     * This method is used to retrieve the current configuration with an updated total available tickets count,
     * extracted by using methods of the ticketUtility class.
     *
     * @return the current {@link Configuration} with the updated ticket count.
     */
    public Configuration viewConfiguration() {
        Configuration config = getOrCreateDefaultConfiguration();
        int totalAvailableTickets = (int) ticketUtility.countAvailableTickets();
        config.setTotalAvailableTickets(totalAvailableTickets);
        return config;
    }

    /**
     * This method updates the admin credentials if the provided old credentials match the stored credentials.
     *
     * @param oldUser the current admin username.
     * @param oldPassword the current admin password.
     * @param newUser the new admin username.
     * @param newPassword the new admin password.
     * @return a {@link ResponseFinder} indicating success or failure.
     */
    public ResponseFinder updateAdminCredentials(String oldUser, String oldPassword,
                                                 String newUser, String newPassword) {
        Configuration config = getOrCreateDefaultConfiguration();
        if (config.getConfigAdminUser().equals(oldUser) && config.getConfigAdminPassword().equals(oldPassword)) {
            config.setConfigAdminUser(newUser);
            config.setConfigAdminPassword(newPassword);
            configurationRepository.save(config);
            return new ResponseFinder(true,"Admin credentials updated successfully.");
        }
        return new ResponseFinder(false, "Error: Invalid old credentials.");
    }

    /**
     * This method updates the ticket-related settings, including release rate, retrieval rate, and maximum capacity.
     * It ensures the new max capacity is not less than the total available tickets. The updates will only be confirmed
     * if the provided Admin credentials match the stored credentials.
     *
     * @param adminUser the admin username for authentication.
     * @param adminPassword the admin password for authentication.
     * @param releaseRate the new ticket release rate.
     * @param retrievalRate the new ticket retrieval rate.
     * @param maxCapacity the new maximum ticket capacity.
     * @return a {@link ResponseFinder} indicating success or failure.
     */
    public ResponseFinder updateTicketSettings(String adminUser, String adminPassword,
                                               Double releaseRate, Double retrievalRate, Integer maxCapacity) {
        Configuration config = getOrCreateDefaultConfiguration();

        // Get total available tickets from TicketPoolService
        int totalAvailableTickets = (int) ticketUtility.countAvailableTickets();

        // Check if the new maxCapacity is valid
        if (maxCapacity < totalAvailableTickets) {
            return new ResponseFinder(false,
                    "Error: Cannot set max capacity lower than the current total available tickets.");
        }

        if (config.getConfigAdminUser().equals(adminUser) && config.getConfigAdminPassword().equals(adminPassword)) {
            config.setTotalAvailableTickets(totalAvailableTickets);
            config.setTicketReleaseRate(releaseRate);
            config.setCustomerRetrievalRate(retrievalRate);
            config.setMaxTicketCapacity(maxCapacity);
            configurationRepository.save(config);

            return new ResponseFinder(true, "Ticket settings updated successfully.");
        }
        return new ResponseFinder(false, "Error: Invalid admin credentials.");
    }
}
