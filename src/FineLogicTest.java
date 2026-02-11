import model.*;

public class FineLogicTest {
    public static void main(String[] args) {
        int overstayHours = 30; // 6 hours past the 24h limit
        
        // Testing Option A: Fixed
        FineStrategy fixed = new FixedFineStrategy();
        System.out.println("Fixed Fine for 30h: RM " + fixed.calculateFine(overstayHours));

        // Testing Option B: Progressive
        FineStrategy progressive = new ProgressiveFineStrategy();
        System.out.println("Progressive Fine for 30h: RM " + progressive.calculateFine(overstayHours));

        // Testing Option C: Hourly
        FineStrategy hourly = new HourlyFineStrategy();
        System.out.println("Hourly Fine for 30h (6h over): RM " + hourly.calculateFine(overstayHours));
    }
}