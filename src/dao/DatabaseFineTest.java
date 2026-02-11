import dao.FineDAO;

public class DatabaseFineTest {
    public static void main(String[] args) {
        FineDAO dao = new FineDAO();
        String plate = "WYY1234";

        // 1. Add a fine to a specific plate
        System.out.println("Adding RM 50 fine to " + plate);
        dao.updateFine(plate, 50.0);

        // 2. Retrieve it to see if it saved
        double balance = dao.getExistingFines(plate);
        System.out.println("Current Unpaid Balance for " + plate + ": RM " + balance);
    }
}