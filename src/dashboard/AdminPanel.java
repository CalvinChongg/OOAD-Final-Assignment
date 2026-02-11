
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
        });
        refreshData();
    }

    private void refreshData() {
        // Occupancy by type
        Map<String, Integer> occMap = reportService.getOccupancyByType();
        String[] columns = {"Spot Type", "Occupied", "Total", "Occupancy %"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        for (Map.Entry<String, Integer> e : occMap.entrySet()) {
            // We need total per type; for simplicity we fetch again
            // In a real system you'd have a method to get totals by type.
            // Here we show dummy data.
            model.addRow(new Object[]{e.getKey(), e.getValue(), "?", "?"});
        }
        occupancyTable.setModel(model);

        // Revenue
        double rev = reportService.getTotalRevenue();
        revenueLabel.setText("Total Revenue: RM " + String.format("%.2f", rev));
    }
}