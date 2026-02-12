
package dashboard;

import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainContainer = new JPanel(cardLayout);
    private EntryPanel entryPanel;
    private ExitPanel exitPanel;
    private AdminPanel adminPanel;
    private ReportingPanel reportingPanel;

    public MainFrame() {
        setTitle("University Parking System");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create panels with reference to this frame
        entryPanel = new EntryPanel(this);
        exitPanel = new ExitPanel(this);
        adminPanel = new AdminPanel(this);
        reportingPanel = new ReportingPanel(this);

        mainContainer.add(entryPanel, "Entry");
        mainContainer.add(exitPanel, "Exit");
        mainContainer.add(adminPanel, "Admin");
        mainContainer.add(reportingPanel, "Reports");

        // Navigation bar
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Navigation");
        JMenuItem entryItem = new JMenuItem("Entry");
        JMenuItem exitItem = new JMenuItem("Exit");
        JMenuItem adminItem = new JMenuItem("Admin");
        JMenuItem reportItem = new JMenuItem("Reports");
        entryItem.addActionListener(e -> showPanel("Entry"));
        exitItem.addActionListener(e -> showPanel("Exit"));
        adminItem.addActionListener(e -> showPanel("Admin"));
        reportItem.addActionListener(e -> showPanel("Reports"));
        menu.add(entryItem);
        menu.add(exitItem);
        menu.add(adminItem);
        menu.add(reportItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        add(mainContainer);
    }

    public void refreshAdminAndReports() {
        if (adminPanel != null) adminPanel.refreshData();
        if (reportingPanel != null) reportingPanel.refreshAll();
    }

    public void showPanel(String name) { cardLayout.show(mainContainer, name); }
}