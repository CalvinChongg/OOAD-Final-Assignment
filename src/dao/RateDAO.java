package dao;

import database.SQLiteConnection;
import java.sql.*;

public class RateDAO {
    private Connection conn;

    public RateDAO() {
        this.conn = SQLiteConnection.getInstance().getConnection();
    }

    public double getHourlyRateForSpot(String spotId) {
        String sql = "SELECT HourlyRate FROM ParkingSpots WHERE SpotID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, spotId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble("HourlyRate");
        } catch (SQLException e) { e.printStackTrace(); }
        return 5.0; // Default to Regular rate if not found
    }
}