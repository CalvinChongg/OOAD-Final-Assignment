
package service;

import database.SQLiteConnection;
import java.sql.*;

public class FineService {
    private static FineService instance;
    private SQLiteConnection dbManager;

    private FineService() { dbManager = SQLiteConnection.getInstance(); }
    public static FineService getInstance() {
        if (instance == null) instance = new FineService();
        return instance;
    }

    public String getCurrentScheme() {
        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT schemeType FROM FineSettings WHERE id = 1")) {
            if (rs.next()) return rs.getString("schemeType");
        } catch (SQLException e) { e.printStackTrace(); }
        return "FIXED";
    }

    public void setScheme(String scheme) {
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(
                "UPDATE FineSettings SET schemeType = ? WHERE id = 1")) {
            pstmt.setString(1, scheme);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public double calculateOverstayFine(long hoursOver24) {
        String scheme = getCurrentScheme();
        switch (scheme) {
            case "FIXED": return 50.0;
            case "PROGRESSIVE":
            if (hoursOver24 <= 24) return 50.0;
            else if (hoursOver24 <= 48) return 150.0; // 50+100
            else if (hoursOver24 <= 72) return 300.0; // 50+100+150
            else return 500.0; // 50+100+150+200
            case "HOURLY": return hoursOver24 * 20.0;
            case "HOURLY-CAP": {
                double hourlyFine = hoursOver24 * 20.0;
                return Math.min(hourlyFine, 500.0);
            }
            default: return 0;
        }
    }
}
