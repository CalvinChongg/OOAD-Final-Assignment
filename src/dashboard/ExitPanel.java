package dashboard;
import javax.swing.*;
import java.awt.*;

public class ExitPanel extends JPanel { // Change class name for each
    public  ExitPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        add(new JLabel("Exit Dashboard", SwingConstants.CENTER), BorderLayout.NORTH);
        // Add navigation buttons like you had before
    }
}