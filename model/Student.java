package model;

public class Student extends User {
    private String researchTitle;
    private String presentationType; 
    private String filePath;

    public Student(String id, String username, String password) {
        super(id, username, password, "STUDENT");
        
        this.researchTitle = "N/A";
        this.presentationType = "Oral";
    }
    public void setResearchTitle(String title) { this.researchTitle = title; }
    public String getResearchTitle() { return researchTitle; }
}
