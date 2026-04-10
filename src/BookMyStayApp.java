import java.util.*;

// -------------------- Room Classes --------------------
abstract class Room {
    private int beds;
    private double size;
    private double price;

    public Room(int beds, double size, double price) {
        this.beds = beds;
        this.size = size;
        this.price = price;
    }

    public abstract String getRoomType();
}

class SingleRoom extends Room {
    public SingleRoom() { super(1, 200, 1500); }
    public String getRoomType() { return "Single Room"; }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super(2, 350, 2500); }
    public String getRoomType() { return "Double Room"; }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super(3, 600, 5000); }
    public String getRoomType() { return "Suite Room"; }
}

// -------------------- Reservation --------------------
class Reservation {
    private String guestName;
    private String roomType;
    private String reservationId;

    public Reservation(String guestName, String roomType, String reservationId) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.reservationId = reservationId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

// -------------------- Inventory --------------------
class RoomInventory {
    private HashMap<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 2);
        inventory.put("Suite Room", 1);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    public void decreaseRoom(String type) {
        inventory.put(type, inventory.get(type) - 1);
    }
}

// -------------------- Booking Queue --------------------
class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.offer(r);
    }

    public Reservation getNext() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

// -------------------- Booking Service --------------------
class BookingService {
    private Set<String> allocatedRoomIds = new HashSet<>();

    public List<Reservation> confirmedReservations = new ArrayList<>();

    private String generateRoomId(String type) {
        return type.substring(0, 2).toUpperCase() + "-" + UUID.randomUUID().toString().substring(0, 4);
    }

    public void processBookings(BookingQueue queue, RoomInventory inventory) {

        while (!queue.isEmpty()) {
            Reservation req = queue.getNext();

            if (inventory.getAvailability(req.getRoomType()) > 0) {

                String roomId;
                do {
                    roomId = generateRoomId(req.getRoomType());
                } while (allocatedRoomIds.contains(roomId));

                allocatedRoomIds.add(roomId);
                inventory.decreaseRoom(req.getRoomType());

                System.out.println("Booking Confirmed for " + req.getGuestName() +
                        " | Room ID: " + roomId);

                confirmedReservations.add(req);

            } else {
                System.out.println("Booking Failed for " + req.getGuestName());
            }
        }
    }
}

// -------------------- Add-On Service --------------------
class AddOnService {
    private String name;
    private double cost;

    public AddOnService(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public double getCost() {
        return cost;
    }

    public String getName() {
        return name;
    }
}

// -------------------- Add-On Service Manager --------------------
class AddOnServiceManager {

    // Map: Reservation ID -> List of Services
    private Map<String, List<AddOnService>> serviceMap = new HashMap<>();

    // Add service to reservation
    public void addService(String reservationId, AddOnService service) {

        serviceMap.putIfAbsent(reservationId, new ArrayList<>());
        serviceMap.get(reservationId).add(service);

        System.out.println("Added service: " + service.getName() +
                " to Reservation ID: " + reservationId);
    }

    // Calculate total cost
    public double calculateTotalCost(String reservationId) {
        double total = 0;

        List<AddOnService> services = serviceMap.get(reservationId);

        if (services != null) {
            for (AddOnService s : services) {
                total += s.getCost();
            }
        }

        return total;
    }

    // Display services
    public void displayServices(String reservationId) {

        System.out.println("\nServices for Reservation ID: " + reservationId);

        List<AddOnService> services = serviceMap.get(reservationId);

        if (services == null || services.isEmpty()) {
            System.out.println("No services selected.");
            return;
        }

        for (AddOnService s : services) {
            System.out.println("- " + s.getName() + " (₹" + s.getCost() + ")");
        }

        System.out.println("Total Add-On Cost: ₹" + calculateTotalCost(reservationId));
    }
}

// -------------------- Main Application --------------------
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("===== Book My Stay =====");

        // Step 1: Setup
        RoomInventory inventory = new RoomInventory();
        BookingQueue queue = new BookingQueue();
        BookingService bookingService = new BookingService();
        AddOnServiceManager serviceManager = new AddOnServiceManager();

        // Step 2: Add booking requests
        queue.addRequest(new Reservation("Alice", "Single Room", "R1"));
        queue.addRequest(new Reservation("Bob", "Double Room", "R2"));

        // Step 3: Process bookings
        bookingService.processBookings(queue, inventory);

        // Step 4: Add Add-On Services
        serviceManager.addService("R1", new AddOnService("Breakfast", 300));
        serviceManager.addService("R1", new AddOnService("Airport Pickup", 800));
        serviceManager.addService("R2", new AddOnService("Extra Bed", 500));

        // Step 5: Display services & cost
        serviceManager.displayServices("R1");
        serviceManager.displayServices("R2");

        System.out.println("\n===== Completed =====");
    }
}