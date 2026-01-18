package dao;

import database.SQLiteConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public boolean checkLogin(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = SQLiteConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            System.out.println("Checking database for user: " + username);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // User found, now check password
                String dbPass = rs.getString("password");
                System.out.println("User found! DB Password: " + dbPass + " | Entered: " + password);
                
                if (dbPass.equals(password)) {
                    return true;
                } else {
                    System.out.println("Password Mismatch!");
                    return false;
                }
            } else {
                System.out.println("User NOT found in database.");
                return false;
            }
            
        } catch (SQLException e) {
            System.out.println("DATABASE ERROR: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public String getUserRole(String username) {
        String role = "";
        // WE ARE USING 'roles' (PLURAL) HERE TO MATCH YOUR REQUEST
        String sql = "SELECT roles FROM users WHERE username = ?";
        
        try (Connection conn = SQLiteConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                role = rs.getString("roles"); // This must match your DB column exactly!
            }
        } catch (SQLException e) {
            System.out.println("ROLE ERROR: " + e.getMessage());
        }
        return role;
    }
}