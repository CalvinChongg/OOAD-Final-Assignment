
package dashboard;

import java.awt.*;
import javax.swing.*;
import service.ParkingService;
import service.ParkingService.ExitData;

public class ExitPanel extends JPanel {
    private MainFrame frame;
    private ParkingService parkingService = ParkingService.getInstance();

    private JTextField plateField;
    private JButton searchButton, payButton;
    private JTextArea receiptArea;
    private ExitData currentExit;

    public ExitPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());

        // Top: plate entry
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("License Plate:"));
        plateField = new JTextField(15);
        topPanel.add(plateField);
        searchButton = new JButton("Calculate Charges");
        topPanel.add(searchButton);
        add(topPanel, BorderLayout.NORTH);

        // Center: receipt display
        receiptArea = new JTextArea(20, 50);
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(receiptArea), BorderLayout.CENTER);

        // Bottom: payment
        JPanel bottomPanel = new JPanel();
        payButton = new JButton("Process Payment");
        payButton.setEnabled(false);
        bottomPanel.add(payButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Event handlers
        searchButton.addActionListener(e -> calculate());
        payButton.addActionListener(e -> processPayment());
    }

    private void calculate() {
        String plate = plateField.getText().trim();
        if (plate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter license plate");
            return;
        }
        currentExit = parkingService.prepareExit(plate);
        if (currentExit == null) {
            receiptArea.setText("No active parking found for plate: " + plate);
            payButton.setEnabled(false);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("========== EXIT BILL ==========\n");
        sb.append("Ticket: ").append(currentExit.ticketId).append("\n");
        sb.append("Spot: ").append(currentExit.spotId).append(" (").append(currentExit.spotType).append(")\n");
        sb.append("Entry: ").append(currentExit.entryTime).append("\n");
        sb.append("Duration: ").append(currentExit.hours).append(" hours\n");
        sb.append("Parking fee: RM ").append(String.format("%.2f", currentExit.parkingFee)).append("\n");
        if (currentExit.overstayFine > 0)
            sb.append("Overstay fine: RM ").append(currentExit.overstayFine).append("\n");
        if (currentExit.misuseFine > 0)
            sb.append("Reserved misuse fine: RM ").append(currentExit.misuseFine).append("\n");
        if (currentExit.unpaidFines > 0)
            sb.append("Unpaid fines (previous): RM ").append(currentExit.unpaidFines).append("\n");
        sb.append("--------------------------------\n");
        sb.append("TOTAL DUE: RM ").append(String.format("%.2f", currentExit.totalDue)).append("\n");
        sb.append("================================\n");
        receiptArea.setText(sb.toString());
        payButton.setEnabled(true);
    }

    private void processPayment() {
        if (currentExit == null) return;
        String[] options = {"Cash", "Card"};
        int choice = JOptionPane.showOptionDialog(this,
                "Select payment method", "Payment",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);

        String method = (choice == 0) ? "Cash" : "Card";
        double amountPaid = currentExit.totalDue;

        boolean success = parkingService.processPayment(
                plateField.getText().trim(),
                currentExit.ticketId,
                currentExit.parkingFee,
                currentExit.overstayFine + currentExit.misuseFine,
                currentExit.unpaidFines,
                amountPaid,
                method
        );
        if (success) {
            receiptArea.append("\nPAYMENT SUCCESSFUL (RM " + amountPaid + " via " + method + ")\n");
            JOptionPane.showMessageDialog(this, "Exit completed. Thank you!");
            frame.refreshAdminAndReports();
            payButton.setEnabled(false);
            plateField.setText("");
            currentExit = null;
        } else {
            JOptionPane.showMessageDialog(this, "Payment failed");
        }
    }
}