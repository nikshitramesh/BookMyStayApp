import java.util.*;

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

// -------------------- Thread-Safe Inventory --------------------
class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 1);
    }

    // Critical Section (synchronized)
    public synchronized boolean allocateRoom(String roomType) {
        int available = inventory.getOrDefault(roomType, 0);

        if (available > 0) {
            // Simulate delay (to expose race condition if not synchronized)
            try { Thread.sleep(100); } catch (InterruptedException e) {}

            inventory.put(roomType, available - 1);
            return true;
        }
        return false;
    }

    public void displayInventory() {
        System.out.println("\nFinal Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}

// -------------------- Shared Booking Queue --------------------
class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public synchronized void addRequest(Reservation r) {
        queue.add(r);
        notify(); // notify waiting threads
    }

    public synchronized Reservation getRequest() {
        while (queue.isEmpty()) {
            try {
                wait(); // wait until request comes
            } catch (InterruptedException e) {}
        }
        return queue.poll();
    }
}

// -------------------- Booking Processor (Thread) --------------------
class BookingProcessor extends Thread {

    private BookingQueue queue;
    private RoomInventory inventory;

    public BookingProcessor(BookingQueue queue, RoomInventory inventory, String name) {
        super(name);
        this.queue = queue;
        this.inventory = inventory;
    }

    @Override
    public void run() {
        // Each thread processes 2 requests
        for (int i = 0; i < 2; i++) {

            Reservation r = queue.getRequest();

            synchronized (inventory) { // critical section

                boolean success = inventory.allocateRoom(r.getRoomType());

                if (success) {
                    System.out.println(Thread.currentThread().getName() +
                            " booked " + r.getRoomType() +
                            " for " + r.getGuestName());
                } else {
                    System.out.println(Thread.currentThread().getName() +
                            " FAILED for " + r.getGuestName() +
                            " (No rooms available)");
                }
            }
        }
    }
}

// -------------------- Main Class --------------------
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("===== Concurrent Booking Simulation =====");

        RoomInventory inventory = new RoomInventory();
        BookingQueue queue = new BookingQueue();

        // Simulate multiple booking requests
        queue.addRequest(new Reservation("Alice", "Single Room"));
        queue.addRequest(new Reservation("Bob", "Single Room"));
        queue.addRequest(new Reservation("Charlie", "Single Room"));
        queue.addRequest(new Reservation("David", "Double Room"));
        queue.addRequest(new Reservation("Eve", "Double Room"));

        // Multiple threads (guests)
        BookingProcessor t1 = new BookingProcessor(queue, inventory, "Thread-1");
        BookingProcessor t2 = new BookingProcessor(queue, inventory, "Thread-2");
        BookingProcessor t3 = new BookingProcessor(queue, inventory, "Thread-3");

        // Start threads
        t1.start();
        t2.start();
        t3.start();

        // Wait for all threads to finish
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {}

        inventory.displayInventory();

        System.out.println("\n===== Thread-Safe Execution Completed =====");
    }
}