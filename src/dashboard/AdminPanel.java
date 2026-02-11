package dashboard;
import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JPanel { // Change class name for each
    public AdminPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        add(new JLabel("Admin Dashboard", SwingConstants.CENTER), BorderLayout.NORTH);
        // Add navigation buttons like you had before
    }
}