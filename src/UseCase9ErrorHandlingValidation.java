/**
 * UseCase9ErrorHandlingValidation - Use Case 9: Error Handling & Validation
 *
 * <p>Strengthens reliability with domain validation and custom exceptions.
 * Demonstrates fail-fast guard clauses for booking inputs and inventory updates.</p>
 *
 * @author BookMyStay
 * @version 9.0
 */

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class UseCase9ErrorHandlingValidation {

    public static void main(String[] args) {
        try {
            BookingService bookingService = new BookingService();

            bookingService.createRoomType("Single", 3);
            bookingService.createRoomType("Double", 2);

            bookingService.submitRequest(new Reservation("R-2001", "Alice", "Single", 2));
            bookingService.submitRequest(new Reservation("R-2002", "Bob", "Double", 1));
            bookingService.submitRequest(new Reservation("R-2003", "Carol", "Suite", 1)); // invalid type

            bookingService.processBookings();
        } catch (BookingValidationException ex) {
            System.out.println("Validation failed: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Unexpected error: " + ex.getMessage());
        }

        System.out.println("Execution completed (system remains stable).");
    }
}

class BookingService {
    private final RoomInventory inventory = new RoomInventory();
    private final Queue<Reservation> queue = new LinkedList<>();

    public void createRoomType(String roomType, int availability) {
        if (roomType == null || roomType.isBlank()) {
            throw new BookingValidationException("Room type cannot be empty");
        }
        if (availability < 0) {
            throw new BookingValidationException("Availability cannot be negative");
        }
        inventory.registerRoomType(roomType, availability);
    }

    public void submitRequest(Reservation reservation) {
        validateReservation(reservation);
        queue.offer(reservation);
    }

    private void validateReservation(Reservation reservation) {
        if (reservation == null) {
            throw new BookingValidationException("Reservation is null");
        }
        if (reservation.getReservationId() == null || reservation.getReservationId().isBlank()) {
            throw new BookingValidationException("Invalid reservation ID");
        }
        if (reservation.getGuestName() == null || reservation.getGuestName().isBlank()) {
            throw new BookingValidationException("Invalid guest name");
        }
        if (!inventory.isRegisteredRoomType(reservation.getRoomType())) {
            throw new BookingValidationException("Unknown room type: " + reservation.getRoomType());
        }
        if (reservation.getNights() <= 0) {
            throw new BookingValidationException("Nights must be at least 1");
        }
    }

    public void processBookings() {
        while (!queue.isEmpty()) {
            Reservation request = queue.poll();
            int available = inventory.getAvailability(request.getRoomType());
            if (available <= 0) {
                System.out.println("Booking rejected: no availability for " + request.getRoomType() + " (" + request.getReservationId() + ")");
                continue;
            }
            inventory.updateAvailability(request.getRoomType(), available - 1);
            System.out.println("Booking confirmed: " + request.getReservationId() + " assigned " + request.getRoomType());
        }

        System.out.println("Remaining inventory: " + inventory.currentAvailability());
    }
}

class RoomInventory {
    private final Map<String, Integer> available = new HashMap<>();

    public void registerRoomType(String roomType, int quantity) {
        available.put(roomType, quantity);
    }

    public boolean isRegisteredRoomType(String roomType) {
        return available.containsKey(roomType);
    }

    public int getAvailability(String roomType) {
        return available.getOrDefault(roomType, 0);
    }

    public void updateAvailability(String roomType, int quantity) {
        if (!isRegisteredRoomType(roomType)) {
            throw new BookingValidationException("Room type not registered: " + roomType);
        }
        if (quantity < 0) {
            throw new BookingValidationException("Inventory cannot go negative for " + roomType);
        }
        available.put(roomType, quantity);
    }

    public Map<String, Integer> currentAvailability() {
        return Map.copyOf(available);
    }
}

class Reservation {
    private final String reservationId;
    private final String guestName;
    private final String roomType;
    private final int nights;

    public Reservation(String reservationId, String guestName, String roomType, int nights) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.nights = nights;
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

    public int getNights() {
        return nights;
    }
}

class BookingValidationException extends RuntimeException {
    public BookingValidationException(String message) {
        super(message);
    }
}
