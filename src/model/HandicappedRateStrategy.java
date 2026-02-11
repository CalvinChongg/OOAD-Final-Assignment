package model;

public class HandicappedRateStrategy implements RateStrategy {
    private boolean hasCard;
    private String spotType;

    // Add this constructor to fix the 'undefined' error
    public HandicappedRateStrategy(boolean hasCard, String spotType) {
        this.hasCard = hasCard;
        this.spotType = spotType;
    }

    @Override
    public double calculateFee(int hours) {
        // Logic: Free if they have the card
        if (hasCard) {
            return 0.0;
        }
        // Otherwise, maybe a flat fee or standard rate
        return 2.0; 
    }
}