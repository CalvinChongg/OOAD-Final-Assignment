
package model;

public class SUV extends Vehicle {
    public SUV(String plate) { super(plate, VehicleType.SUV_TRUCK); }
    @Override
    public boolean canPark(SpotType spotType) {
        return spotType == SpotType.REGULAR;
    }
}
