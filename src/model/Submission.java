package model;

import java.io.Serializable;

public class Submission implements Serializable {
    private String researchTitle;
    private String abstractText;
    private String filePath;
    private String presentationType; // "Oral" or "Poster"

    public Submission(String title, String abstractText, String type, String path) {
        this.researchTitle = title;
        this.abstractText = abstractText;
        this.presentationType = type;
        this.filePath = path;
    }

    // Getters
    public String getTitle() { return researchTitle; }
    public String getType() { return presentationType; }
    
}