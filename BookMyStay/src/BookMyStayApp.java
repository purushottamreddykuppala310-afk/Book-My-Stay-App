import java.util.*;

// ==========================
// ENTITY: Add-On Service
// ==========================
class AddOnService {
    private String serviceId;
    private String name;
    private double price;

    public AddOnService(String serviceId, String name, double price) {
        this.serviceId = serviceId;
        this.name = name;
        this.price = price;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return name + " (₹" + price + ")";
    }
}

// ==========================
// MANAGER: Add-On Services
// ==========================
class AddOnServiceManager {

    private Map<String, List<AddOnService>> reservationServicesMap = new HashMap<>();

    public void addService(String reservationId, AddOnService service) {
        reservationServicesMap
                .computeIfAbsent(reservationId, k -> new ArrayList<>())
                .add(service);
    }

    public void removeService(String reservationId, String serviceId) {
        List<AddOnService> services = reservationServicesMap.get(reservationId);
        if (services != null) {
            services.removeIf(service -> service.getServiceId().equals(serviceId));
        }
    }

    public List<AddOnService> getServices(String reservationId) {
        return reservationServicesMap.getOrDefault(reservationId, new ArrayList<>());
    }

    public double calculateTotalServiceCost(String reservationId) {
        double total = 0;
        for (AddOnService service : getServices(reservationId)) {
            total += service.getPrice();
        }
        return total;
    }

    public void printServices(String reservationId) {
        List<AddOnService> services = getServices(reservationId);

        if (services.isEmpty()) {
            System.out.println("No add-on services selected.");
            return;
        }

        System.out.println("Selected Add-On Services:");
        for (AddOnService service : services) {
            System.out.println("- " + service);
        }
    }
}

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
}

// ==========================
// MAIN CLASS (IMPORTANT)
// ==========================
public class BookMyStayApp {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Create reservation
        System.out.print("Enter Reservation ID: ");
        String resId = scanner.nextLine();

        System.out.print("Enter Guest Name: ");
        String guestName = scanner.nextLine();

        System.out.print("Enter Base Price: ");
        double basePrice = scanner.nextDouble();

        Reservation reservation = new Reservation(resId, guestName, basePrice);
        AddOnServiceManager manager = new AddOnServiceManager();

        // Predefined services
        AddOnService breakfast = new AddOnService("S1", "Breakfast", 500);
        AddOnService pickup = new AddOnService("S2", "Airport Pickup", 1200);
        AddOnService spa = new AddOnService("S3", "Spa Access", 2000);

        int choice;

        do {
            System.out.println("\n===== MENU =====");
            System.out.println("1. Add Service");
            System.out.println("2. Remove Service");
            System.out.println("3. View Services");
            System.out.println("4. View Total Cost");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            choice = scanner.nextInt();

            switch (choice) {

                case 1:
                    System.out.println("\nAvailable Services:");
                    System.out.println("1. Breakfast (₹500)");
                    System.out.println("2. Airport Pickup (₹1200)");
                    System.out.println("3. Spa Access (₹2000)");
                    System.out.print("Select service: ");
                    int s = scanner.nextInt();

                    if (s == 1) manager.addService(resId, breakfast);
                    else if (s == 2) manager.addService(resId, pickup);
                    else if (s == 3) manager.addService(resId, spa);
                    else System.out.println("Invalid choice");

                    break;

                case 2:
                    System.out.print("Enter Service ID to remove (S1/S2/S3): ");
                    String removeId = scanner.next();
                    manager.removeService(resId, removeId);
                    break;

                case 3:
                    manager.printServices(resId);
                    break;

                case 4:
                    double addOnCost = manager.calculateTotalServiceCost(resId);
                    double total = reservation.getBasePrice() + addOnCost;

                    System.out.println("\nBase Price: ₹" + reservation.getBasePrice());
                    System.out.println("Add-On Cost: ₹" + addOnCost);
                    System.out.println("Total Cost: ₹" + total);
                    break;

                case 0:
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid choice");
            }

        } while (choice != 0);

        scanner.close();
    }
}