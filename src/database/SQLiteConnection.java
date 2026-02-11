package database;

import java.sql.*;

public class SQLiteConnection {
    private static SQLiteConnection instance;
    private Connection connection;

    private SQLiteConnection() {
        try {
            // Updated path: This moves the .db file into the src folder
            // The "./src/" ensures it looks for the src folder relative to your project root
            String url = "jdbc:sqlite:./src/parking_lot.db"; 
            this.connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.err.println("Database Connection Error: " + e.getMessage());
        }
    }

    public static SQLiteConnection getInstance() {
        if (instance == null) {
            instance = new SQLiteConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void initializeDatabase() {
        if (connection == null) return;
        try (Statement stmt = connection.createStatement()) {
            // Create spots, tickets, and fines tables
            stmt.execute("CREATE TABLE IF NOT EXISTS ParkingSpots (SpotID TEXT PRIMARY KEY, Type TEXT, Status TEXT DEFAULT 'Available', HourlyRate REAL)");
            stmt.execute("CREATE TABLE IF NOT EXISTS ActiveTickets (TicketID TEXT PRIMARY KEY, LicensePlate TEXT, VehicleType TEXT, SpotID TEXT, EntryTime DATETIME DEFAULT CURRENT_TIMESTAMP)");
            stmt.execute("CREATE TABLE IF NOT EXISTS UnpaidFines (LicensePlate TEXT PRIMARY KEY, TotalAmount REAL DEFAULT 0.0)");
            
            // Seed sample data for Day 2 testing
            stmt.execute("INSERT OR IGNORE INTO ParkingSpots VALUES ('F1-S1', 'Compact', 'Available', 2.0)");
            stmt.execute("INSERT OR IGNORE INTO ParkingSpots VALUES ('F2-S1', 'Regular', 'Available', 5.0)");
            stmt.execute("INSERT OR IGNORE INTO ParkingSpots VALUES ('F3-S1', 'Handicapped', 'Available', 2.0)");
            
            System.out.println("Database Initialized.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}