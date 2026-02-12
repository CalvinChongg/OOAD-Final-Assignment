
package dashboard;

import java.awt.*;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import service.FineService;
import service.ReportService;

public class AdminPanel extends JPanel {
    private MainFrame frame;
    private FineService fineService = FineService.getInstance();
    private ReportService reportService = new ReportService();

    private JComboBox<String> schemeCombo;
    private JButton applySchemeBtn;
    private JTable occupancyTable;
    private JLabel revenueLabel;

    public AdminPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());

        // Top: fine scheme
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Fine Scheme:"));
        schemeCombo = new JComboBox<>(new String[]{"FIXED", "PROGRESSIVE", "HOURLY"});
        schemeCombo.setSelectedItem(fineService.getCurrentScheme());
        topPanel.add(schemeCombo);
        applySchemeBtn = new JButton("Apply");
        topPanel.add(applySchemeBtn);
        add(topPanel, BorderLayout.NORTH);

        // Center: occupancy table
        occupancyTable = new JTable();
        add(new JScrollPane(occupancyTable), BorderLayout.CENTER);

        // South: revenue
        revenueLabel = new JLabel("Total Revenue: RM 0.00");
        revenueLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(revenueLabel, BorderLayout.SOUTH);

        // Event
        applySchemeBtn.addActionListener(e -> {
            fineService.setScheme((String) schemeCombo.getSelectedItem());
            JOptionPane.showMessageDialog(this, "Fine scheme updated");
            frame.refreshAdminAndReports();
        });
        refreshData();
    }

    public void refreshData() {
        // Occupancy by type
        Map<String, int[]> stats = reportService.getOccupancyStats();
        String[] columns = {"Spot Type", "Occupied", "Total", "Occupancy %"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (Map.Entry<String, int[]> entry : stats.entrySet()) {
            String type = entry.getKey();
            int occ = entry.getValue()[0];
            int total = entry.getValue()[1];
            String percent = total == 0 ? "0%" : String.format("%.1f%%", (occ * 100.0 / total));
            model.addRow(new Object[]{type, occ, total, percent});
        }
        occupancyTable.setModel(model);

        // 2. Update revenue label
        double rev = reportService.getTotalRevenue();
        revenueLabel.setText("Total Revenue: RM " + String.format("%.2f", rev));
    }
}