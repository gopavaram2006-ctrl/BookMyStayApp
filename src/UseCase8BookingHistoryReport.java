/**
 * UseCase8BookingHistoryReport - Use Case 8: Booking History & Reporting
 *
 * <p>Tracks confirmed reservations in insertion order and provides report generation
 * over the booking history without modifying the historical records.</p>
 *
 * @author BookMyStay
 * @version 8.0
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UseCase8BookingHistoryReport {

    public static void main(String[] args) {
        BookingHistory bookingHistory = new BookingHistory();

        bookingHistory.addConfirmedReservation(new Reservation("R-1001", "Alice", "Single", 2));
        bookingHistory.addConfirmedReservation(new Reservation("R-1002", "Bob", "Double", 3));
        bookingHistory.addConfirmedReservation(new Reservation("R-1003", "Carol", "Suite", 1));

        BookingReportService reportService = new BookingReportService(bookingHistory);

        System.out.println("Welcome to Book My Stay - Hotel Booking Management System");
        System.out.println("Use Case 8: Booking History & Reporting");
        System.out.println("Version: 8.1");
        System.out.println("---------------------------------------------------");

        System.out.println("Booking history (chronological):");
        for (Reservation reservation : bookingHistory.getAllReservations()) {
            System.out.println(reservation);
        }

        System.out.println("---------------------------------------------------");
        System.out.println("Total reservations: " + reportService.getTotalConfirmedBookings());
        System.out.println("Bookings by type: " + reportService.getBookingsByRoomType());

        System.out.println("---------------------------------------------------");
        System.out.println("Execution successful: UC8 completed and application terminated.");
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

    @Override
    public String toString() {
        return reservationId + " | " + guestName + " | " + roomType + " | " + nights + " nights";
    }
}

class BookingHistory {
    private final List<Reservation> confirmedReservations = new ArrayList<>();

    public void addConfirmedReservation(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        confirmedReservations.add(reservation);
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(confirmedReservations);
    }
}

class BookingReportService {
    private final BookingHistory bookingHistory;

    public BookingReportService(BookingHistory bookingHistory) {
        this.bookingHistory = bookingHistory;
    }

    public int getTotalConfirmedBookings() {
        return bookingHistory.getAllReservations().size();
    }

    public Map<String, Integer> getBookingsByRoomType() {
        Map<String, Integer> result = new HashMap<>();
        for (Reservation reservation : bookingHistory.getAllReservations()) {
            result.put(reservation.getRoomType(), result.getOrDefault(reservation.getRoomType(), 0) + 1);
        }
        return result;
    }
}
