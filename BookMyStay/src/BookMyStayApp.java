import java.util.*;

// ==========================
// CUSTOM EXCEPTIONS
// ==========================
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

class InvalidRoomTypeException extends Exception {
    public InvalidRoomTypeException(String message) {
        super(message);
    }
}

class InsufficientInventoryException extends Exception {
    public InsufficientInventoryException(String message) {
        super(message);
    }
}

// ==========================
// ENTITY: Reservation
// ==========================
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    @Override
    public String toString() {
        return "Reservation ID: " + reservationId +
                ", Guest: " + guestName +
                ", Room Type: " + roomType;
    }
}

// ==========================
// INVENTORY MANAGER
// ==========================
class RoomInventory {

    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("STANDARD", 2);
        inventory.put("DELUXE", 2);
        inventory.put("SUITE", 1);
    }

    public void validateRoomType(String roomType) throws InvalidRoomTypeException {
        if (!inventory.containsKey(roomType)) {
            throw new InvalidRoomTypeException("Invalid room type: " + roomType);
        }
    }

    public void checkAvailability(String roomType) throws InsufficientInventoryException {
        int count = inventory.get(roomType);
        if (count <= 0) {
            throw new InsufficientInventoryException("No rooms available for: " + roomType);
        }
    }

    public void bookRoom(String roomType) {
        inventory.put(roomType, inventory.get(roomType) - 1);
    }

    public void printInventory() {
        System.out.println("\nCurrent Inventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + " → " + inventory.get(type));
        }
    }
}

// ==========================
// VALIDATOR (FAIL-FAST)
// ==========================
class BookingValidator {

    public static void validate(String reservationId, String guestName, String roomType)
            throws InvalidBookingException {

        if (reservationId == null || reservationId.isEmpty()) {
            throw new InvalidBookingException("Reservation ID cannot be empty.");
        }

        if (guestName == null || guestName.isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }

        if (roomType == null || roomType.isEmpty()) {
            throw new InvalidBookingException("Room type cannot be empty.");
        }
    }
}

// ==========================
// MAIN APPLICATION
// ==========================
public class BookMyStayApp {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        RoomInventory inventory = new RoomInventory();

        int choice;

        do {
            System.out.println("\n===== MENU =====");
            System.out.println("1. Book Room");
            System.out.println("2. View Inventory");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {

                case 1:
                    try {
                        // INPUT
                        System.out.print("Enter Reservation ID: ");
                        String id = scanner.nextLine();

                        System.out.print("Enter Guest Name: ");
                        String name = scanner.nextLine();

                        System.out.print("Enter Room Type (STANDARD/DELUXE/SUITE): ");
                        String roomType = scanner.nextLine().toUpperCase();

                        // STEP 1: Basic validation (fail-fast)
                        BookingValidator.validate(id, name, roomType);

                        // STEP 2: Business validation
                        inventory.validateRoomType(roomType);
                        inventory.checkAvailability(roomType);

                        // STEP 3: Process booking
                        inventory.bookRoom(roomType);

                        Reservation reservation = new Reservation(id, name, roomType);

                        System.out.println("\n✅ Booking Successful!");
                        System.out.println(reservation);

                    } catch (InvalidBookingException |
                             InvalidRoomTypeException |
                             InsufficientInventoryException e) {

                        // Graceful failure
                        System.out.println("\n❌ Booking Failed: " + e.getMessage());
                    }

                    break;

                case 2:
                    inventory.printInventory();
                    break;

                case 0:
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid choice.");
            }

        } while (choice != 0);

        scanner.close();
    }
}