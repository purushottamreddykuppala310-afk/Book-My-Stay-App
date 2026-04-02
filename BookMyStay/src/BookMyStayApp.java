import java.io.*;
import java.util.*;

// ==========================
// ENTITY: Reservation (Serializable)
// ==========================
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reservationId;
    private String guestName;
    private String roomType;
    private String roomId;
    private boolean isCancelled;

    public Reservation(String reservationId, String guestName, String roomType, String roomId) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.isCancelled = false;
    }

    public String getReservationId() { return reservationId; }
    public String getRoomType() { return roomType; }
    public String getRoomId() { return roomId; }
    public boolean isCancelled() { return isCancelled; }

    public void cancel() { this.isCancelled = true; }

    @Override
    public String toString() {
        return "ID: " + reservationId +
                ", Guest: " + guestName +
                ", RoomType: " + roomType +
                ", RoomID: " + roomId +
                ", Status: " + (isCancelled ? "CANCELLED" : "CONFIRMED");
    }
}

// ==========================
// ROOM INVENTORY (Serializable)
// ==========================
class RoomInventory implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Integer> inventory = new HashMap<>();
    private Map<String, Stack<String>> roomPool = new HashMap<>();

    public RoomInventory() {
        inventory.put("STANDARD", 2);
        inventory.put("DELUXE", 2);
        inventory.put("SUITE", 1);

        roomPool.put("STANDARD", new Stack<>());
        roomPool.put("DELUXE", new Stack<>());
        roomPool.put("SUITE", new Stack<>());

        // Preload room IDs
        roomPool.get("STANDARD").push("S1");
        roomPool.get("STANDARD").push("S2");

        roomPool.get("DELUXE").push("D1");
        roomPool.get("DELUXE").push("D2");

        roomPool.get("SUITE").push("SU1");
    }

    public String allocateRoom(String roomType) throws Exception {
        if (!inventory.containsKey(roomType)) throw new Exception("Invalid room type");
        if (inventory.get(roomType) <= 0) throw new Exception("No rooms available");

        inventory.put(roomType, inventory.get(roomType) - 1);
        return roomPool.get(roomType).pop();
    }

    public void releaseRoom(String roomType, String roomId) {
        roomPool.get(roomType).push(roomId);
        inventory.put(roomType, inventory.get(roomType) + 1);
    }

    public void printInventory() {
        System.out.println("\nInventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + " → " + inventory.get(type));
        }
    }
}

// ==========================
// BOOKING HISTORY (Serializable)
// ==========================
class BookingHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Reservation> bookings = new HashMap<>();

    public void addReservation(Reservation r) { bookings.put(r.getReservationId(), r); }
    public Reservation getReservation(String id) { return bookings.get(id); }
    public void printAll() {
        System.out.println("\n=== BOOKINGS ===");
        for (Reservation r : bookings.values()) System.out.println(r);
    }
}

// ==========================
// CANCELLATION SERVICE
// ==========================
class CancellationService {
    public void cancelBooking(String reservationId, BookingHistory history, RoomInventory inventory) throws Exception {
        Reservation r = history.getReservation(reservationId);
        if (r == null) throw new Exception("Reservation not found.");
        if (r.isCancelled()) throw new Exception("Booking already cancelled.");

        inventory.releaseRoom(r.getRoomType(), r.getRoomId());
        r.cancel();
    }
}

// ==========================
// PERSISTENCE SERVICE
// ==========================
class PersistenceService {

    private static final String FILE_NAME = "hotel_state.dat";

    public static void save(RoomInventory inventory, BookingHistory history) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(inventory);
            oos.writeObject(history);
            System.out.println("\n💾 System state saved successfully!");
        } catch (IOException e) {
            System.out.println("❌ Error saving state: " + e.getMessage());
        }
    }

    public static Object[] load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            RoomInventory inventory = (RoomInventory) ois.readObject();
            BookingHistory history = (BookingHistory) ois.readObject();
            System.out.println("♻️ System state restored successfully!");
            return new Object[]{inventory, history};
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("⚠️ No previous state found, starting fresh.");
            return null;
        }
    }
}

// ==========================
// MAIN APPLICATION
// ==========================
public class BookMyStayApp {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Load persisted state if available
        Object[] state = PersistenceService.load();
        RoomInventory inventory = (state != null) ? (RoomInventory) state[0] : new RoomInventory();
        BookingHistory history = (state != null) ? (BookingHistory) state[1] : new BookingHistory();
        CancellationService cancelService = new CancellationService();

        int choice;

        do {
            System.out.println("\n===== MENU =====");
            System.out.println("1. Book Room");
            System.out.println("2. Cancel Booking");
            System.out.println("3. View Bookings");
            System.out.println("4. View Inventory");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    try {
                        System.out.print("Reservation ID: "); String id = scanner.nextLine();
                        System.out.print("Guest Name: "); String name = scanner.nextLine();
                        System.out.print("Room Type (STANDARD/DELUXE/SUITE): "); String type = scanner.nextLine().toUpperCase();

                        String roomId = inventory.allocateRoom(type);
                        Reservation r = new Reservation(id, name, type, roomId);
                        history.addReservation(r);

                        System.out.println("\n✅ Booking Confirmed!"); System.out.println(r);
                    } catch (Exception e) { System.out.println("\n❌ Booking Failed: " + e.getMessage()); }
                    break;

                case 2:
                    try {
                        System.out.print("Reservation ID to cancel: ");
                        String cancelId = scanner.nextLine();
                        cancelService.cancelBooking(cancelId, history, inventory);
                        System.out.println("\n✅ Booking Cancelled!");
                    } catch (Exception e) { System.out.println("\n❌ Cancellation Failed: " + e.getMessage()); }
                    break;

                case 3: history.printAll(); break;
                case 4: inventory.printInventory(); break;
                case 0:
                    // Persist state on exit
                    PersistenceService.save(inventory, history);
                    System.out.println("Exiting...");
                    break;
                default: System.out.println("Invalid choice."); break;
            }

        } while (choice != 0);

        scanner.close();
    }
}