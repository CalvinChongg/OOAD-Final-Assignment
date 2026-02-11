package dao;

import database.SQLiteConnection;
import java.sql.*;

public class FineDAO {
    private Connection conn;

    public FineDAO() {
        this.conn = SQLiteConnection.getInstance().getConnection();
    }

    // Requirement: Link fine to plate number
    public double getExistingFines(String plate) {
        String sql = "SELECT TotalAmount FROM UnpaidFines WHERE LicensePlate = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, plate);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble("TotalAmount");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    // Requirement: Add fine to account if unpaid
    public void updateFine(String plate, double amount) {
        String sql = "INSERT INTO UnpaidFines (LicensePlate, TotalAmount) VALUES (?, ?) " +
                     "ON CONFLICT(LicensePlate) DO UPDATE SET TotalAmount = TotalAmount + ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, plate);
            pstmt.setDouble(2, amount);
            pstmt.setDouble(3, amount);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}