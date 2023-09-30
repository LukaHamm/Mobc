package app.thecity.model;

public class Evaluation {
    public String text;
    public String user;
    public int rating;
    public String uploadDate;
    public String username;

    public Evaluation(String text, String user, int rating, String uploadDate,String username) {
        this.text = text;
        this.user = user;
        this.rating = rating;
        this.uploadDate = uploadDate;
        this.username=username;

    }
}
