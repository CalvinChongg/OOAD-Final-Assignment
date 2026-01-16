package dashboard;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import model.User;

public class MainFrame extends JFrame {
    
    private List<User> users;
    private User loggedInUser;
    
    private CardLayout cardLayout;
    private JPanel mainContainer;

    public MainFrame(List<User> passedUsers) {
        this.users = passedUsers;

        setTitle("Seminar System (Prototype)");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Pass 'this' to LoginPanel so it can switch screens
        mainContainer.add(new LoginPanel(this), "LOGIN");
        
        mainContainer.add(new JLabel("Student Dashboard - Coming Soon"), "STUDENT");
        mainContainer.add(new JLabel("Coordinator Dashboard - Coming Soon"), "COORDINATOR");

        add(mainContainer);
    }

    public void switchScreen(String screenName) {
        cardLayout.show(mainContainer, screenName);
    }

    public List<User> getUsers() { return users; }
    public void setLoggedInUser(User user) { this.loggedInUser = user; }
}