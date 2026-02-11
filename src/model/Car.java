
package model;

public class Car extends Vehicle {
    public Car(String plate) { super(plate, VehicleType.CAR); }
    @Override
    public boolean canPark(SpotType spotType) {
        return spotType == SpotType.COMPACT || spotType == SpotType.REGULAR || spotType == SpotType.RESERVED;
    }
}