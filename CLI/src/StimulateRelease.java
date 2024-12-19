//Real-Time Ticketing System CLI by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.

import java.util.List;

/**
 * The StimulateRelease class is responsible for simulating the release of tickets
 * into the ticket pool by multiple vendors. Each vendor releases tickets at a specified rate,
 * demonstrating concurrent operations in a multithreaded environment.
 */
public class StimulateRelease{

    /**
     * Reference to the shared TicketPool class instance used for ticket management.
     */
    private TicketPool ticketpool;

    /**
     * The rate at which vendors release tickets, specified in milliseconds.
     */
    private int releaseRate;

    /**
     * This Constructor constructs a StimulateRelease instance with a specified ticket pool and release rate.
     *
     * @param ticketpool  the shared TicketPool object where tickets are released.
     * @param releaseRate the rate at which vendors release tickets into the pool.
     */
    public StimulateRelease(TicketPool ticketpool, int releaseRate) {
        this.ticketpool = ticketpool;
        this.releaseRate = releaseRate;
    }

    /**
     * This starts the simulation of ticket release by multiple vendors.
     * Vendors create batches of tickets and release them into the ticket pool concurrently.
     * Each vendor runs in a separate thread to simulate real-time ticket release.
     */
    public void startBotRelease(){
        // Creating vendors and set their release rates and ticket pool.

        Vendor vendor1 = Vendor.createVendor("gren234","20he40gn");
        vendor1.setTicketReleaseRate(releaseRate);
        vendor1.setTicketPool(ticketpool);

        Vendor vendor2 = Vendor.createVendor("kren234","20he40gn");
        vendor2.setTicketReleaseRate(releaseRate);
        vendor2.setTicketPool(ticketpool);

        Vendor vendor3 = Vendor.createVendor("jren234","20he40gn");
        vendor3.setTicketReleaseRate(releaseRate);
        vendor3.setTicketPool(ticketpool);

        // Creating ticket batches for each vendor.
        List<Ticket> tickets1 = Ticket.createTicketsForVendor(vendor1.getVendorId(),100,"rolo",
                75.3,"2 hours","2022-10-20");
        List<Ticket> tickets2 = Ticket.createTicketsForVendor(vendor2.getVendorId(),100,"polo",
                75.3,"2 hours","2022-10-20");
        List<Ticket> tickets3 = Ticket.createTicketsForVendor(vendor3.getVendorId(),100,"yolo",
                75.3,"2 hours","2022-10-20");
        List<Ticket> tickets4 = Ticket.createTicketsForVendor(vendor1.getVendorId(),100,"holo",
                75.3,"2 hours","2022-10-20");


        // Starting  threads for each vendor to release tickets.
        vendor1.setTicketBatch(tickets1);
        Thread vendorThread1 = new Thread(vendor1);
        vendorThread1.start();

        vendor2.setTicketBatch(tickets2);
        Thread vendorThread2 = new Thread(vendor2);
        vendorThread2.start();

        vendor3.setTicketBatch(tickets3);
        Thread vendorThread3 = new Thread(vendor3);
        vendorThread3.start();

        // Introduce a delay before the next ticket batch is released.
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Releasing an additional batch of tickets from vendor1.
        vendor1.setTicketBatch(tickets4);
        Thread vendorThread4 = new Thread(vendor1);
        vendorThread4.start();
    }
}


