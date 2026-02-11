package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class Ticket {
    private String ticketId;
    private String licensePlate;
    private String spotId;
    private String vehicleType;
    private LocalDateTime entryTime;
    private boolean hasHandicappedCard;

    public Ticket(String plate, String spotId, String vehicleType, boolean hasCard) {
        // Generating a unique ID based on plate and current timestamp
        this.ticketId = "T-" + plate + "-" + System.currentTimeMillis();
        this.licensePlate = plate;
        this.spotId = spotId;
        this.vehicleType = vehicleType;
        this.entryTime = LocalDateTime.now();
        this.hasHandicappedCard = hasCard;
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

    // Getters for the BillingManager to use later
    public String getSpotId() { return spotId; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public String getVehicleType() { return vehicleType; }
    public boolean hasHandicappedCard() { return hasHandicappedCard; }
    public String getTicketId() { return ticketId; }
}