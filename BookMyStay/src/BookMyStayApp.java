import java.util.*;

// -------------------- DOMAIN MODEL --------------------
class Room {
    private String type;
    private double price;
    private String amenities;
    private int capacity;

    public Room(String type, double price, String amenities, int capacity) {
        this.type = type;
        this.price = price;
        this.amenities = amenities;
        this.capacity = capacity;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public String getAmenities() {
        return amenities;
    }

    public int getCapacity() {
        return capacity;
    }

    public void displayDetails() {
        System.out.println("Room Type: " + type);
        System.out.println("Price: $" + price);
        System.out.println("Capacity: " + capacity + " persons");
        System.out.println("Amenities: " + amenities);
        System.out.println("---------------------------");
    }
}

// -------------------- INVENTORY (STATE HOLDER) --------------------
class Inventory {
    private Map<String, Integer> availability = new HashMap<>();

    public void setAvailability(String roomType, int count) {
        availability.put(roomType, count);
    }

    // Read-only method
    public int getAvailability(String roomType) {
        return availability.getOrDefault(roomType, 0);
    }

    public Set<String> getAllRoomTypes() {
        return availability.keySet();
    }
}

// -------------------- SEARCH SERVICE (READ-ONLY ACCESS) --------------------
class SearchService {
    private Inventory inventory;
    private Map<String, Room> roomCatalog;

    public SearchService(Inventory inventory, Map<String, Room> roomCatalog) {
        this.inventory = inventory;
        this.roomCatalog = roomCatalog;
    }

    // Read-only search operation
    public void searchAvailableRooms() {
        System.out.println("Available Rooms:");
        System.out.println("=================");

        for (String type : inventory.getAllRoomTypes()) {

            int availableCount = inventory.getAvailability(type);

            // Defensive Programming: Only show if availability > 0
            if (availableCount > 0 && roomCatalog.containsKey(type)) {

                Room room = roomCatalog.get(type);

                room.displayDetails();
                System.out.println("Available Rooms: " + availableCount);
                System.out.println("=================");
            }
        }
    }
}

// -------------------- MAIN APPLICATION --------------------
public class BookMyStayApp {

    public static void main(String[] args) {

        // Create Room Domain Objects
        Room standard = new Room("Standard", 100.0, "WiFi, TV", 2);
        Room deluxe = new Room("Deluxe", 180.0, "WiFi, TV, Mini Bar", 3);
        Room suite = new Room("Suite", 300.0, "WiFi, TV, Mini Bar, Jacuzzi", 4);

        // Store Room objects in catalog
        Map<String, Room> roomCatalog = new HashMap<>();
        roomCatalog.put(standard.getType(), standard);
        roomCatalog.put(deluxe.getType(), deluxe);
        roomCatalog.put(suite.getType(), suite);

        // Initialize Inventory (State Holder)
        Inventory inventory = new Inventory();
        inventory.setAvailability("Standard", 5);
        inventory.setAvailability("Deluxe", 2);
        inventory.setAvailability("Suite", 0); // Will not be shown

        // Create Search Service
        SearchService searchService = new SearchService(inventory, roomCatalog);

        // Guest initiates search
        searchService.searchAvailableRooms();

        // Inventory remains unchanged (No mutation)
    }
}