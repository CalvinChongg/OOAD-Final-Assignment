package dashboard;

import java.awt.*;
import javax.swing.*;
import model.User;

public class MainFrame extends JFrame {

    private User loggedInUser;

    private CardLayout cardLayout;
    private JPanel mainContainer;

    public MainFrame() {

        setTitle("Seminar System (Prototype)");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Login panel will talk to UserDAO
        mainContainer.add(new LoginPanel(this), "LOGIN");

        mainContainer.add(new JLabel("Student Dashboard - Coming Soon"), "STUDENT");
        mainContainer.add(new JLabel("Coordinator Dashboard - Coming Soon"), "COORDINATOR");

        add(mainContainer);
    }

    public void switchScreen(String screenName) {
        cardLayout.show(mainContainer, screenName);
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }
}
