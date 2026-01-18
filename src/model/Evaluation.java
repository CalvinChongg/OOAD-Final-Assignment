package model;

public class Evaluation {
    // Fields match your database columns
    private int id;
    private int submissionId;
    private int evaluatorId;
    private int problemClarity;
    private int methodology;
    private int results;
    private int presentationQuality;
    private double overallScore;
    private String comments;

    // Constructor
    public Evaluation(int id, int submissionId, int evaluatorId, int clarity, 
                      int method, int results, int pres, double overall, String comments) {
        this.id = id;
        this.submissionId = submissionId;
        this.evaluatorId = evaluatorId;
        this.problemClarity = clarity;
        this.methodology = method;
        this.results = results;
        this.presentationQuality = pres;
        this.overallScore = overall;
        this.comments = comments;
    }

    // Getters (You need these to read the values later)
    public int getId() { return id; }
    public int getSubmissionId() { return submissionId; }
    public int getEvaluatorId() { return evaluatorId; }
    public int getProblemClarity() { return problemClarity; }
    public int getMethodology() { return methodology; }
    public int getResults() { return results; }
    public int getPresentationQuality() { return presentationQuality; }
    public double getOverallScore() { return overallScore; }
    public String getComments() { return comments; }
    
    // Setters (Optional, but good if you want to edit grades later)
    public void setComments(String comments) { this.comments = comments; }
}