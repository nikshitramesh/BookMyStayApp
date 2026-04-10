import java.util.*;

// Abstract Room Class
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

// Concrete Room Classes
class SingleRoom extends Room {
    public SingleRoom() {
        super(1, 200, 1500);
    }
    public String getRoomType() {
        return "Single Room";
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super(2, 350, 2500);
    }
    public String getRoomType() {
        return "Double Room";
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super(3, 600, 5000);
    }
    public String getRoomType() {
        return "Suite Room";
    }
}

// Reservation (Booking Request)
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

// Inventory Service
class RoomInventory {
    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 2);
        inventory.put("Suite Room", 1);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void decreaseRoom(String roomType) {
        int count = inventory.getOrDefault(roomType, 0);
        if (count > 0) {
            inventory.put(roomType, count - 1);
        }
    }

    public void displayInventory() {
        System.out.println("\n===== Updated Inventory =====");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " Available: " + entry.getValue());
        }
    }
}

// Booking Queue (FIFO)
class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.offer(r);
    }

    public Reservation getNextRequest() {
        return queue.poll(); // FIFO
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

// Booking Service (Core Logic)
class BookingService {

    // Track all allocated room IDs (uniqueness)
    private Set<String> allocatedRoomIds = new HashSet<>();

    // Map room type -> allocated room IDs
    private HashMap<String, Set<String>> allocationMap = new HashMap<>();

    // Generate unique room ID
    private String generateRoomId(String roomType) {
        return roomType.substring(0, 2).toUpperCase() + "-" + UUID.randomUUID().toString().substring(0, 4);
    }

    // Process booking requests
    public void processBookings(BookingQueue queue, RoomInventory inventory) {

        System.out.println("\n===== Processing Bookings =====");

        while (!queue.isEmpty()) {
            Reservation req = queue.getNextRequest();

            String type = req.getRoomType();
            String guest = req.getGuestName();

            if (inventory.getAvailability(type) > 0) {

                // Generate unique ID
                String roomId;
                do {
                    roomId = generateRoomId(type);
                } while (allocatedRoomIds.contains(roomId));

                // Add to set (prevent reuse)
                allocatedRoomIds.add(roomId);

                // Map allocation
                allocationMap.putIfAbsent(type, new HashSet<>());
                allocationMap.get(type).add(roomId);

                // Decrease inventory (atomic step)
                inventory.decreaseRoom(type);

                // Confirm booking
                System.out.println("Booking Confirmed!");
                System.out.println("Guest: " + guest + " | Room: " + type + " | Room ID: " + roomId);

            } else {
                System.out.println("Booking Failed for " + guest + " (No rooms available)");
            }
        }
    }

    // Display all allocations
    public void displayAllocations() {
        System.out.println("\n===== Room Allocations =====");
        for (Map.Entry<String, Set<String>> entry : allocationMap.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}

// Main Application
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("===== Welcome to Book My Stay =====");

        // Initialize services
        RoomInventory inventory = new RoomInventory();
        BookingQueue queue = new BookingQueue();
        BookingService bookingService = new BookingService();

        // Add booking requests (FIFO)
        queue.addRequest(new Reservation("Alice", "Single Room"));
        queue.addRequest(new Reservation("Bob", "Single Room"));
        queue.addRequest(new Reservation("Charlie", "Single Room")); // should fail
        queue.addRequest(new Reservation("David", "Suite Room"));

        // Process bookings
        bookingService.processBookings(queue, inventory);

        // Show results
        bookingService.displayAllocations();
        inventory.displayInventory();

        System.out.println("\n===== Process Completed =====");
    }
}