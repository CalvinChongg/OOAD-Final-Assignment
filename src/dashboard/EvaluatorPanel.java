package dashboard;

import dao.EvaluationDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class EvaluatorPanel extends JPanel {
    
    private MainFrame mainFrame;
    private JComboBox<String> submissionDropdown;
    private JSlider claritySlider, methodSlider, resultSlider, presSlider;
    private JTextArea commentArea;
    private JButton submitBtn, logoutBtn;

    public EvaluatorPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(70, 130, 180)); 
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel title = new JLabel("Evaluator Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        
        logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> mainFrame.switchScreen("LOGIN"));
        
        header.add(title, BorderLayout.WEST);
        header.add(logoutBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Evaluation Rubric", 
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Dropdown
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Select Project:"), gbc);
        submissionDropdown = new JComboBox<>();
        gbc.gridx = 1; 
        formPanel.add(submissionDropdown, gbc);

        // Sliders
        int y = 1;
        claritySlider = addSlider(formPanel, "Problem Clarity (1-10):", y++, gbc);
        methodSlider = addSlider(formPanel, "Methodology (1-10):", y++, gbc);
        resultSlider = addSlider(formPanel, "Results (1-10):", y++, gbc);
        presSlider = addSlider(formPanel, "Presentation Quality (1-10):", y++, gbc);

        // Comments
        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(new JLabel("Comments:"), gbc);
        commentArea = new JTextArea(4, 30);
        commentArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        gbc.gridx = 1; 
        formPanel.add(new JScrollPane(commentArea), gbc);

        // Submit
        submitBtn = new JButton("Submit Score");
        submitBtn.setBackground(new Color(50, 150, 50));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.addActionListener(e -> submitEvaluation());
        
        gbc.gridx = 1; gbc.gridy = y + 1; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(submitBtn, gbc);

        add(new JScrollPane(formPanel), BorderLayout.CENTER);
        
        // Load data immediately
        loadSubmissions();
    }

    private JSlider addSlider(JPanel panel, String label, int y, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel(label), gbc);
        
        JSlider slider = new JSlider(1, 10, 5);
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setBackground(Color.WHITE);
        
        gbc.gridx = 1;
        panel.add(slider, gbc);
        return slider;
    }

    private void loadSubmissions() {
        EvaluationDAO dao = new EvaluationDAO();
        List<String> subs = dao.getSubmissionsForEvaluator();
        submissionDropdown.removeAllItems();
        if(subs.isEmpty()) {
            submissionDropdown.addItem("No submissions available");
            submitBtn.setEnabled(false);
        } else {
            for(String s : subs) submissionDropdown.addItem(s);
            submitBtn.setEnabled(true);
        }
    }

    private void submitEvaluation() {
        String selected = (String) submissionDropdown.getSelectedItem();
        if (selected == null || selected.contains("No submissions")) return;

        int subId = Integer.parseInt(selected.split(":")[0]); 
        
        // In a real app, get this ID from mainFrame.getLoggedInUser()
        // For now, we hardcode the Evaluator ID to 4 (from your seed data)
        int evaluatorId = 4; 

        EvaluationDAO dao = new EvaluationDAO();
        boolean success = dao.saveEvaluation(
            subId,
            evaluatorId,
            claritySlider.getValue(),
            methodSlider.getValue(),
            resultSlider.getValue(),
            presSlider.getValue(),
            commentArea.getText()
        );

        if (success) {
            JOptionPane.showMessageDialog(this, "Scores Submitted!");
            commentArea.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Error: You might have already graded this student.");
        }
    }
}