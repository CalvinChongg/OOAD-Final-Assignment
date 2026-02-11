
package dashboard;

import composite.ParkingSpot;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import model.*;
import service.ParkingService;

public class EntryPanel extends JPanel {
    private MainFrame frame;
    private ParkingService parkingService = ParkingService.getInstance();

    private JTextField plateField;
    private JComboBox<String> vehicleTypeBox;
    private JCheckBox handicapCardBox, vipBox;
    private JButton searchButton, parkButton;
    private JList<String> spotList;
    private DefaultListModel<String> spotListModel;

    public EntryPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("License Plate:"), gbc);
        gbc.gridx = 1; plateField = new JTextField(15); formPanel.add(plateField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Vehicle Type:"), gbc);
        gbc.gridx = 1; vehicleTypeBox = new JComboBox<>(new String[]{"Motorcycle", "Car", "SUV/Truck", "Handicapped"});
        formPanel.add(vehicleTypeBox, gbc);
        gbc.gridx = 0; gbc.gridy = 2; handicapCardBox = new JCheckBox("Has Handicapped Card"); formPanel.add(handicapCardBox, gbc);
        gbc.gridx = 1; vipBox = new JCheckBox("VIP (for Reserved spots)"); formPanel.add(vipBox, gbc);
        gbc.gridx = 0; gbc.gridy = 3; searchButton = new JButton("Search Available Spots"); formPanel.add(searchButton, gbc);

        add(formPanel, BorderLayout.NORTH);

        // Spot list
        spotListModel = new DefaultListModel<>();
        spotList = new JList<>(spotListModel);
        spotList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(spotList), BorderLayout.CENTER);

        // Park button
        parkButton = new JButton("Park Vehicle");
        parkButton.setEnabled(false);
        add(parkButton, BorderLayout.SOUTH);

        // Event handlers
        searchButton.addActionListener(e -> searchSpots());
        parkButton.addActionListener(e -> parkVehicle());
    }

    private void searchSpots() {
        String plate = plateField.getText().trim();
        if (plate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter license plate");
            return;
        }
        String type = (String) vehicleTypeBox.getSelectedItem();
        Vehicle vehicle = createVehicle(type, plate);
        if (vehicle == null) return;

        List<ParkingSpot> spots = parkingService.getAvailableSpots(vehicle);
        spotListModel.clear();
        for (ParkingSpot spot : spots) {
            spotListModel.addElement(spot.getId() + " [" + spot.getType() + "] RM" + spot.getHourlyRate() + "/h");
        }
        if (spots.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No suitable spots available");
            parkButton.setEnabled(false);
        } else {
            parkButton.setEnabled(true);
        }
    }

    private void parkVehicle() {
        String selected = spotList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a spot");
            return;
        }
        String spotId = selected.split(" ")[0];
        String plate = plateField.getText().trim();
        String type = (String) vehicleTypeBox.getSelectedItem();
        boolean hasCard = handicapCardBox.isSelected();
        boolean vip = vipBox.isSelected();

        boolean success = parkingService.assignSpot(plate, spotId, type, hasCard, vip);
        if (success) {
            JOptionPane.showMessageDialog(this, "Parking successful!\nTicket ID: T-" + plate + "-" + System.currentTimeMillis());
            spotListModel.clear();
            parkButton.setEnabled(false);
            plateField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Spot already taken or error occurred");
        }
    }

    private Vehicle createVehicle(String type, String plate) {
        switch (type) {
            case "Motorcycle": return new Motorcycle(plate);
            case "Car": return new Car(plate);
            case "SUV/Truck": return new SUV(plate);
            case "Handicapped": return new HandicappedVehicle(plate);
            default: return null;
        }
    }
}