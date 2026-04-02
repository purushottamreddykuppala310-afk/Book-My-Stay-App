import java.util.*;

// ==========================
// ENTITY: Booking Request
// ==========================
class BookingRequest {
    String guestName;
    String roomType;

    public BookingRequest(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
}

// ==========================
// THREAD-SAFE INVENTORY
// ==========================
class RoomInventory {

    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("STANDARD", 2);
        inventory.put("DELUXE", 2);
        inventory.put("SUITE", 1);
    }

    // CRITICAL SECTION (synchronized)
    public synchronized boolean bookRoom(String roomType, String guestName) {

        if (!inventory.containsKey(roomType)) {
            System.out.println("❌ " + guestName + ": Invalid room type");
            return false;
        }

        int available = inventory.get(roomType);

        if (available <= 0) {
            System.out.println("❌ " + guestName + ": No " + roomType + " rooms left");
            return false;
        }

        // Simulate delay (to expose race conditions if not synchronized)
        try { Thread.sleep(100); } catch (InterruptedException e) {}

        inventory.put(roomType, available - 1);

        System.out.println("✅ " + guestName + " booked " + roomType +
                " | Remaining: " + (available - 1));

        return true;
    }

    public void printInventory() {
        System.out.println("\nFinal Inventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + " → " + inventory.get(type));
        }
    }
}

// ==========================
// SHARED BOOKING QUEUE
// ==========================
class BookingQueue {

    private Queue<BookingRequest> queue = new LinkedList<>();

    public synchronized void addRequest(BookingRequest request) {
        queue.add(request);
    }

    public synchronized BookingRequest getRequest() {
        return queue.poll();
    }
}

// ==========================
// WORKER THREAD
// ==========================
class BookingProcessor extends Thread {

    private BookingQueue queue;
    private RoomInventory inventory;

    public BookingProcessor(BookingQueue queue, RoomInventory inventory) {
        this.queue = queue;
        this.inventory = inventory;
    }

    @Override
    public void run() {

        while (true) {
            BookingRequest request;

            // synchronized fetch
            synchronized (queue) {
                request = queue.getRequest();
            }

            if (request == null) {
                break;
            }

            inventory.bookRoom(request.roomType, request.guestName);
        }
    }
}

// ==========================
// MAIN APPLICATION
// ==========================
public class BookMyStayApp {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingQueue queue = new BookingQueue();

        // Simulate multiple users
        queue.addRequest(new BookingRequest("Alice", "STANDARD"));
        queue.addRequest(new BookingRequest("Bob", "STANDARD"));
        queue.addRequest(new BookingRequest("Charlie", "STANDARD")); // extra
        queue.addRequest(new BookingRequest("David", "DELUXE"));
        queue.addRequest(new BookingRequest("Eve", "DELUXE"));
        queue.addRequest(new BookingRequest("Frank", "SUITE"));
        queue.addRequest(new BookingRequest("Grace", "SUITE")); // extra

        // Create multiple threads
        Thread t1 = new BookingProcessor(queue, inventory);
        Thread t2 = new BookingProcessor(queue, inventory);
        Thread t3 = new BookingProcessor(queue, inventory);

        // Start threads
        t1.start();
        t2.start();
        t3.start();

        // Wait for completion
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Final inventory state
        inventory.printInventory();
    }
}