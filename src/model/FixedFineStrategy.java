package model;

public class FixedFineStrategy implements FineStrategy {
    public double calculateFine(int hours) {
        return (hours > 24) ? 50.0 : 0.0;
    }
    public String getSchemeName() { return "Fixed Fine Scheme"; }
}