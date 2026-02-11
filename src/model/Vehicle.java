package model;

public abstract class Vehicle {
    protected String licensePlate;
    protected String type;

    public Vehicle(String licensePlate, String type) {
        this.licensePlate = licensePlate;
        this.type = type;
    }
}