
package composite;

public class ParkingSpot extends ParkingComponent {
    private String spotId;
    private String type;          // Compact, Regular, Handicapped, Reserved
    private double hourlyRate;
    private boolean available;
    private String currentVehiclePlate;

    public ParkingSpot(String floor, String row, String spotNum, String type, double rate) {
        this.spotId = "F" + floor + "-R" + row + "-S" + spotNum;
        this.type = type;
        this.hourlyRate = rate;
        this.available = true;
        this.currentVehiclePlate = null;
    }

    @Override
    public String getId() { return spotId; }
    public String getType() { return type; }
    public double getHourlyRate() { return hourlyRate; }
    @Override
    public boolean isAvailable() { return available; }
    public String getCurrentVehiclePlate() { return currentVehiclePlate; }

    public void assignVehicle(String plate) {
        this.available = false;
        this.currentVehiclePlate = plate;
    }
    public void removeVehicle() {
        this.available = true;
        this.currentVehiclePlate = null;
    }

    @Override
    public void display() {
        System.out.println("      " + spotId + " [" + type + "] " + 
                          (available ? "Available" : "Occupied by " + currentVehiclePlate));
    }
}
