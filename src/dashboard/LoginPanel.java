package dashboard;

import dao.UserDAO;
import java.awt.*;
import javax.swing.*;
import model.Coordinator;
import model.Student;

public class LoginPanel extends JPanel {
    private MainFrame mainFrame;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("System Login");
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        JButton loginBtn = new JButton("Login");

        // Layout
        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2; add(title, gbc);
        gbc.gridwidth=1;
        gbc.gridx=0; gbc.gridy=1; add(new JLabel("User:"), gbc);
        gbc.gridx=1; add(userField, gbc);
        gbc.gridx=0; gbc.gridy=2; add(new JLabel("Pass:"), gbc);
        gbc.gridx=1; add(passField, gbc);
        gbc.gridx=1; gbc.gridy=3; add(loginBtn, gbc);

        // Button Logic
        loginBtn.addActionListener(e -> {
            String uIn = userField.getText();
            String pIn = new String(passField.getPassword());
            
            UserDAO dao = new UserDAO();

            if (dao.checkLogin(uIn, pIn)) {
                String role = dao.getUserRole(uIn);
                
                // IMPORTANT: We use "0" as a dummy ID here to satisfy your Constructor
                if (role.equalsIgnoreCase("student")) {
                    mainFrame.setLoggedInUser(new Student("0", uIn, pIn));
                    mainFrame.switchScreen("STUDENT");
                    
                } else if (role.equalsIgnoreCase("coordinator")) {
                    mainFrame.setLoggedInUser(new Coordinator("0", uIn, pIn));
                    mainFrame.switchScreen("COORDINATOR");
                    
                } else {
                    JOptionPane.showMessageDialog(this, "Unknown Role: " + role);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Login");
            }
        });
    }
}