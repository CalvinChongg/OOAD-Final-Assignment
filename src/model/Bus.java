package model;

public class Bus extends Vehicle {
    public Bus(String plate) { super(plate, VehicleType.BUS); }
    @Override
    public boolean canPark(SpotType spotType) {
        return spotType == SpotType.REGULAR || spotType == SpotType.ELECTRIC || spotType == SpotType.RESERVED;
    }
}
