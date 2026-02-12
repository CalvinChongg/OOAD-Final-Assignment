
package service;

import composite.ParkingSpot;
import database.SQLiteConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import model.*;

public class ParkingService {
    private static ParkingService instance;
    private SQLiteConnection dbManager;

    private ParkingService() {
        dbManager = SQLiteConnection.getInstance();
    }

    public static ParkingService getInstance() {
        if (instance == null) instance = new ParkingService();
        return instance;
    }

    // -------------------- ENTRY --------------------
    public List<ParkingSpot> getAvailableSpots(Vehicle vehicle) {
        List<ParkingSpot> spots = new ArrayList<>();
        String sql = "SELECT * FROM ParkingSpots WHERE status = 'Available'";
        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String typeStr = rs.getString("type");
                SpotType spotType = SpotType.valueOf(typeStr);
                if (vehicle.canPark(spotType)) {
                    ParkingSpot spot = new ParkingSpot(
                        rs.getString("floor"),
                        rs.getString("row"),
                        rs.getString("spotNum"),
                        typeStr,
                        rs.getDouble("hourlyRate")
                    );
                    spots.add(spot);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return spots;
    }

    public boolean assignSpot(String licensePlate, String spotId, String vehicleTypeStr) {
        Connection conn = dbManager.getConnection();
        try {
            conn.setAutoCommit(false);
            // Update spot status
            PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE ParkingSpots SET status = 'Occupied', currentPlate = ? WHERE spotID = ? AND status = 'Available'");
                pstmt.setString(1, licensePlate);
                pstmt.setString(2, spotId);
                int updated = pstmt.executeUpdate();
                if (updated == 0) { conn.rollback(); return false; }
                
            // Generate ticket ID: T-PLATE-TIMESTAMP
            String ticketId = "T-" + licensePlate + "-" + System.currentTimeMillis();
            PreparedStatement pstmt2 = conn.prepareStatement(
                "INSERT INTO ActiveTickets (ticketID, licensePlate, vehicleType, spotID, entryTime) VALUES (?,?,?,?,?)");
            pstmt2.setString(1, ticketId);
            pstmt2.setString(2, licensePlate);
            pstmt2.setString(3, vehicleTypeStr);
            pstmt2.setString(4, spotId);
            pstmt2.setString(5, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            pstmt2.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to assign spot");
            try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }

    // -------------------- EXIT --------------------
    public class ExitData {
        public String ticketId, spotId, entryTime, spotType;
        public double hourlyRate;
        public long hours;
        public double parkingFee;
        public double unpaidFines;
        public double overstayFine = 0;
        public double misuseFine = 0;
        public double totalDue;
    }

    public ExitData prepareExit(String licensePlate) {
        ExitData data = new ExitData();
        Connection conn = dbManager.getConnection();
        // 1. Find active ticket
        String sql = "SELECT * FROM ActiveTickets WHERE licensePlate = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, licensePlate);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) return null; // no active parking
            data.ticketId = rs.getString("ticketID");
            data.spotId = rs.getString("spotID");
            data.entryTime = rs.getString("entryTime");

            // 2. Get spot details
            PreparedStatement pstmt2 = conn.prepareStatement("SELECT * FROM ParkingSpots WHERE spotID = ?");
            pstmt2.setString(1, data.spotId);
            ResultSet rs2 = pstmt2.executeQuery();
            if (rs2.next()) {
                data.spotType = rs2.getString("type");
                data.hourlyRate = rs2.getDouble("hourlyRate");
            }

            // 3. Calculate duration (ceiling hours)
            LocalDateTime entry = LocalDateTime.parse(data.entryTime.replace(" ", "T"));
            LocalDateTime now = LocalDateTime.now();
            data.hours = ChronoUnit.MINUTES.between(entry, now) / 60;
            if (ChronoUnit.MINUTES.between(entry, now) % 60 != 0) data.hours++; // ceiling
            System.out.println(ChronoUnit.MINUTES.between(entry, now) + " total minutes");

            // 4. Parking fee
            data.parkingFee = data.hours * data.hourlyRate;
            // Handicapped discount: free if parked in Handicapped spot with card
            if (data.spotType.equals("HANDICAPPED") && hasHandicappedCard(licensePlate)) {
                data.parkingFee = 0;
            }

            // 5. Fines
            FineService fineService = FineService.getInstance();
            // Overstay (>24h)
            if (data.hours > 24) {
                data.overstayFine = fineService.calculateOverstayFine(data.hours);
            }
            // Reserved misuse (parked in reserved without VIP)
            if (data.spotType.equals("RESERVED") && !isVIP(licensePlate)) {
                data.misuseFine = 50.0; // flat fine for misuse (can be changed)
            }
            double totalFine = data.overstayFine + data.misuseFine;

            // 6. Unpaid fines from previous visits
            PreparedStatement pstmt3 = conn.prepareStatement("SELECT totalAmount FROM UnpaidFines WHERE licensePlate = ?");
            pstmt3.setString(1, licensePlate);
            ResultSet rs3 = pstmt3.executeQuery();
            if (rs3.next()) {
                data.unpaidFines = rs3.getDouble("totalAmount");
            }

            data.totalDue = data.parkingFee + totalFine + data.unpaidFines;
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    // Helper methods (simplified – in real system fetch from DB)
    private boolean hasHandicappedCard(String plate) { return false; } // would be stored with vehicle
    private boolean isVIP(String plate) { return false; }

    public boolean processPayment(String licensePlate, String ticketId, double parkingFee, double fineAmount,
                                   double unpaidFines, double amountPaid, String method) {
        Connection conn = dbManager.getConnection();
        try {
            conn.setAutoCommit(false);
            // 1. Insert into Tickets
            PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO Tickets (ticketID, licensePlate, spotID, entryTime, exitTime, parkingFee, fineAmount, totalPaid, paymentMethod) " +
                "SELECT ?, ?, spotID, entryTime, ?, ?, ?, ?, ? FROM ActiveTickets WHERE ticketID = ?");
            pstmt.setString(1, ticketId);
            pstmt.setString(2, licensePlate);
            pstmt.setString(3, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            pstmt.setDouble(4, parkingFee);
            pstmt.setDouble(5, fineAmount);
            pstmt.setDouble(6, amountPaid);
            pstmt.setString(7, method);
            pstmt.setString(8, ticketId);
            pstmt.executeUpdate();

            // 2. Remove from ActiveTickets
            PreparedStatement pstmt2 = conn.prepareStatement("DELETE FROM ActiveTickets WHERE ticketID = ?");
            pstmt2.setString(1, ticketId);
            pstmt2.executeUpdate();

            // 3. Free the parking spot
            PreparedStatement pstmt3 = conn.prepareStatement(
                "UPDATE ParkingSpots SET status = 'Available', currentPlate = NULL WHERE spotID = (SELECT spotID FROM ActiveTickets WHERE ticketID = ?)");
            pstmt3.setString(1, ticketId);
            pstmt3.executeUpdate();

            // 4. Update UnpaidFines
            double remainingFines = unpaidFines + fineAmount - (amountPaid - parkingFee);
            if (remainingFines < 0) remainingFines = 0;
            PreparedStatement pstmt4 = conn.prepareStatement(
                "REPLACE INTO UnpaidFines (licensePlate, totalAmount) VALUES (?, ?)");
            pstmt4.setString(1, licensePlate);
            pstmt4.setDouble(2, remainingFines);
            pstmt4.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }
}
