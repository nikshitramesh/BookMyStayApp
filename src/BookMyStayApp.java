import java.util.*;

// -------------------- Reservation --------------------
class Reservation {
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

    public void display() {
        System.out.println("ID: " + reservationId +
                " | Guest: " + guestName +
                " | Room: " + roomType);
    }
}

// -------------------- Booking History --------------------
class BookingHistory {

    // List preserves insertion order
    private List<Reservation> history = new ArrayList<>();

    // Add confirmed reservation
    public void addReservation(Reservation r) {
        history.add(r);
    }

    // Get all reservations (read-only usage expected)
    public List<Reservation> getAllReservations() {
        return history;
    }

    // Display history
    public void displayHistory() {
        System.out.println("\n===== Booking History =====");
        for (Reservation r : history) {
            r.display();
        }
    }
}

// -------------------- Booking Report Service --------------------
class BookingReportService {

    // Generate summary report
    public void generateReport(BookingHistory history) {

        List<Reservation> list = history.getAllReservations();

        System.out.println("\n===== Booking Report =====");

        // Total bookings
        System.out.println("Total Bookings: " + list.size());

        // Count per room type
        Map<String, Integer> countMap = new HashMap<>();

        for (Reservation r : list) {
            String type = r.getRoomType();
            countMap.put(type, countMap.getOrDefault(type, 0) + 1);
        }

        System.out.println("\nBookings by Room Type:");
        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}

// -------------------- Main Application --------------------
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("===== Book My Stay =====");

        // Initialize history & report service
        BookingHistory history = new BookingHistory();
        BookingReportService reportService = new BookingReportService();

        // Simulate confirmed bookings
        Reservation r1 = new Reservation("R1", "Alice", "Single Room");
        Reservation r2 = new Reservation("R2", "Bob", "Double Room");
        Reservation r3 = new Reservation("R3", "Charlie", "Single Room");

        // Add to history (in order)
        history.addReservation(r1);
        history.addReservation(r2);
        history.addReservation(r3);

        // Admin views history
        history.displayHistory();

        // Admin generates report
        reportService.generateReport(history);

        System.out.println("\n===== Completed =====");
    }
}