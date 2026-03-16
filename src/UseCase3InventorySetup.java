/**
 * UseCase3InventorySetup - Use Case 3: Centralized Room Inventory Management
 *
 * <p>Implements a centralized inventory using HashMap in order to provide a
 * single source of truth for room availability in Book My Stay app.</p>
 *
 * @author BookMyStay
 * @version 3.0
 */
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UseCase3InventorySetup {

    public static void main(String[] args) {
        RoomInventory inventory = new RoomInventory();
        inventory.registerRoomType("Single", 10);
        inventory.registerRoomType("Double", 7);
        inventory.registerRoomType("Suite", 3);

        System.out.println("Welcome to Book My Stay - Hotel Booking Management System");
        System.out.println("Use Case 3: Centralized Room Inventory Management");
        System.out.println("Version: 3.1");
        System.out.println("---------------------------------------------------");

        printInventory(inventory);

        System.out.println("\n-- Update after booking one Double room and one Suite room --");
        inventory.decrementAvailability("Double");
        inventory.decrementAvailability("Suite");

        System.out.println("---------------------------------------------------");
        printInventory(inventory);

        System.out.println("---------------------------------------------------");
        System.out.println("Execution successful: UC3 completed and application terminated.");
    }

    private static void printInventory(RoomInventory inventory) {
        for (Map.Entry<String, Integer> entry : inventory.currentAvailability().entrySet()) {
            System.out.println("Room Type: " + entry.getKey() + " | Available: " + entry.getValue());
        }
    }
}

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

    public void updateAvailability(String roomType, int newQuantity) {
        if (!availability.containsKey(roomType)) {
            throw new IllegalArgumentException("Room type not registered: " + roomType);
        }
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        availability.put(roomType, newQuantity);
    }

    public void decrementAvailability(String roomType) {
        int current = getAvailability(roomType);
        if (current <= 0) {
            throw new IllegalStateException("No available rooms for type: " + roomType);
        }
        availability.put(roomType, current - 1);
    }

    public Map<String, Integer> currentAvailability() {
        return Collections.unmodifiableMap(availability);
    }
}
