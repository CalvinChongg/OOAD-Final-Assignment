package model;

public class ProgressiveFineStrategy implements FineStrategy {
    public double calculateFine(int hours) {
        if (hours <= 24) return 0.0;
        double fine = 50.0; // First 24-48h
        if (hours > 48) fine += 100.0; // 48-72h
        if (hours > 72) fine += 150.0; // Above 72h
        // Note: Logic follows your tiers
        return fine;
    }
    public String getSchemeName() { return "Progressive Fine Scheme"; }
}