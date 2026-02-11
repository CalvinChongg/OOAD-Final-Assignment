package dashboard;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainContainer = new JPanel(cardLayout);

    public MainFrame() {
        setTitle("University Parking System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainContainer.add(new EntryPanel(this), "Entry");
        mainContainer.add(new ExitPanel(this), "Exit");
        mainContainer.add(new AdminPanel(this), "Admin");

        add(mainContainer);
    }

    public void showPanel(String name) { cardLayout.show(mainContainer, name); }
}