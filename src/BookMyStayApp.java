import java.util.*;

// -------------------- Custom Exception --------------------
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// -------------------- Reservation --------------------
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;
    private String roomId;
    private boolean isActive;

    public Reservation(String reservationId, String guestName, String roomType, String roomId) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.isActive = true;
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

    public String getRoomId() {
        return roomId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void cancel() {
        this.isActive = false;
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

    public boolean isValidRoomType(String roomType) {
        return inventory.containsKey(roomType);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void decreaseRoom(String roomType) throws InvalidBookingException {
        int count = getAvailability(roomType);
        if (count <= 0) {
            throw new InvalidBookingException("No rooms available for " + roomType);
        }
        inventory.put(roomType, count - 1);
    }

    public void increaseRoom(String roomType) {
        inventory.put(roomType, getAvailability(roomType) + 1);
    }

    public void displayInventory() {
        System.out.println("\nInventory Status:");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}

// -------------------- Booking History --------------------
class BookingHistory {
    private List<Reservation> history = new ArrayList<>();

    public void addReservation(Reservation r) {
        history.add(r);
    }

    public Reservation findReservation(String id) {
        for (Reservation r : history) {
            if (r.getReservationId().equals(id)) {
                return r;
            }
        }
        return null;
    }

    public void displayHistory() {
        System.out.println("\nBooking History:");
        for (Reservation r : history) {
            System.out.println(r.getReservationId() + " | " +
                    r.getGuestName() + " | " +
                    r.getRoomType() + " | Active: " + r.isActive());
        }
    }
}

// -------------------- Booking Service --------------------
class BookingService {
    private int roomCounter = 1;

    public Reservation bookRoom(String guestName, String roomType, RoomInventory inventory)
            throws InvalidBookingException {

        if (guestName == null || guestName.isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty");
        }

        if (!inventory.isValidRoomType(roomType)) {
            throw new InvalidBookingException("Invalid room type");
        }

        inventory.decreaseRoom(roomType);

        String roomId = "R" + (roomCounter++);
        String reservationId = "RES" + System.currentTimeMillis();

        Reservation r = new Reservation(reservationId, guestName, roomType, roomId);

        System.out.println("Booking Confirmed: " + reservationId + " | Room ID: " + roomId);
        return r;
    }
}

// -------------------- Cancellation Service --------------------
class CancellationService {

    private Stack<String> rollbackStack = new Stack<>();

    public void cancelBooking(String reservationId,
                              BookingHistory history,
                              RoomInventory inventory) {

        Reservation r = history.findReservation(reservationId);

        if (r == null) {
            System.out.println("Cancellation failed: Reservation not found.");
            return;
        }

        if (!r.isActive()) {
            System.out.println("Cancellation failed: Already cancelled.");
            return;
        }

        // Step 1: Push room ID to stack (rollback tracking)
        rollbackStack.push(r.getRoomId());

        // Step 2: Restore inventory
        inventory.increaseRoom(r.getRoomType());

        // Step 3: Mark reservation inactive
        r.cancel();

        System.out.println("Booking cancelled successfully: " + reservationId);
        System.out.println("Rolled back Room ID: " + rollbackStack.peek());
    }

    public void showRollbackStack() {
        System.out.println("\nRollback Stack (LIFO): " + rollbackStack);
    }
}

// -------------------- Main Class --------------------
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("===== Book My Stay (Cancellation System) =====");

        RoomInventory inventory = new RoomInventory();
        BookingHistory history = new BookingHistory();
        BookingService bookingService = new BookingService();
        CancellationService cancelService = new CancellationService();

        try {
            // Booking
            Reservation r1 = bookingService.bookRoom("Alice", "Single Room", inventory);
            Reservation r2 = bookingService.bookRoom("Bob", "Double Room", inventory);

            history.addReservation(r1);
            history.addReservation(r2);

            inventory.displayInventory();

            // Cancellation
            cancelService.cancelBooking(r1.getReservationId(), history, inventory);

            inventory.displayInventory();

            // Invalid cancellation
            cancelService.cancelBooking("INVALID_ID", history, inventory);

            // Duplicate cancellation
            cancelService.cancelBooking(r1.getReservationId(), history, inventory);

            history.displayHistory();
            cancelService.showRollbackStack();

        } catch (InvalidBookingException e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("\n===== System Stable After Rollback =====");
    }
}