import java.util.HashMap;
import java.util.Map;

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

// NEW: Centralized Inventory Class
class RoomInventory {
    private HashMap<String, Integer> inventory;

    // Constructor initializes inventory
    public RoomInventory() {
        inventory = new HashMap<>();

        // Initial room availability
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 2);
    }

    // Get availability
    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    // Update availability (controlled)
    public void updateAvailability(String roomType, int newCount) {
        inventory.put(roomType, newCount);
    }

    // Book a room (decrease count safely)
    public boolean bookRoom(String roomType) {
        int available = getAvailability(roomType);

        if (available > 0) {
            inventory.put(roomType, available - 1);
            return true;
        }
        return false;
    }

    // Display all inventory
    public void displayInventory() {
        System.out.println("===== Current Room Inventory =====");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " उपलब्ध: " + entry.getValue());
        }
    }
}

// Main Application
public class BookMyStayApp {

    public static void main(String[] args) {

        // Create Room Objects
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Initialize Inventory
        RoomInventory inventory = new RoomInventory();

        System.out.println("===== Welcome to Book My Stay =====\n");

        // Display Room Details + Availability
        single.displayRoomDetails();
        System.out.println("Available: " + inventory.getAvailability(single.getRoomType()) + "\n");

        doubleRoom.displayRoomDetails();
        System.out.println("Available: " + inventory.getAvailability(doubleRoom.getRoomType()) + "\n");

        suite.displayRoomDetails();
        System.out.println("Available: " + inventory.getAvailability(suite.getRoomType()) + "\n");

        // Simulate Booking
        System.out.println("Booking a Single Room...");
        if (inventory.bookRoom("Single Room")) {
            System.out.println("Booking Successful!");
        } else {
            System.out.println("No rooms available!");
        }

        System.out.println();

        // Display Updated Inventory
        inventory.displayInventory();

        System.out.println("\n===== Thank You! =====");
    }
}