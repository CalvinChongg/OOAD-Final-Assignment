package model;

public class HandicappedRateStrategy implements RateStrategy {
    @Override
    public double calculateFee(int totalHours) {
        return totalHours * 2.0; // RM 2/hour fixed discount
    }
}