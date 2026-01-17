import dashboard.MainFrame;
import database.SQLiteConnection; // Import your frame from the dashboard folder
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import model.*;

public class Main {
   public static void main(String[] args) {

        // Initialize database once
        SQLiteConnection.initializeDatabase();

       // 2. Launch
       SwingUtilities.invokeLater(() -> {
           List<User> users = new ArrayList<>();
           MainFrame frame = new MainFrame(users);
           frame.setVisible(true);
       });
   }
}