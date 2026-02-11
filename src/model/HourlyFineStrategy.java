package model;

public class HourlyFineStrategy implements FineStrategy {
    @Override
        public double calculateFine(int totalHours) {
        if (totalHours <= 24) return 0.0;
        
        // RM 20 per hour for every hour past the 24-hour mark
        int overstayHours = totalHours - 24;
        return overstayHours * 20.0;
    }

    @Override
    public String getSchemeName() {
        return "Hourly Fine Scheme";
    }
}