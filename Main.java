import dashboard.MainFrame;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.*;

public class Main {
    public static void main(String[] args) {
        
        List<User> userList = new ArrayList<>();
        
        // 1. Connection String: Replace 'seminar.db' with your actual database file name
        String url = "jdbc:sqlite:database.db"; 

        // 2. Query to get all users
        // I am assuming you have a 'role' column to distinguish Students from Coordinators
        String query = "SELECT id, username, password, role FROM users";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("Connected to database. Fetching users...");

            while (rs.next()) {
                String id = rs.getString("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String role = rs.getString("role");

                // 3. Polymorphism: Check the role and instantiate the correct subclass
                if ("COORDINATOR".equalsIgnoreCase(role)) {
                    userList.add(new Coordinator(id, username, password));
                } else if ("STUDENT".equalsIgnoreCase(role)) {
                    userList.add(new Student(id, username, password));
                }
            }

        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            System.out.println(e.getMessage());
            
            // Optional: Fallback to mock data if database fails so you can still test GUI
            System.out.println("Loading mock data instead...");
            userList.add(new Coordinator("C01", "admin", "admin123"));
            userList.add(new Student("S01", "ali", "123"));
        }

        // 4. Pass the database-populated list to your frame
        MainFrame frame = new MainFrame(userList);
        frame.setVisible(true);
    }
}