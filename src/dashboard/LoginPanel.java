package dashboard;

import dao.UserDAO;
import java.awt.*;
import javax.swing.*;

public class LoginPanel extends JPanel {
    private MainFrame mainFrame;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("Seminar Management System");
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel subtitle = new JLabel("Login to Continue");
        subtitle.setFont(new Font("Arial", Font.ITALIC, 14));

        JTextField userField = new JTextField(20);
        JPasswordField passField = new JPasswordField(20);
        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(70, 130, 180));
        loginBtn.setForeground(Color.WHITE);

        // Layout
        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(title, gbc);

        gbc.gridy = 1;
        add(subtitle, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridy = 2; gbc.gridx = 0; 
        add(new JLabel("Username:"), gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1; 
        add(userField, gbc);

        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridy = 3; gbc.gridx = 0; 
        add(new JLabel("Password:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1; 
        add(passField, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        add(loginBtn, gbc);

        // gbc.gridy = 5; gbc.gridwidth = 2;
        // gbc.fill = GridBagConstraints.HORIZONTAL;
        // add(testPanel, gbc);

        // Button Logic
        loginBtn.addActionListener(e -> {
            String uIn = userField.getText().trim();
            String pIn = new String(passField.getPassword());
            
            if (uIn.isEmpty() || pIn.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter username and password", 
                    "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            UserDAO dao = new UserDAO();

            if (dao.checkLogin(uIn, pIn)) {
                String role = dao.getUserRole(uIn);
                
                // IMPORTANT: We use "0" as a dummy ID here to satisfy your Constructor
                if (role.equalsIgnoreCase("student")) {
                    mainFrame.switchScreen("STUDENT");
                    
                } else if (role.equalsIgnoreCase("coordinator")) {
                    mainFrame.switchScreen("COORDINATOR");
                    
                } else if (role.equalsIgnoreCase("admin")) {
                    mainFrame.switchScreen("ADMIN");
                    
                } else if (role.equalsIgnoreCase("evaluator")) {
                    mainFrame.switchScreen("EVALUATOR");
                    
                }else {
                    JOptionPane.showMessageDialog(this, "Unknown Role: " + role);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Invalid username or password", 
                    "Login Failed", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}