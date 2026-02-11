package model;

public class StandardRateStrategy implements RateStrategy {
    private double hourlyRate;

    public StandardRateStrategy(double rate) {
        this.hourlyRate = rate;
    }

    @Override
    public double calculateFee(int totalHours) {
        // Simple math for regular spots (Compact, Regular, Reserved)
        return totalHours * hourlyRate;
    }
}