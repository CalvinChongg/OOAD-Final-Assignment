package model;

public class HourlyFineStrategy implements FineStrategy {
    @Override
    public double calculateFine(int hours) {
        // Requirement: RM 20 per hour for overstaying (past 24h)
        if (hours <= 24) return 0.0;
        int overstayHours = hours - 24;
        return overstayHours * 20.0;
    }

    @Override
    public String getSchemeName() {
        return "Hourly Fine Scheme";
    }
}