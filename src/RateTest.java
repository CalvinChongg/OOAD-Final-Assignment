import model.*;

public class RateTest {
    public static void main(String[] args) {
        int hours = 3; 
        
        // This will now resolve if StandardRateStrategy.java is in the model folder
        RateStrategy regular = new StandardRateStrategy(5.0);
        System.out.println("Regular Fee (3h): RM " + regular.calculateFee(hours));

        RateStrategy handicapped = new HandicappedRateStrategy();
        System.out.println("Handicapped Fee (3h): RM " + handicapped.calculateFee(hours));
    }
}