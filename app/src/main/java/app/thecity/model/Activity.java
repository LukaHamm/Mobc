package app.thecity.model;

public class Activity {
    public String title;
    public String activityType;
    public String uploadDate;
    public String description;

    public Activity(String title, String activityType, String uploadDate, String description) {
        this.title = title;
        this.activityType = activityType;
        this.uploadDate = uploadDate;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getActivityType() {
        return activityType;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public String getDescription() {
        return description;
    }
}
