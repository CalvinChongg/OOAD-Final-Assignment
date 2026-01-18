package dashboard;

import java.awt.*;
import javax.swing.*;

public class AdminPanel extends JPanel {
    
    public AdminPanel(MainFrame mainFrame) {
        setLayout(new BorderLayout());
        setBackground(Color.LIGHT_GRAY); 
        
        JLabel welcome = new JLabel("Welcome to the Admin Dashboard", SwingConstants.CENTER);
        welcome.setFont(new Font("Arial", Font.BOLD, 24));
        
        add(welcome, BorderLayout.CENTER);
        
        // Logout Button
        JButton logout = new JButton("Logout");
        logout.addActionListener((e -> {
        mainFrame.switchScreen("LOGIN");
        }));
        add(logout, BorderLayout.SOUTH);
    }
}
