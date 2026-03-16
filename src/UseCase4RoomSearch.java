/**
 * UseCase4RoomSearch - Use Case 4: Room Search & Availability Check
 *
 * <p>Implements read-only room search using RoomInventory and Room model.
 * No inventory mutation occurs during this operation.</p>
 *
 * @author BookMyStay
 * @version 4.0
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UseCase4RoomSearch {

    public static void main(String[] args) {
        RoomInventory inventory = new RoomInventory();
        inventory.registerRoomType("Single", 10);
        inventory.registerRoomType("Double", 7);
        inventory.registerRoomType("Suite", 0);

        List<Room> rooms = new ArrayList<>();
        rooms.add(new SingleRoom("Single", 1, 150, 18));
        rooms.add(new DoubleRoom("Double", 2, 250, 26));
        rooms.add(new SuiteRoom("Suite", 4, 450, 40));

        SearchService searchService = new SearchService(inventory, rooms);

        System.out.println("Welcome to Book My Stay - Hotel Booking Management System");
        System.out.println("Use Case 4: Room Search & Availability Check");
        System.out.println("Version: 4.1");
        System.out.println("---------------------------------------------------");

        List<Room> availableRooms = searchService.searchAvailableRooms();
        if (availableRooms.isEmpty()) {
            System.out.println("No rooms currently available.");
        } else {
            System.out.println("Available rooms:");
            for (Room room : availableRooms) {
                System.out.println("Room Type: " + room.getRoomType()
                        + " | Beds: " + room.getBeds()
                        + " | Price: $" + room.getPricePerNight()
                        + " | Area: " + room.getAreaSqm() + " sqm"
                        + " | Available: " + inventory.getAvailability(room.getRoomType()));
            }
        }

        System.out.println("---------------------------------------------------");
        System.out.println("Execution successful: UC4 completed and application terminated.");
    }
}

class SearchService {
    private final RoomInventory inventory;
    private final List<Room> rooms;

    public SearchService(RoomInventory inventory, List<Room> rooms) {
        this.inventory = inventory;
        this.rooms = rooms;
    }

    public List<Room> searchAvailableRooms() {
        List<Room> result = new ArrayList<>();
        for (Room room : rooms) {
            int available = inventory.getAvailability(room.getRoomType());
            if (available > 0) {
                result.add(room);
            }
        }
        return result;
    }
}

// Reuse RoomInventory from UC3
class RoomInventory {
    private final Map<String, Integer> availability = new HashMap<>();

    public void registerRoomType(String roomType, int quantity) {
        if (roomType == null || roomType.isEmpty() || quantity < 0) {
            throw new IllegalArgumentException("Invalid room type or quantity");
        }
        availability.put(roomType, quantity);
    }

    public int getAvailability(String roomType) {
        return availability.getOrDefault(roomType, 0);
    }

    public Map<String, Integer> currentAvailability() {
        return Collections.unmodifiableMap(availability);
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
