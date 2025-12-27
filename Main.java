import dashboard.MainFrame;
import java.util.ArrayList; // Import your frame from the dashboard folder
import java.util.List;
import javax.swing.SwingUtilities;
import model.*;

public class Main {
    public static void main(String[] args) {
        
        // 1. Create Mock Data
        List<User> mockUsers = new ArrayList<>();
        mockUsers.add(new Coordinator("C01", "admin", "admin123"));
        mockUsers.add(new Student("S01", "ali", "123"));

        // 2. Launch
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(mockUsers);
            frame.setVisible(true);
        });
    }
}