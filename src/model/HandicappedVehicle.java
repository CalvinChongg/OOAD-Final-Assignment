
package model;

public class HandicappedVehicle extends Vehicle {
    public HandicappedVehicle(String plate) {
        super(plate, VehicleType.HANDICAPPED);
    }
    @Override
    public boolean canPark(SpotType spotType) {
        return true; // can park anywhere, but may get discount only in Handicapped spot
    }
}