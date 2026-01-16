package model;

public class Student extends User {
    private Submission submission; 

    public Student(String id, String username, String password) {
        super(id, username, password, "STUDENT");
        this.submission = null; 
    }

    public void submitPresentation(String title, String abs, String type, String path) {
        this.submission = new Submission(title, abs, type, path);
    }

    public Submission getSubmission() { return submission; }
    
    public boolean hasSubmitted() { return submission != null; }
}