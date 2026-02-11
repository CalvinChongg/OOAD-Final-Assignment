package database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteConnection {
    private static SQLiteConnection instance;
    private Connection connection;

    private SQLiteConnection() {
        try {
            File dbDir = new File("database");
            if (!dbDir.exists()) dbDir.mkdir();

            String url = "jdbc:sqlite:database/parking_lot.db";
            this.connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
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
            stmt.execute("CREATE TABLE IF NOT EXISTS ParkingSpots (SpotID TEXT PRIMARY KEY, Type TEXT, Status TEXT DEFAULT 'Available', HourlyRate REAL)");
            stmt.execute("CREATE TABLE IF NOT EXISTS ActiveTickets (TicketID TEXT PRIMARY KEY, LicensePlate TEXT, VehicleType TEXT, SpotID TEXT, EntryTime DATETIME DEFAULT CURRENT_TIMESTAMP)");
            stmt.execute("CREATE TABLE IF NOT EXISTS UnpaidFines (LicensePlate TEXT PRIMARY KEY, TotalAmount REAL DEFAULT 0.0)");
            System.out.println("Database initialized.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}