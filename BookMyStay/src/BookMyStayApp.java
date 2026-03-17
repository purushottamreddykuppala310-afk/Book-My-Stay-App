import java.util.*;

// -------------------- RESERVATION --------------------
class Reservation {
    private String guestName;
    private String roomType;
    private String assignedRoomId; // Assigned after confirmation

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }

    public void setAssignedRoomId(String roomId) {
        this.assignedRoomId = roomId;
    }

    public String getAssignedRoomId() {
        return assignedRoomId;
    }

    @Override
    public String toString() {
        return "Guest: " + guestName +
                " | Room Type: " + roomType +
                " | Room ID: " + assignedRoomId;
    }
}

// -------------------- INVENTORY SERVICE --------------------
class InventoryService {
    private Map<String, Integer> availability = new HashMap<>();

    public void setAvailability(String roomType, int count) {
        availability.put(roomType, count);
    }

    public int getAvailability(String roomType) {
        return availability.getOrDefault(roomType, 0);
    }

    public void decrementAvailability(String roomType) {
        availability.put(roomType, availability.get(roomType) - 1);
    }

    public void displayInventory() {
        System.out.println("\nCurrent Inventory:");
        for (String type : availability.keySet()) {
            System.out.println(type + " -> " + availability.get(type));
        }
    }
}

// -------------------- BOOKING REQUEST QUEUE --------------------
class BookingRequestQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation reservation) {
        queue.offer(reservation);
    }

    public Reservation dequeue() {
        return queue.poll(); // FIFO
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

// -------------------- BOOKING SERVICE (ALLOCATION LOGIC) --------------------
class BookingService {

    private InventoryService inventoryService;

    // Map Room Type -> Set of Allocated Room IDs
    private Map<String, Set<String>> allocatedRooms = new HashMap<>();

    public BookingService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public void processNextRequest(BookingRequestQueue queue) {

        Reservation reservation = queue.dequeue();

        if (reservation == null) {
            System.out.println("No pending requests.");
            return;
        }

        String roomType = reservation.getRoomType();

        // Step 1: Check Availability
        if (inventoryService.getAvailability(roomType) <= 0) {
            System.out.println("No available rooms for " + roomType);
            return;
        }

        // Step 2: Generate Unique Room ID
        String roomId = generateRoomId(roomType);

        // Step 3: Ensure uniqueness using Set
        allocatedRooms.putIfAbsent(roomType, new HashSet<>());

        if (allocatedRooms.get(roomType).contains(roomId)) {
            System.out.println("Duplicate room ID detected! Allocation aborted.");
            return;
        }

        // ---- ATOMIC LOGICAL OPERATION START ----
        allocatedRooms.get(roomType).add(roomId);
        inventoryService.decrementAvailability(roomType);
        reservation.setAssignedRoomId(roomId);
        // ---- ATOMIC LOGICAL OPERATION END ----

        // Step 4: Confirm Reservation
        System.out.println("Reservation Confirmed:");
        System.out.println(reservation);
    }

    // Unique Room ID Generator
    private String generateRoomId(String roomType) {
        return roomType.substring(0, 1).toUpperCase() + UUID.randomUUID().toString().substring(0, 5);
    }

    public void displayAllocations() {
        System.out.println("\nAllocated Rooms:");
        for (String type : allocatedRooms.keySet()) {
            System.out.println(type + " -> " + allocatedRooms.get(type));
        }
    }
}

// -------------------- MAIN APPLICATION --------------------
public class BookMyStayApp {

    public static void main(String[] args) {

        // Initialize Inventory
        InventoryService inventory = new InventoryService();
        inventory.setAvailability("Standard", 2);
        inventory.setAvailability("Deluxe", 1);

        // Create Booking Queue
        BookingRequestQueue queue = new BookingRequestQueue();
        queue.addRequest(new Reservation("Alice", "Standard"));
        queue.addRequest(new Reservation("Bob", "Standard"));
        queue.addRequest(new Reservation("Charlie", "Deluxe"));
        queue.addRequest(new Reservation("David", "Deluxe")); // Should fail (only 1 available)

        // Create Booking Service
        BookingService bookingService = new BookingService(inventory);

        // Process Requests (FIFO)
        while (!queue.isEmpty()) {
            bookingService.processNextRequest(queue);
        }

        // Display Final State
        bookingService.displayAllocations();
        inventory.displayInventory();
    }
}