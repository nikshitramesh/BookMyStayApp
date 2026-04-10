abstract class Room {
    private int beds;
    private double size;
    private double price;

    public Room(int beds, double size, double price) {
        this.beds = beds;
        this.size = size;
        this.price = price;
    }

    // Getters (Encapsulation)
    public int getBeds() {
        return beds;
    }

    public double getSize() {
        return size;
    }

    public double getPrice() {
        return price;
    }

    // Abstract method
    public abstract String getRoomType();

    // Common method
    public void displayRoomDetails() {
        System.out.println("Room Type: " + getRoomType());
        System.out.println("Beds: " + beds);
        System.out.println("Size: " + size + " sq.ft");
        System.out.println("Price: ₹" + price);
    }
}

// Single Room Class
class SingleRoom extends Room {
    public SingleRoom() {
        super(1, 200, 1500);
    }

    @Override
    public String getRoomType() {
        return "Single Room";
    }
}

// Double Room Class
class DoubleRoom extends Room {
    public DoubleRoom() {
        super(2, 350, 2500);
    }

    @Override
    public String getRoomType() {
        return "Double Room";
    }
}

// Suite Room Class
class SuiteRoom extends Room {
    public SuiteRoom() {
        super(3, 600, 5000);
    }

    @Override
    public String getRoomType() {
        return "Suite Room";
    }
}

// Main Class
public class BookMyStayApp {

    public static void main(String[] args) {

        // Creating room objects (Polymorphism)
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Static availability
        int singleAvailable = 5;
        int doubleAvailable = 3;
        int suiteAvailable = 2;

        System.out.println("===== Welcome to Book My Stay =====\n");

        single.displayRoomDetails();
        System.out.println("Available: " + singleAvailable + "\n");

        doubleRoom.displayRoomDetails();
        System.out.println("Available: " + doubleAvailable + "\n");

        suite.displayRoomDetails();
        System.out.println("Available: " + suiteAvailable + "\n");

        System.out.println("===== Thank You! =====");
    }
}