package model;

public interface FineStrategy {
    double calculateFine(int totalHours);
    String getSchemeName();
}