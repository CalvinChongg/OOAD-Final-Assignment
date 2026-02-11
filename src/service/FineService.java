package service;

import database.SQLiteConnection;
import java.sql.*; // Use your Strategy classes
import model.*;

public class FineService {
    private static FineService instance;
    private SQLiteConnection dbManager;

    private FineService() { dbManager = SQLiteConnection.getInstance(); }
    public static FineService getInstance() {
        if (instance == null) instance = new FineService();
        return instance;
    }

    // Fetches which strategy to use from the database
    public FineStrategy getActiveStrategy() {
        String scheme = "FIXED"; // Default
        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT schemeType FROM FineSettings WHERE id = 1")) {
            if (rs.next()) scheme = rs.getString("schemeType");
        } catch (SQLException e) { e.printStackTrace(); }

        // AUTOMATED: Returns YOUR strategy objects based on DB setting
        switch (scheme) {
            case "PROGRESSIVE": return new ProgressiveFineStrategy();
            case "HOURLY": return new HourlyFineStrategy();
            default: return new FixedFineStrategy();
        }
    }

    public double calculateOverstayFine(int hours) {
        if (hours <= 24) return 0.0;
        // This now uses your actual Strategy logic!
        return getActiveStrategy().calculateFine(hours);
    }
}