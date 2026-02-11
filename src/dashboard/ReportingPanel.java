
package dashboard;

import service.ReportService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReportingPanel extends JPanel {
    private MainFrame frame;
    private ReportService reportService = new ReportService();

    private JTabbedPane tabbedPane;
    private JTable currentVehiclesTable, finesTable;
    private JLabel revenueLabel;

    public ReportingPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();

        // Current vehicles
        currentVehiclesTable = new JTable();
        tabbedPane.addTab("Current Vehicles", new JScrollPane(currentVehiclesTable));

        // Revenue report
        JPanel revenuePanel = new JPanel();
        revenueLabel = new JLabel("Total Revenue: RM 0.00");
        revenueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        revenuePanel.add(revenueLabel);
        tabbedPane.addTab("Revenue", revenuePanel);

        // Occupancy report
        JTable occTable = new JTable();
        tabbedPane.addTab("Occupancy", new JScrollPane(occTable));

        // Fine report (outstanding)
        finesTable = new JTable();
        tabbedPane.addTab("Outstanding Fines", new JScrollPane(finesTable));

        add(tabbedPane, BorderLayout.CENTER);

        refreshAll();
    }

    private void refreshAll() {
        // Current vehicles
        List<String[]> vehicles = reportService.getCurrentVehicles();
        String[] cols = {"Plate", "Type", "Spot", "Spot Type", "Entry Time"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (String[] row : vehicles) model.addRow(row);
        currentVehiclesTable.setModel(model);

        // Revenue
        double rev = reportService.getTotalRevenue();
        revenueLabel.setText("Total Revenue: RM " + String.format("%.2f", rev));

        // Outstanding fines
        List<String[]> fines = reportService.getOutstandingFines();
        DefaultTableModel fineModel = new DefaultTableModel(new String[]{"License Plate", "Unpaid Fine (RM)"}, 0);
        for (String[] f : fines) fineModel.addRow(f);
        finesTable.setModel(fineModel);
    }
}
