/**
 * UseCase6RoomAllocationService - Use Case 6: Reservation Confirmation & Room Allocation
 *
 * <p>Processes queued booking requests FIFO, allocates unique room IDs,
 * decrements inventory, and prevents double-booking.</p>
 *
 * @author BookMyStay
 * @version 6.0
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

public class UseCase6RoomAllocationService {

    public static void main(String[] args) {
        BookingRequestQueue requestQueue = new BookingRequestQueue();
        requestQueue.submitRequest(new Reservation("Alice", "Single", 2));
        requestQueue.submitRequest(new Reservation("Bob", "Double", 3));
        requestQueue.submitRequest(new Reservation("Carol", "Suite", 1));

        RoomInventory inventory = new RoomInventory();
        inventory.registerRoomType("Single", 5);
        inventory.registerRoomType("Double", 3);
        inventory.registerRoomType("Suite", 1);

        RoomAllocationService allocationService = new RoomAllocationService(inventory);

        System.out.println("Welcome to Book My Stay - Hotel Booking Management System");
        System.out.println("Use Case 6: Reservation Confirmation & Room Allocation");
        System.out.println("Version: 6.1");
        System.out.println("---------------------------------------------------");

        while (!requestQueue.isEmpty()) {
            Reservation request = requestQueue.pollRequest();
            boolean allocated = allocationService.confirmReservation(request);
            System.out.println(request.getGuestName() + " reservation for " + request.getRoomType()
                    + " (" + request.getNights() + " nights) "
                    + (allocated ? "confirmed" : "failed (no availability)"));
        }

        System.out.println("---------------------------------------------------");
        allocationService.printAllocationStatus();
        System.out.println("---------------------------------------------------");
        System.out.println("Final inventory:");
        inventory.currentAvailability().forEach((roomType, available) ->
                System.out.println(roomType + " remaining: " + available));

        System.out.println("---------------------------------------------------");
        System.out.println("Execution successful: UC6 completed and application terminated.");
    }
}

class RoomAllocationService {
    private final RoomInventory inventory;
    private final Set<String> allocatedRoomIds = new HashSet<>();
    private final Map<String, Set<String>> allocationByRoomType = new HashMap<>();

    public RoomAllocationService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public boolean confirmReservation(Reservation reservation) {
        if (reservation == null || reservation.getRoomType() == null) {
            return false;
        }

        int available = inventory.getAvailability(reservation.getRoomType());
        if (available <= 0) {
            return false;
        }

        String roomId = generateUniqueRoomId(reservation.getRoomType());
        allocatedRoomIds.add(roomId);

        allocationByRoomType.computeIfAbsent(reservation.getRoomType(), k -> new HashSet<>()).add(roomId);

        inventory.updateAvailability(reservation.getRoomType(), available - 1);

        return true;
    }

    private String generateUniqueRoomId(String roomType) {
        String roomId;
        do {
            roomId = roomType.substring(0, 1).toUpperCase() + "-" + UUID.randomUUID().toString();
        } while (allocatedRoomIds.contains(roomId));
        return roomId;
    }

    public void printAllocationStatus() {
        System.out.println("Allocated room IDs by type:");
        allocationByRoomType.forEach((roomType, ids) -> {
            System.out.println(roomType + " => " + ids);
        });
    }
}

class Reservation {
    private final String guestName;
    private final String roomType;
    private final int nights;

    public Reservation(String guestName, String roomType, int nights) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.nights = nights;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getNights() {
        return nights;
    }
}

class BookingRequestQueue {
    private final Queue<Reservation> queue = new LinkedList<>();

    public void submitRequest(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        queue.offer(reservation);
    }

    public Reservation pollRequest() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
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

    public Map<String, Integer> currentAvailability() {
        return Map.copyOf(availability);
    }
}
