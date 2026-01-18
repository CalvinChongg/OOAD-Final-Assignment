package dashboard;

import java.awt.*;
import javax.swing.*;
import model.User;

public class MainFrame extends JFrame {
    
    private User loggedInUser;
    
    private CardLayout cardLayout;
    private JPanel mainContainer;

    // No arguments in constructor
    public MainFrame() {
        setTitle("Seminar Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Add the panels - pass 'this' reference
        mainContainer.add(new LoginPanel(this), "LOGIN");
        mainContainer.add(new StudentPanel(this), "STUDENT");
        mainContainer.add(new CoordinatorPanel(this), "COORDINATOR");
        mainContainer.add(new EvaluatorPanel(this), "EVALUATOR");
        mainContainer.add(new AdminPanel(this), "ADMIN");

        add(mainContainer);
        
        // Start with login screen
        switchScreen("LOGIN");
    }

    public void switchScreen(String screenName) {
        cardLayout.show(mainContainer, screenName);
    }

    public void setLoggedInUser(User user) { 
        this.loggedInUser = user; 
    }
}
