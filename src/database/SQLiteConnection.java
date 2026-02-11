
package database;

import java.sql.*;
import model.SpotType;

public class SQLiteConnection {
    private static SQLiteConnection instance;
    private Connection connection;

    private SQLiteConnection() {
        try {
            String url = "jdbc:sqlite:./parking_lot.db";
            connection = DriverManager.getConnection(url);
            initializeTables();
            seedSampleData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static SQLiteConnection getInstance() {
        if (instance == null) instance = new SQLiteConnection();
        return instance;
    }

    public Connection getConnection() { return connection; }

    private void initializeTables() {
        try (Statement stmt = connection.createStatement()) {
            // ParkingSpots table
            stmt.execute("CREATE TABLE IF NOT EXISTS ParkingSpots (" +
                "spotID TEXT PRIMARY KEY, floor INTEGER, row INTEGER, spotNum INTEGER, " +
                "type TEXT, status TEXT DEFAULT 'Available', hourlyRate REAL, currentPlate TEXT)");
            // ActiveTickets (current parking)
            stmt.execute("CREATE TABLE IF NOT EXISTS ActiveTickets (" +
                "ticketID TEXT PRIMARY KEY, licensePlate TEXT, vehicleType TEXT, " +
                "spotID TEXT, entryTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            // Completed tickets with payment
            stmt.execute("CREATE TABLE IF NOT EXISTS Tickets (" +
                "ticketID TEXT PRIMARY KEY, licensePlate TEXT, spotID TEXT, " +
                "entryTime TIMESTAMP, exitTime TIMESTAMP, parkingFee REAL, " +
                "fineAmount REAL, totalPaid REAL, paymentMethod TEXT)");
            // Unpaid fines linked to license plate
            stmt.execute("CREATE TABLE IF NOT EXISTS UnpaidFines (" +
                "licensePlate TEXT PRIMARY KEY, totalAmount REAL DEFAULT 0.0)");
            // Fine scheme setting
            stmt.execute("CREATE TABLE IF NOT EXISTS FineSettings (" +
                "id INTEGER PRIMARY KEY CHECK (id = 1), schemeType TEXT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void seedSampleData() {
        // Insert default fine scheme (Fixed) if not exists
        try (PreparedStatement pstmt = connection.prepareStatement(
                "INSERT OR IGNORE INTO FineSettings (id, schemeType) VALUES (1, ?)")) {
            pstmt.setString(1, "FIXED");
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }

        // Create 5 floors, 2 rows each, 5 spots per row
        SpotType[] types = SpotType.values();
        double[] rates = {2.0, 5.0, 2.0, 10.0};
        String[] typeNames = {"COMPACT", "REGULAR", "HANDICAPPED", "RESERVED"};

        try (PreparedStatement pstmt = connection.prepareStatement(
                "INSERT OR IGNORE INTO ParkingSpots VALUES (?,?,?,?,?,?,?,?)")) {
            for (int f = 1; f <= 5; f++) {
                for (int r = 1; r <= 2; r++) {
                    for (int s = 1; s <= 5; s++) {
                        int typeIndex = (s - 1) % 4; // mix types
                        String spotId = "F" + f + "-R" + r + "-S" + s;
                        pstmt.setString(1, spotId);
                        pstmt.setInt(2, f);
                        pstmt.setInt(3, r);
                        pstmt.setInt(4, s);
                        pstmt.setString(5, typeNames[typeIndex]);
                        pstmt.setString(6, "Available");
                        pstmt.setDouble(7, rates[typeIndex]);
                        pstmt.setString(8, null);
                        pstmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}