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
 * This class represents a vendor entity that interacts with the TicketPool to release tickets.
 * This class supports vendor sign-up, sign-in, ticket release functionality,which includes starting and stopping
 * Ticket releasing threads, and includes multi-threading capabilities for releasing tickets in batches.
 */
public class Vendor implements Runnable {
    private String vendorId; // Unique identifier for the vendor.
    private String password; // Vendor's password.
    private TicketPool ticketPool; // Shared ticket pool.
    private AtomicBoolean releasingTickets = new AtomicBoolean(true); // Vendor-level stop/start authority.
    private static AtomicBoolean adminStopAll = new AtomicBoolean(false); // Admin-level stop/start authority.
    private int ticketReleaseRate; //Ticket release rate in milliseconds
    private List<Ticket> ticketBatch;  // Batch of tickets to release.
    private static final String VENDOR_FILE = "Vendors.json"; // File storing vendor data.
    private static final int MAX_ATTEMPTS = 3; // Number of attempts provided for user inputs.
    private static final Logger logger = LogManager.getLogger(Vendor.class); //Logging for vendor class.
    private static final Logger loggerRun = LogManager.getLogger("VendorRun"); //Logging for vendor class.
    private static final ReentrantLock vendorLock = new ReentrantLock(); // Lock for vendor file operations.


    /**
     * This private constructor is for initializing vendor credentials (for sign-in or sign-up).
     *
     * @param vendorId Vendor's unique ID
     * @param password Vendor's password
     */
    private Vendor(String vendorId, String password) {
        this.vendorId = vendorId;
        this.password = password;
    }

    /**
     * This is the factory method to create a Vendor instance with only vendor ID and password.
     *
     * @param vendorId Vendor's unique ID
     * @param password Vendor's password
     * @return A new Vendor instance.
     */
    public static Vendor createVendor(String vendorId, String password) {
        return new Vendor(vendorId, password); // This Calls the private constructor.
    }

    /**
     * This overload constructor is for initializing vendor with ticket-related functionality.
     *
     * @param vendorId Vendor's unique ID
     * @param password Vendor's password
     * @param ticketPool Shared ticket pool
     * @param ticketBatch Batch of tickets to release
     * @param releaseRate Ticket release rate in milliseconds
     */
    public Vendor(String vendorId, String password, TicketPool ticketPool, List<Ticket> ticketBatch, int releaseRate) {
        this.vendorId = vendorId;
        this.password = password;
        this.ticketPool = ticketPool;
        this.ticketReleaseRate = releaseRate;
        this.ticketBatch = ticketBatch;
    }

    //The required getters and setters.

    public void setTicketPool(TicketPool ticketPool) {
        this.ticketPool = ticketPool;
    }

    public void setTicketReleaseRate(int releaseRate) {
        this.ticketReleaseRate = releaseRate;
    }

    public void setTicketBatch(List<Ticket> ticketBatch) {
        this.ticketBatch = ticketBatch;
    }

    public String getVendorId() {
        return vendorId;
    }

    /**
     * This method prompts the user to enter valid vendor credentials.
     *
     * @return An array containing vendor ID and password.
     */
    public static String[] promptVendorCredentials() {
        Scanner scanner = new Scanner(System.in);
        String vendorId, password;

        for (int attempts = 0; attempts < MAX_ATTEMPTS; attempts++) {
            System.out.print("Enter Vendor ID (7 characters, last 3 must be digits): ");
            vendorId = scanner.nextLine().trim();

            if (!isValidVendorId(vendorId)) {
                System.out.println("Invalid Vendor ID. Vendor ID should be exactly 7 characters long," +
                        " with the last 3 characters being digits");
                continue;
            }

            System.out.print("Enter Password (8-12 characters): ");
            password = scanner.nextLine().trim();

            if (!isValidPassword(password)) {
                System.out.println("Invalid Password. Password must be between 8-12 characters.");
                continue;
            }
            // For successful inputs an array containing the inputs is returned.
            return new String[]{vendorId, password};
        }

        System.out.println("Maximum attempts reached. Returning to main menu.");
        return new String[]{"", ""}; // For Failed input after max attempts an array with empty strings is returned.
    }

    /**
     * This method validates the format of a vendor ID.
     *
     * @param vendorId Vendor ID to validate
     * @return True if valid, otherwise false
     */
    private static boolean isValidVendorId(String vendorId) {
        return vendorId.length() == 7 && Pattern.matches("^[A-Za-z0-9_]{4}[0-9]{3}$", vendorId);
    }

    /**
     * This validates the length of a vendor password.
     *
     * @param password Password to validate
     * @return True if valid, otherwise false
     */
    private static boolean isValidPassword(String password) {
        return password.length() >= 8 && password.length() <= 12;
    }

    /**
     * This method is to Signs up a new vendor.
     *
     * @return True if sign-up is successful, otherwise false
     */
    public static boolean signUp() {
        vendorLock.lock();
        try {
            String[] newVendorCredentials = promptVendorCredentials();
            if (newVendorCredentials[0].equals("") && newVendorCredentials[1].equals("")) {
                return false;
            }
            List<VendorData> vendors = loadVendors();
            // This Checks if vendorId is already taken
            for (VendorData vendor : vendors) {
                if (vendor.getVendorId().equals(newVendorCredentials[0])) {
                    logger.info("Vendor ID already taken : "+ newVendorCredentials[0]+ " .");
                    System.out.println("Vendor ID already taken.");
                    return false;
                }
            }
            vendors.add(new VendorData(newVendorCredentials[0], newVendorCredentials[1]));
            // Write updated vendor list back to file
            logger.info("Vendor sign up successful, vendor was registered under Vendor ID  : "+
                    newVendorCredentials[0]+ " .");
            return saveVendors(vendors);
        } finally {
            vendorLock.unlock();
        }
    }

    /**
     * This method is to Signs in an existing vendor.
     *
     * @return A Vendor object if sign-in is successful, otherwise null
     */
    public static Vendor signIn() {
        vendorLock.lock();
        try {
            String[] vendorCredentials = promptVendorCredentials();
            if (vendorCredentials[0].equals("") && vendorCredentials[1].equals("")) {
                System.out.println("Returning Back to main menu all the sign in attempts Failed!\n");
                return null;
            }
            List<VendorData> vendors = loadVendors();
            if (vendors.isEmpty()) {
                logger.error("Vendors database is empty!");
                System.out.println("Vendors database is empty!.\n");
                return null;
            }
            for (VendorData vendor : vendors) {
                if (vendor.getVendorId().equals(vendorCredentials[0]) &&
                        vendor.getPassword().equals(vendorCredentials[1])) {
                    System.out.println("Vendor signed in successfully!\n");
                    logger.info("Vendor signed in successfully! with vendor ID: "+ vendorCredentials[0]);
                    // Initialize only credentials for sign-in
                    return new Vendor(vendorCredentials[0], vendorCredentials[1]);
                }
            }
            System.out.println("Sign in failed: Incorrect ID or password.\n");
            logger.error("Sign in failed: Incorrect ID or password.");
            return null;
        } finally {
            vendorLock.unlock();
        }
    }

    /**
     * This method Loads vendors from the vendor storage file.
     *
     * @return A list of VendorData objects
     */
    private static List<VendorData> loadVendors() {
        vendorLock.lock();
        try {
            try (FileReader reader = new FileReader(VENDOR_FILE)) {
                Gson gson = new Gson();
                Type vendorListType = new TypeToken<List<VendorData>>() {}.getType();
                List<VendorData> vendors = gson.fromJson(reader, vendorListType);
                return vendors != null ? vendors : new ArrayList<>(); // Return empty list if JSON is null
            } catch (IOException e) {
                System.out.println("Error loading vendors: " + e.getMessage());
                return new ArrayList<>(); // Return an empty list if file doesn't exist or read error occurs
            }
        } finally {
            vendorLock.unlock();
        }
    }

    /**
     * This method is used to save vendors to the vendor storage file.
     *
     * @param vendors List of VendorData objects to save
     * @return True if successful, otherwise false
     */
    private static boolean saveVendors(List<VendorData> vendors) {
        vendorLock.lock();
        try {
            try (FileWriter writer = new FileWriter(VENDOR_FILE)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(vendors, writer);
                System.out.println("Vendor saved successfully.");
                return true;
            } catch (IOException e) {
                System.out.println("Error saving vendor: " + e.getMessage());
                return false;
            }
        } finally {
            vendorLock.unlock();
        }
    }

    /**
     * This an Inner class representing vendor data for storage and retrieval.
     */
    private static class VendorData {
        private final String vendorId; // Vendor's unique ID.
        private final String password; // Vendor's password.

        public VendorData(String vendorId, String password) {
            this.vendorId = vendorId;
            this.password = password;
        }

        public String getVendorId() {
            return vendorId;
        }

        public String getPassword() {
            return password;
        }
    }

    /**
     * This method is Runnable implementation for releasing tickets in batches.
     */
    @Override
    public void run() {
        String ticketName ="";
        for (Ticket ticket : ticketBatch) {
            ticketName = ticket.getEventName();
            if (shouldStop(1)) return; // general/Admin stop message.

            boolean added = ticketPool.addTicket(ticket); // Synchronization handled in TicketPool
            if (added) {
                loggerRun.info("Ticket added by " + vendorId + ": " + ticket.getTicketId()+ " "+
                        ticket.getEventName());
            }

            try {
                TimeUnit.MILLISECONDS.sleep(ticketReleaseRate);
                if (shouldStop(2)) return; // Stopped after sleep message
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                loggerRun.error("Ticket release interrupted for vendor: " + vendorId);
                return;
            }
        }
        loggerRun.info("Ticket release completed for vendor: " + vendorId +" selling " + ticketName+ " tickets.");
    }

    /**
     * This method checks if the ticket release process should stop.
     *
     * @param logCase Specifies the logging case
     * @return True if the process should stop, otherwise false
     */
    private boolean shouldStop(int logCase) {
        if (!releasingTickets.get() || adminStopAll.get()) {
            if (logCase == 1) {
                loggerRun.info("Ticket release stopped for vendor: " + vendorId);
            } else if (logCase == 2) {
                loggerRun.info("Ticket release stopped after sleep for vendor: " + vendorId);
            }
            return true;
        }
        return false;
    }

    /**
     * Admin-level method to stop all ticket release for all vendors.
     */
    public static void stopAllReleases() {
        adminStopAll.set(true);
    }

    /**
     * Admin-level method to resume all ticket release for all vendors.
     */
    public static void resumeAllReleases() {
        adminStopAll.set(false);
    }

    /**
     * Stops ticket releases for individual vendors.
     */
    public void stopReleasingTickets() {
        releasingTickets.set(false);
    }

    /**
     * Resume ticket releases for individual vendors.
     */
    public void resumeReleasingTickets() {
        releasingTickets.set(true);
    }

    /**
     * This method checks the current ticket releasing status of an individual vendor
     * before starting the ticket releasing activities for that customer again.
     *
     * @return True if purchasing is active, otherwise false
     */
    public boolean checkReleaseStatus() {
        if (adminStopAll.get()) {
            System.out.println("Ticket release paused by admin for all vendors.\n");
            return false;
        } else if (!releasingTickets.get()) {
            System.out.println("Ticket release paused by the vendor: " + vendorId+"\n");
            return false;
        } else {
            System.out.println("Ticket release is active for vendor: " + vendorId+"\n");
            return true;
        }
    }
}
