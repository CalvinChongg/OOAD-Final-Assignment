
package service;

import composite.ParkingSpot;
import database.SQLiteConnection;
import java.sql.*;
import java.time.LocalDateTime;
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

    public boolean assignSpot(String licensePlate, String spotId, String vehicleTypeStr, boolean hasCard, boolean isVIP) {
        //System.out.println("Assigning spot " + spotId + " to " + licensePlate);
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
                "INSERT INTO ActiveTickets (ticketID, licensePlate, vehicleType, spotID, entryTime) VALUES (?,?,?,?, CURRENT_TIMESTAMP)");
            pstmt2.setString(1, ticketId);
            pstmt2.setString(2, licensePlate);
            pstmt2.setString(3, vehicleTypeStr);
            pstmt2.setString(4, spotId);
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
        
        String sql = "SELECT * FROM ActiveTickets WHERE licensePlate = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, licensePlate);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) return null;

            data.ticketId = rs.getString("ticketID");
            data.spotId = rs.getString("spotID");
            data.entryTime = rs.getString("entryTime");
            String vehicleType = rs.getString("vehicleType");

            // 1. Get Spot Details
            PreparedStatement pstmt2 = conn.prepareStatement("SELECT * FROM ParkingSpots WHERE spotID = ?");
            pstmt2.setString(1, data.spotId);
            ResultSet rs2 = pstmt2.executeQuery();
            if (rs2.next()) {
                data.spotType = rs2.getString("type");
                data.hourlyRate = rs2.getDouble("hourlyRate");
            }

            // 2. Calculate Duration (Ceiling Hours)
            LocalDateTime entry = LocalDateTime.parse(data.entryTime.replace(" ", "T"));
            LocalDateTime now = LocalDateTime.now();
            data.hours = ChronoUnit.MINUTES.between(entry, now) / 60;
            if (ChronoUnit.MINUTES.between(entry, now) % 60 != 0) data.hours++;

            // 3. Apply Rate Strategy
            RateStrategy rateStrategy;
            if (vehicleType.equalsIgnoreCase("Handicapped")) {
                rateStrategy = new HandicappedRateStrategy(hasHandicappedCard(licensePlate), data.spotType);
            } else {
                rateStrategy = new StandardRateStrategy(data.hourlyRate);
            }
            data.parkingFee = rateStrategy.calculateFee((int) data.hours);

            // 4. Fine Detection & Calculation
            FineService fineService = FineService.getInstance();

            // A. Overstay Fine (>24h)
            if (data.hours > 24) {
                data.overstayFine = fineService.calculateOverstayFine((int) data.hours);
            }

            // B. Misuse Fines (Wrong Spot Detection)
            if (data.spotType.equals("RESERVED") && !isVIP(licensePlate)) {
                data.misuseFine = 50.0; // Flat fine for non-VIP in Reserved spot
            } else if (data.spotType.equals("HANDICAPPED") && !hasHandicappedCard(licensePlate)) {
                data.misuseFine = 100.0; // Heavier fine for parking in OKU spot illegally
            }

            // 5. Unpaid Fines check
            PreparedStatement pstmt3 = conn.prepareStatement("SELECT totalAmount FROM UnpaidFines WHERE licensePlate = ?");
            pstmt3.setString(1, licensePlate);
            ResultSet rs3 = pstmt3.executeQuery();
            if (rs3.next()) {
                data.unpaidFines = rs3.getDouble("totalAmount");
            }

            // Final total calculation including all dynamic fines
            data.totalDue = data.parkingFee + data.overstayFine + data.misuseFine + data.unpaidFines;
            
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
                "SELECT ?, ?, spotID, entryTime, CURRENT_TIMESTAMP, ?, ?, ?, ? FROM ActiveTickets WHERE ticketID = ?");
            pstmt.setString(1, ticketId);
            pstmt.setString(2, licensePlate);
            pstmt.setDouble(3, parkingFee);
            pstmt.setDouble(4, fineAmount);
            pstmt.setDouble(5, amountPaid);
            pstmt.setString(6, method);
            pstmt.setString(7, ticketId);
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

    public boolean finalizeTransaction(String licensePlate, String paymentMethod) {
        Connection conn = dbManager.getConnection();
        try {
            // 1. Start a Transaction to ensure data integrity
            conn.setAutoCommit(false);

            // 2. Fetch the "Active" data (The 'Brain' part)
            ExitData data = prepareExit(licensePlate);
            if (data == null) {
                System.out.println("Error: No active parking found for " + licensePlate);
                return false;
            }

            // 3. Move to 'Tickets' and record the final calculation
            String insertSql = "INSERT INTO Tickets (ticketID, licensePlate, spotID, entryTime, exitTime, parkingFee, fineAmount, totalPaid, paymentMethod) " +
                            "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setString(1, data.ticketId);
                pstmt.setString(2, licensePlate);
                pstmt.setString(3, data.spotId);
                pstmt.setString(4, data.entryTime);
                pstmt.setDouble(5, data.parkingFee); // The RM 85.0 you saw earlier
                pstmt.setDouble(6, data.overstayFine + data.misuseFine);
                pstmt.setDouble(7, data.totalDue);
                pstmt.setString(8, paymentMethod);
                pstmt.executeUpdate();
            }

            // 4. Delete from 'ActiveTickets'
            PreparedStatement deleteActive = conn.prepareStatement("DELETE FROM ActiveTickets WHERE ticketID = ?");
            deleteActive.setString(1, data.ticketId);
            deleteActive.executeUpdate();

            // 5. Release the Spot (Set to Available)
            PreparedStatement releaseSpot = conn.prepareStatement("UPDATE ParkingSpots SET status = 'Available', currentPlate = NULL WHERE spotID = ?");
            releaseSpot.setString(1, data.spotId);
            releaseSpot.executeUpdate();

            // 6. Commit all changes
            conn.commit();
            System.out.println("Biller Manager: Successfully finalized bill for " + licensePlate);
            return true;

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }

    private boolean isVIP(String plate) {
        String sql = "SELECT isVIP FROM Vehicles WHERE licensePlate = ?";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, plate);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("isVIP") == 1; // Assuming 1 is true
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // Helper to check for a valid Handicapped/OKU status
    private boolean hasHandicappedCard(String plate) {
        String sql = "SELECT hasOKUCard FROM Vehicles WHERE licensePlate = ?";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, plate);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("hasOKUCard") == 1;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}
