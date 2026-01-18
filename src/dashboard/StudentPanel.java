package dashboard;

import dao.SubmissionDAO;
import dao.Submission;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class StudentPanel extends JPanel {
    private MainFrame mainFrame;
    private JTextField titleField, supervisorField;
    private JTextArea abstractArea;
    private JComboBox<String> typeCombo;
    private JTextField filePathField;
    private JButton browseBtn, submitBtn, logoutBtn, viewBtn;
    
    public StudentPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel welcome = new JLabel("Student Dashboard - Presentation Submission");
        welcome.setFont(new Font("Arial", Font.BOLD, 20));
        welcome.setForeground(Color.WHITE);
        headerPanel.add(welcome, BorderLayout.WEST);
        
        logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> mainFrame.switchScreen("LOGIN"));
        headerPanel.add(logoutBtn, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Main Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Submission Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            "Submit Your Presentation",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            Color.DARK_GRAY
        ));
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Research Title
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Research Title *:"), gbc);
        
        titleField = new JTextField(30);
        gbc.gridx = 1; gbc.gridwidth = 2;
        formPanel.add(titleField, gbc);
        
        // Abstract
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Abstract *:"), gbc);
        
        abstractArea = new JTextArea(5, 30);
        abstractArea.setLineWrap(true);
        abstractArea.setWrapStyleWord(true);
        JScrollPane abstractScroll = new JScrollPane(abstractArea);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.gridheight = 2;
        formPanel.add(abstractScroll, gbc);
        
        // Supervisor
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.gridheight = 1;
        formPanel.add(new JLabel("Supervisor:"), gbc);
        
        supervisorField = new JTextField(30);
        gbc.gridx = 1; gbc.gridwidth = 2;
        formPanel.add(supervisorField, gbc);
        
        // Presentation Type
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Presentation Type *:"), gbc);
        
        typeCombo = new JComboBox<>(new String[]{"ORAL", "POSTER"});
        gbc.gridx = 1; gbc.gridwidth = 2;
        formPanel.add(typeCombo, gbc);
        
        // File Upload
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Presentation File:"), gbc);
        
        JPanel filePanel = new JPanel(new BorderLayout(5, 0));
        filePathField = new JTextField(20);
        filePathField.setEditable(false);
        browseBtn = new JButton("Browse...");
        browseBtn.addActionListener(e -> browseFile());
        filePanel.add(filePathField, BorderLayout.CENTER);
        filePanel.add(browseBtn, BorderLayout.EAST);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        formPanel.add(filePanel, gbc);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        submitBtn = new JButton("Submit Presentation");
        submitBtn.setBackground(new Color(50, 150, 50));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.addActionListener(this::submitPresentation);
        
        viewBtn = new JButton("View My Submissions");
        viewBtn.setBackground(new Color(70, 130, 180));
        viewBtn.setForeground(Color.WHITE);
        viewBtn.addActionListener(this::viewSubmissions);
        
        buttonPanel.add(viewBtn);
        buttonPanel.add(submitBtn);
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(buttonPanel, gbc);
        
        // Instructions Panel
        JPanel instructionPanel = new JPanel(new BorderLayout());
        instructionPanel.setBorder(BorderFactory.createTitledBorder("Instructions"));
        instructionPanel.setBackground(new Color(240, 248, 255));
        
        JTextArea instructions = new JTextArea();
        instructions.setEditable(false);
        instructions.setLineWrap(true);
        instructions.setWrapStyleWord(true);
        instructions.setText("""
            1. Fill in all required fields marked with *
            2. Abstract should be 150-300 words
            3. Supported file formats: PDF, PPT, PPTX
            4. Oral presentations: 15 minutes + 5 minutes Q&A
            5. Poster size: A1 (594mm x 841mm) portrait orientation
            6. Submission deadline: Check seminar schedule
            """);
        instructionPanel.add(new JScrollPane(instructions), BorderLayout.CENTER);
        
        // Add panels to content
        contentPanel.add(formPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(instructionPanel);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Footer
        JLabel footer = new JLabel("Student Module - © 2024 Seminar Management System", 
            SwingConstants.CENTER);
        footer.setFont(new Font("Arial", Font.PLAIN, 12));
        footer.setForeground(Color.GRAY);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(footer, BorderLayout.SOUTH);
    }
    
    private void browseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Presentation Files", "pdf", "ppt", "pptx", "doc", "docx"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            filePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private void submitPresentation(ActionEvent e) {
        // Validate inputs
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter research title", 
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (abstractArea.getText().trim().isEmpty() || abstractArea.getText().trim().length() < 50) {
            JOptionPane.showMessageDialog(this, "Abstract should be at least 50 characters", 
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // In real implementation, get actual student ID from logged-in user
        // For now, use a dummy ID (student1 has ID=5 in seeded data)
        int studentId = 5;
        
        SubmissionDAO dao = new SubmissionDAO();
        boolean success = dao.addSubmission(
            studentId,
            titleField.getText().trim(),
            abstractArea.getText().trim(),
            supervisorField.getText().trim(),
            (String) typeCombo.getSelectedItem(),
            filePathField.getText().trim()
        );
        
        if (success) {
            JOptionPane.showMessageDialog(this, 
                "Presentation submitted successfully!\n\n" +
                "Title: " + titleField.getText() + "\n" +
                "Type: " + typeCombo.getSelectedItem() + "\n" +
                "Status: Submitted for review",
                "Submission Successful",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Clear form
            titleField.setText("");
            abstractArea.setText("");
            supervisorField.setText("");
            filePathField.setText("");
            typeCombo.setSelectedIndex(0);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to submit presentation. Please try again.",
                "Submission Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewSubmissions(ActionEvent e) {
        // In real implementation, get actual student ID
        int studentId = 5;
        
        SubmissionDAO dao = new SubmissionDAO();
        var submissions = dao.getSubmissionsByStudent(studentId);
        
        if (submissions.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No submissions found.",
                "My Submissions",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Your Submissions:\n\n");
            for (int i = 0; i < submissions.size(); i++) {
                Submission s = submissions.get(i);
                sb.append(i + 1).append(". ").append(s.getTitle()).append("\n");
                sb.append("   Type: ").append(s.getType()).append("\n");
                sb.append("------------------------\n");
            }
            
            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            
            JOptionPane.showMessageDialog(this, scrollPane, 
                "My Submissions", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}