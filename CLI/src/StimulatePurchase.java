//Real-Time Ticketing System CLI by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.

/**
 * The StimulatePurchase class simulates ticket purchases by multiple customers.
 * It creates customers, assigns purchase requests to them, and starts their purchase process
 * in separate threads.
 */
public class StimulatePurchase {

    /**
     * Reference to the shared TicketPool class instance used for ticket management.
     */
    private TicketPool ticketpool;

    /**
     * The rate at which customers retrieve tickets, specified in milliseconds.
     */
    private int purchaseRate;

    /**
     * This constructor constructs a new StimulatePurchase instance.
     *
     * @param ticketpool   the shared {@code TicketPool} instance used for ticket operations
     * @param purchaseRate the rate (in milliseconds) at which customers attempt to retrieve tickets
     */
    public StimulatePurchase(TicketPool ticketpool, int purchaseRate) {
        this.ticketpool = ticketpool;
        this.purchaseRate = purchaseRate;
    }

    /**
     * This method starts the ticket purchase simulation by creating multiple customer threads.
     * This method creates three customers, assigns them unique purchase requests, and starts
     * their ticket retrieval in separate threads. An additional purchase request is made
     * by reusing one of the customers after a brief delay.
     */
    public void startBotPurcahse(){
        // Create and configure customer 1.
        Customer customer1 = new Customer("robe234","rg34jh24");
        customer1.setCustomerRetrievalRate(purchaseRate);
        customer1.setTicketPool(ticketpool);

        // Create and configure customer 2.
        Customer customer2 = new Customer("bele234","rg34jh24");
        customer2.setCustomerRetrievalRate(purchaseRate);
        customer2.setTicketPool(ticketpool);

        // Create and configure customer 3.
        Customer customer3 = new Customer("relo234","rg34jh24");
        customer3.setCustomerRetrievalRate(purchaseRate);
        customer3.setTicketPool(ticketpool);

        // Define all 4 purchase requests.
        PurchaseRequest purchase1 = new PurchaseRequest("rolo",50);
        PurchaseRequest purchase2 = new PurchaseRequest("polo",50);
        PurchaseRequest purchase3 = new PurchaseRequest("yolo",50);
        PurchaseRequest purchase4 = new PurchaseRequest("holo",50);

        // Assign and start thread for customer 1.
        customer1.setPurchaseRequest(purchase1);
        Thread customerThread1 = new Thread(customer1);
        customerThread1.start();

        // Assign and start thread for customer 2.
        customer2.setPurchaseRequest(purchase2);
        Thread customerThread2 = new Thread(customer2);
        customerThread2.start();

        // Assign and start thread for customer 3.
        customer3.setPurchaseRequest(purchase3);
        Thread customerThread3 = new Thread(customer3);
        customerThread3.start();

        // Introduce a delay before assigning a new purchase request to customer 1.
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Reuse customer 1 for a new purchase request and start a new thread.
        customer1.setPurchaseRequest(purchase4);
        Thread customerThread4 = new Thread(customer1);
        customerThread4.start();
    }
}
