package dashboard;

import java.awt.*;
import javax.swing.*;

public class EvaluatorPanel extends JPanel {
    
    public EvaluatorPanel(MainFrame mainFrame) {
        setLayout(new BorderLayout());
        setBackground(Color.LIGHT_GRAY); 
        
        JLabel welcome = new JLabel("Welcome to the Evaluator Dashboard", SwingConstants.CENTER);
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