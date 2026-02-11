package dashboard;
import javax.swing.*;
import java.awt.*;

public class EntryPanel extends JPanel { // Change class name for each
    public EntryPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        add(new JLabel("Entry Dashboard", SwingConstants.CENTER), BorderLayout.NORTH);
        // Add navigation buttons like you had before
    }
}