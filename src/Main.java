import dashboard.MainFrame;
import database.SQLiteConnection;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Initialize database (Singleton)
        SQLiteConnection.getInstance();

        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}