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

    public int getBeds() {
        return beds;
    }

    public double getSize() {
        return size;
    }

    public double getPrice() {
        return price;
    }

    public abstract String getRoomType();

    public void displayRoomDetails() {
        System.out.println("Room Type: " + getRoomType());
        System.out.println("Beds: " + beds);
        System.out.println("Size: " + size + " sq.ft");
        System.out.println("Price: ₹" + price);
    }
}

// Concrete Room Classes
class SingleRoom extends Room {
    public SingleRoom() {
        super(1, 200, 1500);
    }

    @Override
    public String getRoomType() {
        return "Single Room";
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super(2, 350, 2500);
    }

    @Override
    public String getRoomType() {
        return "Double Room";
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super(3, 600, 5000);
    }

    @Override
    public String getRoomType() {
        return "Suite Room";
    }
}

// Centralized Inventory (same as Use Case 3)
class RoomInventory {
    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 0); // Example: unavailable
    }

    // Read-only access
    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    // Expose full inventory (read-only usage expected)
    public Map<String, Integer> getAllInventory() {
        return inventory;
    }
}

// NEW: Search Service (Read-Only)
class RoomSearchService {

    public void searchAvailableRooms(List<Room> rooms, RoomInventory inventory) {

        System.out.println("===== Available Rooms =====\n");

        for (Room room : rooms) {

            int available = inventory.getAvailability(room.getRoomType());

            // Validation: show only available rooms
            if (available > 0) {
                room.displayRoomDetails();
                System.out.println("Available: " + available + "\n");
            }
        }
    }
}

// Main Application
public class BookMyStayApp {

    public static void main(String[] args) {

        // Room objects (Domain Model)
        List<Room> rooms = new ArrayList<>();
        rooms.add(new SingleRoom());
        rooms.add(new DoubleRoom());
        rooms.add(new SuiteRoom());

        // Inventory (State Holder)
        RoomInventory inventory = new RoomInventory();

        // Search Service (Read-only)
        RoomSearchService searchService = new RoomSearchService();

        System.out.println("===== Welcome to Book My Stay =====\n");

        // Guest performs search
        searchService.searchAvailableRooms(rooms, inventory);

        System.out.println("===== Search Completed (No Data Modified) =====");
    }
}