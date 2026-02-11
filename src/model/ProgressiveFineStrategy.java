package model;

public class ProgressiveFineStrategy implements FineStrategy {
    public double calculateFine(int totalHours) {
        if (totalHours <= 24) return 0.0;
        
        int overstayHours = totalHours - 24;
        double fine = 50.0; // Base fine for the first 24h of overstaying

        if (totalHours > 48) fine += 100.0; // Additional for hours 24-48
        if (totalHours > 72) fine += 150.0; // Additional for hours 48-72
        if (totalHours > 96) fine += 200.0; // Additional for above 72h of overstaying

        return fine;
    }
    public String getSchemeName() { return "Progressive Fine Scheme"; }
}