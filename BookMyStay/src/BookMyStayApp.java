import java.util.HashMap;
import java.util.Map;

/**
 * Abstract Room class
 */
abstract class Room {
    private int beds;
    private double price;
    private String type;

    public Room(String type, int beds, double price) {
        this.type = type;
        this.beds = beds;
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void displayDetails() {
        System.out.println("Room Type : " + type);
        System.out.println("Beds      : " + beds);
        System.out.println("Price     : " + price);
    }
}

// Room Types
class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 1000);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 2000);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 3, 5000);
    }
}

/**
 * RoomInventory - Centralized Inventory Management
 */
class RoomInventory {

    private HashMap<String, Integer> availability;

    // Constructor → initialize inventory
    public RoomInventory() {
        availability = new HashMap<>();

        availability.put("Single Room", 5);
        availability.put("Double Room", 3);
        availability.put("Suite Room", 2);
    }

    // Get availability
    public int getAvailability(String roomType) {
        return availability.getOrDefault(roomType, 0);
    }

    // Update availability (controlled)
    public void updateAvailability(String roomType, int count) {
        availability.put(roomType, count);
    }

    // Display full inventory
    public void displayInventory() {
        System.out.println("\n=== Current Room Availability ===");
        for (Map.Entry<String, Integer> entry : availability.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}

/**
 * Main Application
 */
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("========= Book My Stay =========");

        // Create room objects
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();

        // Display room details + availability
        System.out.println("\n--- Single Room ---");
        single.displayDetails();
        System.out.println("Available : " + inventory.getAvailability(single.getType()));

        System.out.println("\n--- Double Room ---");
        doubleRoom.displayDetails();
        System.out.println("Available : " + inventory.getAvailability(doubleRoom.getType()));

        System.out.println("\n--- Suite Room ---");
        suite.displayDetails();
        System.out.println("Available : " + inventory.getAvailability(suite.getType()));

        // Display centralized inventory
        inventory.displayInventory();
    }
}