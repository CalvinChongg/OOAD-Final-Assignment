import dashboard.MainFrame;
import database.SQLiteConnection;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // 1. Initialize Database (Create table if missing)
        SQLiteConnection.initializeDatabase();
        
        // 2. Launch the App
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
} 
