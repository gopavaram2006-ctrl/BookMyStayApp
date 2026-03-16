/**
 * UseCase2RoomInitialization - Use Case 2: Basic Room Types & Static Availability
 *
 * <p>Demonstrates abstract class Room, inheritance, and static availability tracking.
 * Provides a simple console view of room types and availability.</p>
 *
 * @author BookMyStay
 * @version 2.0
 */
public class UseCase2RoomInitialization {

    public static void main(String[] args) {
        int singleAvailable = 10;
        int doubleAvailable = 7;
        int suiteAvailable = 3;

        Room singleRoom = new SingleRoom("Single", 1, 150, 18);
        Room doubleRoom = new DoubleRoom("Double", 2, 250, 26);
        Room suiteRoom = new SuiteRoom("Suite", 4, 450, 40);

        System.out.println("Welcome to Book My Stay - Hotel Booking Management System");
        System.out.println("Use Case 2: Basic Room Types & Static Availability");
        System.out.println("Version: 2.1");
        System.out.println("---------------------------------------------------");

        printRoomInfo(singleRoom, singleAvailable);
        printRoomInfo(doubleRoom, doubleAvailable);
        printRoomInfo(suiteRoom, suiteAvailable);

        System.out.println("---------------------------------------------------");
        System.out.println("Execution successful: UC2 completed and application terminated.");
    }

    private static void printRoomInfo(Room room, int available) {
        System.out.println("Room Type: " + room.getRoomType());
        System.out.println("Beds: " + room.getBeds());
        System.out.println("Price Per Night: $" + room.getPricePerNight());
        System.out.println("Area: " + room.getAreaSqm() + " sqm");
        System.out.println("Available: " + available);
        System.out.println();
    }
}

abstract class Room {
    private final String roomType;
    private final int beds;
    private final double pricePerNight;
    private final double areaSqm;

    protected Room(String roomType, int beds, double pricePerNight, double areaSqm) {
        this.roomType = roomType;
        this.beds = beds;
        this.pricePerNight = pricePerNight;
        this.areaSqm = areaSqm;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getBeds() {
        return beds;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public double getAreaSqm() {
        return areaSqm;
    }
}

class SingleRoom extends Room {
    public SingleRoom(String roomType, int beds, double pricePerNight, double areaSqm) {
        super(roomType, beds, pricePerNight, areaSqm);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom(String roomType, int beds, double pricePerNight, double areaSqm) {
        super(roomType, beds, pricePerNight, areaSqm);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom(String roomType, int beds, double pricePerNight, double areaSqm) {
        super(roomType, beds, pricePerNight, areaSqm);
    }
}
