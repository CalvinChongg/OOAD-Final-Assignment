import dashboard.MainFrame;
import database.SQLiteConnection;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Singleton pattern access
        SQLiteConnection.getInstance().initializeDatabase();

        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}