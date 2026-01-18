package dashboard;

import java.awt.*;
import javax.swing.*;

public class StudentPanel extends JPanel {
    
    public StudentPanel(MainFrame mainFrame) {
        setLayout(new BorderLayout());
        setBackground(Color.LIGHT_GRAY); 
        
        JLabel welcome = new JLabel("Welcome to the Student Dashboard", SwingConstants.CENTER);
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