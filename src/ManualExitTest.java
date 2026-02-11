import service.*;

public class ManualExitTest {
    public static void main(String[] args) {
        String plate = "VX 123"; // Using the plate from your ActiveTickets
        
        ParkingService parkingService = ParkingService.getInstance();
        FineService fineService = FineService.getInstance();

        // 1. Prepare Exit (This fetches entry time and spot info)
        ParkingService.ExitData data = parkingService.prepareExit(plate);

        if (data != null) {
            System.out.println("--- MANUAL CALCULATION CHECK ---");
            System.out.println("Plate: " + plate + " | Spot: " + data.spotId);
            System.out.println("Hours Stayed: " + data.hours);
            
            // 2. Verify the Math manually
            System.out.println("Calculated Parking Fee: RM " + data.parkingFee);
            System.out.println("Calculated Overstay Fine: RM " + data.overstayFine);
            System.out.println("Total Due: RM " + data.totalDue);
            
            // 3. Confirm against Database Rates
            // VX 123 is in F1-R1-S2, which is REGULAR (RM 5/h)
            double expectedFee = data.hours * 5.0; 
            System.out.println("Manual Verification: " + (data.parkingFee == expectedFee ? "PASS" : "FAIL"));
        } else {
            System.out.println("Vehicle " + plate + " not found in ActiveTickets!");
        }
    }
}