
package model;

public abstract class Vehicle {
    protected String licensePlate;
    protected VehicleType type;
    protected boolean hasHandicappedCard;  // for HandicappedVehicle
    protected boolean isVIP;              // for Reserved spots

    public Vehicle(String licensePlate, VehicleType type) {
        this.licensePlate = licensePlate;
        this.type = type;
        this.hasHandicappedCard = false;
        this.isVIP = false;
    }

    public String getLicensePlate() { return licensePlate; }
    public VehicleType getType() { return type; }
    public boolean hasHandicappedCard() { return hasHandicappedCard; }
    public boolean isVIP() { return isVIP; }
    public void setHasHandicappedCard(boolean b) { this.hasHandicappedCard = b; }
    public void setVIP(boolean b) { this.isVIP = b; }

    public abstract boolean canPark(SpotType spotType);
}