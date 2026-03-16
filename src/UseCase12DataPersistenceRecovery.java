/**
 * UseCase12DataPersistenceRecovery - Use Case 12: Data Persistence & System Recovery
 *
 * <p>Demonstrates file-based persistence and recovery of inventory + booking history.
 * Includes corruption handling and safe fallback to default state.</p>
 *
 * Note: This program uses simple Text file serialization for clarity.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UseCase12DataPersistenceRecovery {
    private static final String PERSISTENCE_FILE = "bookmyStay_state.txt";

    public static void main(String[] args) {
        PersistenceService persistence = new PersistenceService(PERSISTENCE_FILE);
        BookingSystem bookingSystem;

        try {
            bookingSystem = persistence.restore();
            System.out.println("State restored from file.");
        } catch (IOException | IllegalStateException ex) {
            System.out.println("Loading failed (" + ex.getMessage() + "), starting with empty state.");
            bookingSystem = new BookingSystem();
            bookingSystem.registerRoomType("Single", 3);
            bookingSystem.registerRoomType("Double", 2);
        }

        // operate some changes
        bookingSystem.confirmBooking("R-4001", "Alice", "Single");
        bookingSystem.confirmBooking("R-4002", "Bob", "Double");

        System.out.println("Current inventory: " + bookingSystem.getInventory());
        System.out.println("Booking history: " + bookingSystem.getBookingHistory());

        try {
            persistence.save(bookingSystem);
            System.out.println("State saved successfully.");
        } catch (IOException ex) {
            System.out.println("Failed to save state: " + ex.getMessage());
        }

        System.out.println("Execution successful: UC12 completed and application terminated.");
    }
}

class BookingSystem {
    private final Map<String, Integer> inventory = new HashMap<>();
    private final List<String> bookingHistory = new ArrayList<>();

    public void registerRoomType(String roomType, int count) {
        inventory.put(roomType, count);
    }

    public void confirmBooking(String reservationId, String guest, String roomType) {
        int available = inventory.getOrDefault(roomType, 0);
        if (available <= 0) {
            throw new IllegalStateException("No availability for " + roomType);
        }
        inventory.put(roomType, available - 1);
        bookingHistory.add(reservationId + ":" + guest + ":" + roomType);
    }

    public Map<String, Integer> getInventory() {
        return new HashMap<>(inventory);
    }

    public List<String> getBookingHistory() {
        return new ArrayList<>(bookingHistory);
    }

    public Map<String, Integer> loadInventory(Map<String, Integer> data) {
        inventory.clear();
        inventory.putAll(data);
        return inventory;
    }

    public List<String> loadHistory(List<String> data) {
        bookingHistory.clear();
        bookingHistory.addAll(data);
        return bookingHistory;
    }
}

class PersistenceService {
    private final String path;

    public PersistenceService(String path) {
        this.path = path;
    }

    public void save(BookingSystem system) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write("#inventory");
            writer.newLine();
            for (Map.Entry<String, Integer> entry : system.getInventory().entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
            writer.write("#history");
            writer.newLine();
            for (String r : system.getBookingHistory()) {
                writer.write(r);
                writer.newLine();
            }
        }
    }

    public BookingSystem restore() throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new IOException("Persistence file not found: " + path);
        }

        BookingSystem system = new BookingSystem();
        Map<String, Integer> inv = new HashMap<>();
        List<String> history = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean inInventory = false;
            boolean inHistory = false;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (line.equals("#inventory")) {
                    inInventory = true;
                    inHistory = false;
                    continue;
                }
                if (line.equals("#history")) {
                    inInventory = false;
                    inHistory = true;
                    continue;
                }
                if (inInventory) {
                    String[] parts = line.split(":" );
                    if (parts.length != 2) throw new IllegalStateException("Invalid inventory format");
                    inv.put(parts[0], Integer.parseInt(parts[1]));
                } else if (inHistory) {
                    history.add(line);
                }
            }
        }

        system.loadInventory(inv);
        system.loadHistory(history);
        return system;
    }
}
