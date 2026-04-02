import java.util.*;

// ==========================
// ENTITY: Reservation
// ==========================
class Reservation {
    private String reservationId;
    private String guestName;
    private double basePrice;

    public Reservation(String reservationId, String guestName, double basePrice) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.basePrice = basePrice;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public double getBasePrice() {
        return basePrice;
    }

    @Override
    public String toString() {
        return "Reservation ID: " + reservationId +
                ", Guest: " + guestName +
                ", Price: ₹" + basePrice;
    }
}

// ==========================
// BOOKING HISTORY
// ==========================
class BookingHistory {

    // Stores confirmed bookings in order
    private List<Reservation> confirmedBookings = new ArrayList<>();

    // Add confirmed reservation
    public void addReservation(Reservation reservation) {
        confirmedBookings.add(reservation);
    }

    // Get all reservations
    public List<Reservation> getAllReservations() {
        return new ArrayList<>(confirmedBookings); // return copy (safe)
    }
}

// ==========================
// REPORT SERVICE
// ==========================
class BookingReportService {

    // Print all bookings
    public void printAllBookings(List<Reservation> reservations) {
        if (reservations.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        System.out.println("\n=== BOOKING HISTORY ===");
        for (Reservation r : reservations) {
            System.out.println(r);
        }
    }

    // Total revenue
    public void printTotalRevenue(List<Reservation> reservations) {
        double total = 0;
        for (Reservation r : reservations) {
            total += r.getBasePrice();
        }

        System.out.println("\nTotal Revenue: ₹" + total);
    }

    // Total bookings
    public void printTotalBookings(List<Reservation> reservations) {
        System.out.println("\nTotal Bookings: " + reservations.size());
    }

    // Find booking by ID
    public void findBookingById(List<Reservation> reservations, String id) {
        for (Reservation r : reservations) {
            if (r.getReservationId().equals(id)) {
                System.out.println("\nFound: " + r);
                return;
            }
        }
        System.out.println("\nBooking not found.");
    }
}

// ==========================
// MAIN APPLICATION
// ==========================
public class BookMyStayApp {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        BookingHistory history = new BookingHistory();
        BookingReportService reportService = new BookingReportService();

        int choice;

        do {
            System.out.println("\n===== MENU =====");
            System.out.println("1. Confirm Booking");
            System.out.println("2. View Booking History");
            System.out.println("3. Total Revenue");
            System.out.println("4. Total Bookings");
            System.out.println("5. Search Booking");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {

                case 1:
                    // Simulate booking confirmation
                    System.out.print("Enter Reservation ID: ");
                    String id = scanner.nextLine();

                    System.out.print("Enter Guest Name: ");
                    String name = scanner.nextLine();

                    System.out.print("Enter Base Price: ");
                    double price = scanner.nextDouble();
                    scanner.nextLine();

                    Reservation reservation = new Reservation(id, name, price);

                    // Add to history
                    history.addReservation(reservation);

                    System.out.println("Booking confirmed and stored.");
                    break;

                case 2:
                    reportService.printAllBookings(history.getAllReservations());
                    break;

                case 3:
                    reportService.printTotalRevenue(history.getAllReservations());
                    break;

                case 4:
                    reportService.printTotalBookings(history.getAllReservations());
                    break;

                case 5:
                    System.out.print("Enter Reservation ID to search: ");
                    String searchId = scanner.nextLine();
                    reportService.findBookingById(history.getAllReservations(), searchId);
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