//Real-Time Ticketing System Backend by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.
package com.hkrw2082289.ticketing_system.service;
import com.hkrw2082289.ticketing_system.model.TicketEntity;
import com.hkrw2082289.ticketing_system.model.Vendor;
import com.hkrw2082289.ticketing_system.repository.VendorRepository;
import com.hkrw2082289.ticketing_system.utils.ResponseFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This service class is for managing vendor-related operations in the ticketing system.
 * This handles vendor sign-up, sign-in, and thread-based ticket release requests.
 */
@Service
public class VendorService {

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private ConfigurationService configurationService;

    private final TicketService ticketService;

    private static final String VENDOR_ID_REGEX = "^[a-zA-Z]{4}\\d{3}$";

    /**
     * Used to Map threads with vendor IDs from which they were created.
     */
    private final Map<String, List<Thread>> vendorThreads = new ConcurrentHashMap<>();

    private final TicketPoolService ticketPoolService;

    // Lock instance for controlling access
    private static final ReentrantLock vendorLock = new ReentrantLock();

    /**
     * This constructor is used for injecting dependencies like TicketService and TicketPoolService classes.
     *
     * @param ticketService       the service responsible for ticket creation.
     * @param ticketPoolService   the service managing ticket pools.
     */
    public VendorService(TicketService ticketService, TicketPoolService ticketPoolService) {
        this.ticketService = ticketService;
        this.ticketPoolService = ticketPoolService;
    }

    /**
     * This method is used to register a new vendor in the Ticket system.
     *
     * @param vendorId the unique identifier for the vendor.
     * @param password the vendor's password.
     * @return a {@link ResponseFinder} object indicating success or failure of the sign-up process for vendors.
     */
    public ResponseFinder signUpVendor(String vendorId, String password) {
        vendorLock.lock();
        try {
            if (!vendorId.matches(VENDOR_ID_REGEX)) {
                return new ResponseFinder(false,
                        "Error: Vendor ID must be in the format of 4 letters followed by 3 digits.");
            }
            if (password.length() < 8 || password.length() > 12) {
                return new ResponseFinder(false,"Error: Password must be between 8 and 12 characters.");
            }
            if (vendorRepository.existsByVendorId(vendorId)) {
                return new ResponseFinder(false,"Error: Vendor ID already exists.");
            }
            Vendor vendor = new Vendor();
            vendor.setVendorId(vendorId);
            vendor.setPassword(password);
            vendorRepository.save(vendor);
            return new ResponseFinder(true,String.format("Success: Sign-up was successful," +
                    " Here is your Vendor ID: '%s'.", vendorId));
        } finally {
            vendorLock.unlock();
        }
    }

    /**
     * This method is used to authenticate a vendor using their ID and password.
     *
     * @param vendorId the unique identifier for the vendor.
     * @param password the vendor's password.
     * @return a {@link ResponseFinder} object indicating success or failure of the sign-in process of vendors.
     */
    public ResponseFinder signInVendor(String vendorId, String password) {
        vendorLock.lock();
        try {
            Vendor vendor = vendorRepository.findByVendorIdAndPassword(vendorId, password);
            if (vendor != null) {
                return new ResponseFinder(true, String.format("Success: Sign-in successful," +
                        " VendorID found: '%s'.",vendor.getVendorId()), vendor);
            } else {
                return new ResponseFinder(false, "Error: Invalid vendor ID or password.", null);
            }
        } finally {
            vendorLock.unlock();
        }
    }

    /**
     * This method starts a new thread for handling a vendor's ticket release request.
     *
     * @param vendorId the unique identifier for the vendor.
     * @param payload  a map containing details such as event name, price, and ticket batch size.
     * @return a {@link ResponseFinder} object indicating success or failure of ticket release request.
     */
    public ResponseFinder startVendorThread(String vendorId, Map<String, Object> payload) {
        Optional<Vendor> optionalVendor = vendorRepository.findById(vendorId);

        if (optionalVendor.isEmpty()) {
            return new ResponseFinder(false,"Error: Vendor ID " + vendorId +
                    " does not exist in the database.");
        }

        Object priceObj = payload.get("price");
        Double price;
        if (priceObj instanceof Integer) {
            price = ((Integer) priceObj).doubleValue();
        } else if (priceObj instanceof Double) {
            price = (Double) priceObj;
        } else {
            throw new IllegalArgumentException("Invalid price type");
        }

        String eventName = (String) payload.get("event_Name");
        String timeDuration = (String) payload.get("time_Duration");
        String date = (String) payload.get("date");
        int batchSize = (Integer) payload.get("batch_Size");

        List<TicketEntity> ticketBatch = ticketService.createTickets(vendorId, eventName,
                price, timeDuration, date, batchSize);
        Vendor vendor = new Vendor();
        vendor.setVendorId(vendorId);
        double ticketReleaseRate = configurationService.viewConfiguration().getTicketReleaseRate();
        vendor.setTicketReleaseRate(ticketReleaseRate);
        vendor.setTicketBatch(ticketBatch);
        vendor.setTicketPoolService(ticketPoolService);

        Thread vendorThread = new Thread(vendor);
        vendorThreads.computeIfAbsent(vendorId, k -> new ArrayList<>()).add(vendorThread);
        vendorThread.start();

        if (isAdminStopAllRelease()) {
            return new ResponseFinder(false,
                    String.format("Error: System has been stopped by Admin." +
                            " Sorry, your ticket release request for '%s' has been denied", eventName));
        } else {
            return new ResponseFinder(true,
                    String.format("Success: Thread started for vendor ID: %s with event '%s' and ticket batch size %d.",
                            vendorId, eventName, batchSize));
        }
    }

    /**
     * This method stops all ticket releasing threads associated with a given vendor ID.
     *
     * @param vendorId the unique identifier for the vendor.
     * @return a {@link ResponseFinder} object indicating success or failure of the stopping of ticket releases.
     */
    public ResponseFinder stopAllThreadsOfVendor(String vendorId) {
        List<Thread> threads = vendorThreads.get(vendorId);

        if (threads == null || threads.isEmpty()) {
            return new ResponseFinder(false,
                    "Error: No active threads found for vendor ID: " + vendorId);
        }

        for (Thread thread : threads) {
            thread.interrupt();
        }
        vendorThreads.remove(vendorId);

        return new ResponseFinder(true,
                "Success: All threads for vendor ID: " + vendorId + " have been interrupted.");
    }

    /**
     * This method checks if the global stop for ticket releases is enabled.
     *
     * @return {@code true} if the global stop is enabled, otherwise {@code false}.
     */
    public  boolean isAdminStopAllRelease() {
        vendorLock.lock();
        try {
            return Vendor.isAdminStopAllRelease();
        } finally {
            vendorLock.unlock();
        }
    }

    /**
     * This method enables the global stop for all ticket releases for all vendors.
     */
    public static void enableStopAllRelease() {
        vendorLock.lock();
        try {
            Vendor.enableStopAllRelease();
        } finally {
            vendorLock.unlock();
        }
    }

    /**
     * This method disables the global stop for all ticket releases for all vendors.
     */
    public static void disableStopAllRelease() {
        vendorLock.lock();
        try {
            Vendor.disableStopAllRelease();
        } finally {
            vendorLock.unlock();
        }
    }
}


