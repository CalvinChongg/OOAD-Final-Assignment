package dashboard;

import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainContainer = new JPanel(cardLayout);

    public MainFrame() {
        setTitle("University Parking System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Wiring the panels
        mainContainer.add(new EntryPanel(this), "Entry");
        mainContainer.add(new ExitPanel(this), "Exit");
        mainContainer.add(new AdminPanel(this), "Admin");

        add(mainContainer);
    }

    public void showPanel(String name) {
        cardLayout.show(mainContainer, name);
    }
}