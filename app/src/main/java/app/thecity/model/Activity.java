package app.thecity.model;

import java.util.List;

public class Activity {
    public String title;
    public String activityType;
    public String uploadDate;
    public String description;

    public List<Image> images;

    public Location location;

    public Activity(String title, String activityType, String uploadDate, String description,List<Image> images,Location location) {
        this.title = title;
        this.activityType = activityType;
        this.uploadDate = uploadDate;
        this.description = description;
        this.images=images;
        this.location=location;

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
