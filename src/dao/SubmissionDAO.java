package dao;

import database.SQLiteConnection;
import model.Submission;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubmissionDAO {
    
    public boolean addSubmission(int studentId, String title, String abstractText, 
                               String supervisor, String type, String filePath) {
        String sql = """
            INSERT INTO submissions 
            (student_id, research_title, abstract, supervisor_name, presentation_type, file_path, status) 
            VALUES (?, ?, ?, ?, ?, ?, 'SUBMITTED')
            """;
        
        try (Connection conn = SQLiteConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            pstmt.setString(2, title);
            pstmt.setString(3, abstractText);
            pstmt.setString(4, supervisor);
            pstmt.setString(5, type);
            pstmt.setString(6, filePath);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Submission error: " + e.getMessage());
            return false;
        }
    }
    
    public List<Submission> getSubmissionsByStudent(int studentId) {
        List<Submission> submissions = new ArrayList<>();
        String sql = "SELECT * FROM submissions WHERE student_id = ?";
        
        try (Connection conn = SQLiteConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String title = rs.getString("research_title");
                String abstractText = rs.getString("abstract");
                String type = rs.getString("presentation_type");
                String filePath = rs.getString("file_path");
                
                submissions.add(new Submission(title, abstractText, type, filePath));
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching submissions: " + e.getMessage());
        }
        
        return submissions;
    }
    
    public List<Object[]> getAllSubmissionsWithStudentInfo() {
        List<Object[]> submissions = new ArrayList<>();
        String sql = """
            SELECT s.*, u.full_name as student_name, u.email 
            FROM submissions s 
            JOIN users u ON s.student_id = u.id
            ORDER BY s.submission_date DESC
            """;
        
        try (Connection conn = SQLiteConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                submissions.add(row);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching all submissions: " + e.getMessage());
        }
        
        return submissions;
    }
}
