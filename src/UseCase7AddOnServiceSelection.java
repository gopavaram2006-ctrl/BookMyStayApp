/**
 * UseCase7AddOnServiceSelection - Use Case 7: Add-On Service Selection
 *
 * <p>Extends reservation model with optional service selection. No update to
 * booking or inventory occurs.</p>
 *
 * @author BookMyStay
 * @version 7.0
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UseCase7AddOnServiceSelection {

    public static void main(String[] args) {
        Reservation reservation1 = new Reservation("R-1001", "Alice", "Single", 2);
        Reservation reservation2 = new Reservation("R-1002", "Bob", "Double", 3);

        AddOnServiceManager manager = new AddOnServiceManager();

        Service breakfast = new Service("Breakfast", 30);
        Service airportPickup = new Service("Airport Pickup", 50);
        Service lateCheckout = new Service("Late Checkout", 20);

        manager.addServiceToReservation(reservation1.getReservationId(), breakfast);
        manager.addServiceToReservation(reservation1.getReservationId(), lateCheckout);

        manager.addServiceToReservation(reservation2.getReservationId(), airportPickup);

        System.out.println("Welcome to Book My Stay - Hotel Booking Management System");
        System.out.println("Use Case 7: Add-On Service Selection");
        System.out.println("Version: 7.1");
        System.out.println("---------------------------------------------------");

        printReservationServices(reservation1, manager);
        printReservationServices(reservation2, manager);

        System.out.println("---------------------------------------------------");
        System.out.println("Execution successful: UC7 completed and application terminated.");
    }

    private static void printReservationServices(Reservation reservation, AddOnServiceManager manager) {
        System.out.println("Reservation ID: " + reservation.getReservationId());
        System.out.println("Guest: " + reservation.getGuestName());
        System.out.println("Room: " + reservation.getRoomType() + " (" + reservation.getNights() + " nights)");

        List<Service> services = manager.getServices(reservation.getReservationId());
        if (services.isEmpty()) {
            System.out.println("Add-on services: none");
            System.out.println("Additional cost: $0");
        } else {
            double total = 0;
            System.out.println("Add-on services:");
            for (Service service : services) {
                System.out.println("  - " + service.getName() + " ($" + service.getPrice() + ")");
                total += service.getPrice();
            }
            System.out.println("Total add-on cost: $" + total);
        }
        System.out.println();
    }
}

class Service {
    private final String name;
    private final double price;

    public Service(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
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

class AddOnServiceManager {
    private final Map<String, List<Service>> servicesByReservation = new HashMap<>();

    public void addServiceToReservation(String reservationId, Service service) {
        if (reservationId == null || reservationId.isEmpty() || service == null) {
            throw new IllegalArgumentException("Invalid reservation or service");
        }
        servicesByReservation.computeIfAbsent(reservationId, k -> new ArrayList<>()).add(service);
    }

    public List<Service> getServices(String reservationId) {
        return new ArrayList<>(servicesByReservation.getOrDefault(reservationId, List.of()));
    }

    public double calculateTotalAddOnCost(String reservationId) {
        return getServices(reservationId).stream().mapToDouble(Service::getPrice).sum();
    }
}
