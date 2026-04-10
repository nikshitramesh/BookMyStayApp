import java.io.*;
import java.util.*;

// -------------------- Reservation --------------------
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
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

    @Override
    public String toString() {
        return reservationId + " | " + guestName + " | " + roomType;
    }
}

// -------------------- Inventory --------------------
class RoomInventory implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 1);
    }

    public boolean allocateRoom(String roomType) {
        int count = inventory.getOrDefault(roomType, 0);
        if (count > 0) {
            inventory.put(roomType, count - 1);
            return true;
        }
        return false;
    }

    public void displayInventory() {
        System.out.println("\nInventory:");
        for (Map.Entry<String, Integer> e : inventory.entrySet()) {
            System.out.println(e.getKey() + " -> " + e.getValue());
        }
    }
}

// -------------------- Booking History --------------------
class BookingHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Reservation> bookings = new ArrayList<>();

    public void addReservation(Reservation r) {
        bookings.add(r);
    }

    public List<Reservation> getBookings() {
        return bookings;
    }

    public void display() {
        System.out.println("\nBooking History:");
        for (Reservation r : bookings) {
            System.out.println(r);
        }
    }
}

// -------------------- Persistence Service --------------------
class PersistenceService {

    private static final String FILE_NAME = "hotel_data.ser";

    // Save data
    public void save(RoomInventory inventory, BookingHistory history) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            out.writeObject(inventory);
            out.writeObject(history);

            System.out.println("\nData saved successfully.");

        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    // Load data
    public Object[] load() {
        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(FILE_NAME))) {

            RoomInventory inventory = (RoomInventory) in.readObject();
            BookingHistory history = (BookingHistory) in.readObject();

            System.out.println("Data loaded successfully.");
            return new Object[]{inventory, history};

        } catch (FileNotFoundException e) {
            System.out.println("No previous data found. Starting fresh.");
        } catch (Exception e) {
            System.out.println("Data corrupted. Resetting system.");
        }

        // fallback (safe state)
        return new Object[]{new RoomInventory(), new BookingHistory()};
    }
}

// -------------------- Main Class --------------------
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("===== Book My Stay (Persistence Enabled) =====");

        PersistenceService persistence = new PersistenceService();

        // Step 1: Load previous state
        Object[] data = persistence.load();
        RoomInventory inventory = (RoomInventory) data[0];
        BookingHistory history = (BookingHistory) data[1];

        // Step 2: Perform operations
        String resId = "RES" + System.currentTimeMillis();

        if (inventory.allocateRoom("Single Room")) {
            Reservation r = new Reservation(resId, "Alice", "Single Room");
            history.addReservation(r);
            System.out.println("Booking Done: " + r);
        } else {
            System.out.println("No rooms available.");
        }

        inventory.displayInventory();
        history.display();

        // Step 3: Save state before shutdown
        persistence.save(inventory, history);

        System.out.println("\n===== Restart App to See Recovery =====");
    }
}