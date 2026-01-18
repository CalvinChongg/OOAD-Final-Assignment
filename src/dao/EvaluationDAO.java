package dao;


import database.SQLiteConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvaluationDAO {

    public List<String> getSubmissionsForEvaluator() {
        List<String> list = new ArrayList<>();
        // In a real app, we would only get submissions assigned to this specific evaluator
        // For this prototype, we just get ALL submissions
        String sql = "SELECT id, research_title FROM submissions"; 
        
        try (Connection conn = SQLiteConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                list.add(rs.getInt("id") + ": " + rs.getString("research_title"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean saveEvaluation(int subId, int evalId, int clarity, int method, int results, int pres, String comments) {
        String sql = "INSERT INTO evaluations(submission_id, evaluator_id, problem_clarity, methodology, results, presentation_quality, overall_score, comments) VALUES(?,?,?,?,?,?,?,?)";
        
        // Calculate average score
        double overall = (clarity + method + results + pres) / 4.0;

        try (Connection conn = SQLiteConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, subId);
            pstmt.setInt(2, evalId); // We need the Evaluator's User ID
            pstmt.setInt(3, clarity);
            pstmt.setInt(4, method);
            pstmt.setInt(5, results);
            pstmt.setInt(6, pres);
            pstmt.setDouble(7, overall);
            pstmt.setString(8, comments);
            
            pstmt.executeUpdate();
            return true;
            
        } catch (Exception e) {
            System.out.println("Error saving evaluation: " + e.getMessage());
            return false;
        }
    }
}