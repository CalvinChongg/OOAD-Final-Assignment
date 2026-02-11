
package model;

public class Motorcycle extends Vehicle {
    public Motorcycle(String plate) { super(plate, VehicleType.MOTORCYCLE); }
    @Override
    public boolean canPark(SpotType spotType) {
        return spotType == SpotType.COMPACT;
    }
}