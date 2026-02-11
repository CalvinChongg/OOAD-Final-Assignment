import service.ParkingService;

public class ManualFinalizeTest {
    
    public static void main(String[] args) {
        // We create an instance of the test class and run the test
        ManualFinalizeTest test = new ManualFinalizeTest();
        test.testFinalizeParking();
    }

    public void testFinalizeParking() {
        // 1. Initialize the service using the Singleton pattern
        ParkingService service = ParkingService.getInstance(); 
        String plate = "VX 123";

        System.out.println("--- STARTING DATABASE UPDATE TEST ---");

        // 2. Prepare Exit: This does the math (Hours & RM)
        ParkingService.ExitData data = service.prepareExit(plate); 

        if (data == null) {
            System.out.println("Test Failed: Plate " + plate + " not found in ActiveTickets.");
            return;
        }

        // 3. Process Payment: This MOVES the data to the Tickets table
        boolean success = service.processPayment(
            plate, 
            data.ticketId, 
            data.parkingFee, 
            (data.overstayFine + data.misuseFine), // Combine all fines
            data.unpaidFines, 
            data.totalDue, 
            "Cash"
        );

        // 4. Verification Output
        if (success) {
            System.out.println("Calculation: RM " + data.parkingFee);
            System.out.println("Test Passed: Record moved from Active to Archive!");
        } else {
            System.out.println("Test Failed: The database rejected the update.");
        }
    }
}