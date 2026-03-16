/**
 * UseCase11ConcurrentBookingSimulation - Use Case 11: Concurrent Booking Simulation (Thread Safety)
 *
 * <p>Simulates concurrent booking requests in a thread-safe way using synchronized methods.
 * Shared queue and inventory updates are protected against race conditions.</p>
 *
 * @author BookMyStay
 * @version 11.0
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class UseCase11ConcurrentBookingSimulation {

    public static void main(String[] args) throws InterruptedException {
        ConcurrentBookingSystem system = new ConcurrentBookingSystem();
        system.registerRoomType("Single", 5);
        system.registerRoomType("Double", 3);

        // create booking requests for concurrency test
        for (int i = 1; i <= 10; i++) {
            String id = "R-11" + i;
            String roomType = (i % 2 == 0) ? "Double" : "Single";
            system.submitRequest(new Reservation(id, "Guest" + i, roomType));
        }

        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Thread t = new Thread(new BookingWorker(system), "Worker-" + (i + 1));
            workers.add(t);
            t.start();
        }

        for (Thread t : workers) {
            t.join();
        }

        System.out.println("---------------------------------------------------");
        System.out.println("Concurrent booking completed. Final inventory: " + system.getInventory());
        System.out.println("Confirmed allocations: " + system.getConfirmedReservations());
        System.out.println("Execution successful: UC11 completed and application terminated.");
    }
}

class Reservation {
    private final String reservationId;
    private final String guestName;
    private final String roomType;

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getRoomType() {
        return roomType;
    }

    @Override
    public String toString() {
        return reservationId + "(" + roomType + ")";
    }
}

class ConcurrentBookingSystem {
    private final Map<String, Integer> inventory = new HashMap<>();
    private final Queue<Reservation> queue = new LinkedList<>();
    private final Map<String, String> confirmed = new HashMap<>();

    public synchronized void registerRoomType(String roomType, int count) {
        inventory.put(roomType, count);
    }

    public synchronized void submitRequest(Reservation reservation) {
        queue.offer(reservation);
    }

    public void processNextRequest() {
        Reservation request;
        synchronized (this) {
            request = queue.poll();
            if (request == null) {
                return;
            }
        }

        synchronized (this) {
            int available = inventory.getOrDefault(request.getRoomType(), 0);
            if (available > 0) {
                inventory.put(request.getRoomType(), available - 1);
                confirmed.put(request.getReservationId(), request.getRoomType());
                System.out.println(Thread.currentThread().getName() + " confirmed " + request);
            } else {
                System.out.println(Thread.currentThread().getName() + " failed " + request + " (no availability)");
            }
        }
    }

    public synchronized Map<String, Integer> getInventory() {
        return new HashMap<>(inventory);
    }

    public synchronized Map<String, String> getConfirmedReservations() {
        return new HashMap<>(confirmed);
    }

    public synchronized boolean hasPendingRequests() {
        return !queue.isEmpty();
    }

    public synchronized int getQueueSize() {
        return queue.size();
    }
}

class BookingWorker implements Runnable {
    private final ConcurrentBookingSystem system;

    public BookingWorker(ConcurrentBookingSystem system) {
        this.system = system;
    }

    @Override
    public void run() {
        while (true) {
            system.processNextRequest();

            if (!system.hasPendingRequests()) {
                break;
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
