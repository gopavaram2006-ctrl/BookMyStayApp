/**
 * UseCase10BookingCancellation - Use Case 10: Booking Cancellation & Inventory Rollback
 *
 * <p>Implements safe cancellation with rollback using stack to restore the last allocated room IDs,
 * update inventory, and maintain booking history and cancellation state.</p>
 *
 * @author BookMyStay
 * @version 10.0
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class UseCase10BookingCancellation {

    public static void main(String[] args) {
        AllocationSystem allocationSystem = new AllocationSystem();

        allocationSystem.registerRoomType("Single", 2);
        allocationSystem.registerRoomType("Double", 2);

        allocationSystem.confirmBooking("R-3001", "Alice", "Single");
        allocationSystem.confirmBooking("R-3002", "Bob", "Double");

        System.out.println("====================================");
        System.out.println("Before cancellation inventory: " + allocationSystem.getInventoryStatus());

        // Cancel the most recently confirmed booking first (LIFO rollback)
        allocationSystem.cancelBooking("R-3002");

        System.out.println("====================================");
        System.out.println("After cancellation inventory: " + allocationSystem.getInventoryStatus());
        System.out.println("Current reservations: " + allocationSystem.getActiveReservations());
        System.out.println("Cancelled reservations: " + allocationSystem.getCancelledReservations());

        System.out.println("====================================");
        System.out.println("Execution successful: UC10 completed and application terminated.");
    }
}

class AllocationSystem {
    private final RoomInventory inventory = new RoomInventory();
    private final Map<String, String> reservationRoomType = new HashMap<>();
    private final Map<String, String> allocationIdByReservation = new HashMap<>();
    private final Map<String, String> reservationByAllocation = new HashMap<>();
    private final Stack<String> rollbackStack = new Stack<>();
    private final List<String> cancelledReservations = new ArrayList<>();

    public void registerRoomType(String roomType, int available) {
        inventory.registerRoomType(roomType, available);
    }

    public void confirmBooking(String reservationId, String guestName, String roomType) {
        if (reservationId == null || reservationId.isBlank() || roomType == null || roomType.isBlank()) {
            throw new IllegalArgumentException("Invalid reservation or room type");
        }
        if (!inventory.isRegisteredRoomType(roomType)) {
            throw new IllegalArgumentException("Unknown room type: " + roomType);
        }
        if (inventory.getAvailability(roomType) <= 0) {
            throw new IllegalStateException("No availability for room type: " + roomType);
        }

        String allocatedId = roomType.substring(0, 1).toUpperCase() + "-" + reservationId;

        if (allocationIdByReservation.containsKey(reservationId) || reservationByAllocation.containsKey(allocatedId)) {
            throw new IllegalStateException("Duplicate reservation or allocation");
        }

        allocationIdByReservation.put(reservationId, allocatedId);
        reservationRoomType.put(reservationId, roomType);
        reservationByAllocation.put(allocatedId, reservationId);
        rollbackStack.push(allocatedId);
        inventory.updateAvailability(roomType, inventory.getAvailability(roomType) - 1);
    }

    public void cancelBooking(String reservationId) {
        if (reservationId == null || reservationId.isBlank()) {
            throw new IllegalArgumentException("Reservation ID is required");
        }
        if (!allocationIdByReservation.containsKey(reservationId)) {
            System.out.println("Cancellation rejected: reservation " + reservationId + " does not exist or is already cancelled.");
            return;
        }

        String allocatedId = allocationIdByReservation.remove(reservationId);
        String roomType = reservationRoomType.remove(reservationId);
        reservationByAllocation.remove(allocatedId);

        if (rollbackStack.isEmpty() || !rollbackStack.peek().equals(allocatedId)) {
            throw new IllegalStateException("Rollback stack inconsistency for allocation " + allocatedId);
        }

        rollbackStack.pop();
        cancelledReservations.add(reservationId);
        inventory.updateAvailability(roomType, inventory.getAvailability(roomType) + 1);

        System.out.println("Cancellation accepted: " + reservationId + " (" + roomType + ") rolled back using allocation ID " + allocatedId);
    }

    public Map<String, Integer> getInventoryStatus() {
        return inventory.currentAvailability();
    }

    public List<String> getActiveReservations() {
        return new ArrayList<>(reservationRoomType.keySet());
    }

    public List<String> getCancelledReservations() {
        return new ArrayList<>(cancelledReservations);
    }
}

class RoomInventory {
    private final Map<String, Integer> availability = new HashMap<>();

    public void registerRoomType(String roomType, int quantity) {
        if (roomType == null || roomType.isBlank()) {
            throw new IllegalArgumentException("Room type must not be empty");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Initial availability cannot be negative");
        }
        availability.put(roomType, quantity);
    }

    public boolean isRegisteredRoomType(String roomType) {
        return availability.containsKey(roomType);
    }

    public int getAvailability(String roomType) {
        return availability.getOrDefault(roomType, 0);
    }

    public void updateAvailability(String roomType, int quantity) {
        if (!isRegisteredRoomType(roomType)) {
            throw new IllegalArgumentException("Room type not registered: " + roomType);
        }
        if (quantity < 0) {
            throw new IllegalStateException("Inventory cannot be negative for " + roomType);
        }
        availability.put(roomType, quantity);
    }

    public Map<String, Integer> currentAvailability() {
        return Map.copyOf(availability);
    }
}
