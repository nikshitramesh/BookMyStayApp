import java.util.*;

// -------------------- Custom Exception --------------------
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// -------------------- Reservation --------------------
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

// -------------------- Inventory --------------------
class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 1);
        inventory.put("Suite Room", 1);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public boolean isValidRoomType(String roomType) {
        return inventory.containsKey(roomType);
    }

    public void decreaseRoom(String roomType) throws InvalidBookingException {
        int count = getAvailability(roomType);

        if (count <= 0) {
            throw new InvalidBookingException("No available rooms for: " + roomType);
        }

        inventory.put(roomType, count - 1);
    }

    public void displayInventory() {
        System.out.println("\nCurrent Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}

// -------------------- Validator --------------------
class BookingValidator {

    public static void validate(Reservation r, RoomInventory inventory)
            throws InvalidBookingException {

        if (r.getGuestName() == null || r.getGuestName().trim().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }

        if (r.getRoomType() == null || r.getRoomType().trim().isEmpty()) {
            throw new InvalidBookingException("Room type cannot be empty.");
        }

        if (!inventory.isValidRoomType(r.getRoomType())) {
            throw new InvalidBookingException("Invalid room type: " + r.getRoomType());
        }

        if (inventory.getAvailability(r.getRoomType()) <= 0) {
            throw new InvalidBookingException("Room not available: " + r.getRoomType());
        }
    }
}

// -------------------- Booking Service --------------------
class BookingService {

    public void bookRoom(Reservation r, RoomInventory inventory) {

        try {
            // Step 1: Validate input (Fail-fast)
            BookingValidator.validate(r, inventory);

            // Step 2: Allocate (only if valid)
            inventory.decreaseRoom(r.getRoomType());

            System.out.println("Booking successful for " + r.getGuestName() +
                    " | Room: " + r.getRoomType());

        } catch (InvalidBookingException e) {
            // Graceful failure handling
            System.out.println("Booking failed: " + e.getMessage());
        }
    }
}

// -------------------- Main Application --------------------
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("===== Book My Stay (Validation Enabled) =====");

        RoomInventory inventory = new RoomInventory();
        BookingService bookingService = new BookingService();

        // Test Cases
        Reservation r1 = new Reservation("Alice", "Single Room");   // valid
        Reservation r2 = new Reservation("", "Double Room");        // invalid name
        Reservation r3 = new Reservation("Bob", "Luxury Room");     // invalid type
        Reservation r4 = new Reservation("Charlie", "Suite Room");  // valid
        Reservation r5 = new Reservation("David", "Suite Room");    // no availability

        bookingService.bookRoom(r1, inventory);
        bookingService.bookRoom(r2, inventory);
        bookingService.bookRoom(r3, inventory);
        bookingService.bookRoom(r4, inventory);
        bookingService.bookRoom(r5, inventory);

        inventory.displayInventory();

        System.out.println("\n===== System Continues Safely =====");
    }
}