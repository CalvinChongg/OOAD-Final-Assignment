package dashboard;

import java.awt.*;
import javax.swing.*;
import model.Coordinator;
import model.Student;
import model.User;

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

       // Layout Logic
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
           boolean found = false;

           for(User u : mainFrame.getUsers()) {
               if(u.getUsername().equals(uIn) && u.getPassword().equals(pIn)) {
                   found = true;
                   mainFrame.setLoggedInUser(u);
                   if(u instanceof Student) mainFrame.switchScreen("STUDENT");
                   else if(u instanceof Coordinator) mainFrame.switchScreen("COORDINATOR");
                   break;
               }
           }
           if(!found) JOptionPane.showMessageDialog(this, "Invalid Login");
       });
   }
}
