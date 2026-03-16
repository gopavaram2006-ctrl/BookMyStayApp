/**
 * UseCase5BookingRequestQueue - Use Case 5: Booking Request (First-Come-First-Served)
 *
 * <p>Implements reservation intake via a FIFO queue without performing allocation.
 * This captures booking intent in arrival order and avoids inventory changes at this stage.</p>
 *
 * @author BookMyStay
 * @version 5.0
 */

import java.util.LinkedList;
import java.util.Queue;

public class UseCase5BookingRequestQueue {

    public static void main(String[] args) {
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        bookingQueue.submitRequest(new Reservation("Alice", "Single", 2));
        bookingQueue.submitRequest(new Reservation("Bob", "Double", 3));
        bookingQueue.submitRequest(new Reservation("Carol", "Suite", 1));

        System.out.println("Welcome to Book My Stay - Hotel Booking Management System");
        System.out.println("Use Case 5: Booking Request (First-Come-First-Served)");
        System.out.println("Version: 5.1");
        System.out.println("---------------------------------------------------");

        System.out.println("Pending booking requests in FIFO order:");
        for (Reservation req : bookingQueue.getPendingRequests()) {
            System.out.println(req);
        }

        System.out.println("---------------------------------------------------");
        System.out.println("Queue size: " + bookingQueue.pendingCount());
        System.out.println("No inventory updates performed during request intake.");
        System.out.println("Execution successful: UC5 completed and application terminated.");
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

    @Override
    public String toString() {
        return "Reservation[guest=" + guestName + ", room=" + roomType + ", nights=" + nights + "]";
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

    public int pendingCount() {
        return queue.size();
    }

    public Queue<Reservation> getPendingRequests() {
        return new LinkedList<>(queue);
    }
}
