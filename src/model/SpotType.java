
package model;

public enum SpotType {
    COMPACT(2.0), REGULAR(5.0), HANDICAPPED(2.0), RESERVED(10.0);
    private final double baseRate;
    SpotType(double rate) { this.baseRate = rate; }
    public double getBaseRate() { return baseRate; }
}
