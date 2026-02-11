
package service;

import database.SQLiteConnection;
import java.sql.*;
import java.util.*;

public class ReportService {
    private SQLiteConnection dbManager = SQLiteConnection.getInstance();

    public List<String[]> getCurrentVehicles() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT a.licensePlate, a.vehicleType, a.spotID, a.entryTime, p.type " +
                     "FROM ActiveTickets a JOIN ParkingSpots p ON a.spotID = p.spotID";
        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("licensePlate"),
                    rs.getString("vehicleType"),
                    rs.getString("spotID"),
                    rs.getString("type"),
                    rs.getString("entryTime")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public double getTotalRevenue() {
        double revenue = 0;
        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT SUM(totalPaid) FROM Tickets")) {
            if (rs.next()) revenue = rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return revenue;
    }

    public Map<String, Integer> getOccupancyByType() {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT type, COUNT(*) as total, SUM(CASE WHEN status='Occupied' THEN 1 ELSE 0 END) as occ " +
                     "FROM ParkingSpots GROUP BY type";
        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("type"), rs.getInt("occ"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    public List<String[]> getOutstandingFines() {
        List<String[]> list = new ArrayList<>();
        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT licensePlate, totalAmount FROM UnpaidFines WHERE totalAmount > 0")) {
            while (rs.next()) {
                list.add(new String[]{ rs.getString("licensePlate"), String.valueOf(rs.getDouble("totalAmount")) });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void testTicketCalculation() {
        // This simulates how your ReportingPanel would read the archived data
        try (Connection conn = SQLiteConnection.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT SUM(totalPaid) FROM Tickets")) {
            
            if (rs.next()) {
                double revenue = rs.getDouble(1);
                System.out.println("Total Revenue in Tickets Archive: RM " + revenue);
                // Expected for this sample: RM 410.00
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
