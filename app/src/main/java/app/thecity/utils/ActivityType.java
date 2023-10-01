package app.thecity.utils;

public enum ActivityType {
    all(-1), calisthenics(0), parkour(1), outdoor(2), outdoor_gym(3), own(10);

    private int categoryId;
    ActivityType(int categoryid){
        this.categoryId = categoryid;
    }

    public static ActivityType getbyCategoryId(int categoryId){
        for (ActivityType activityType : values()){
            if (activityType.categoryId == categoryId){
                return activityType;
            }
        }
        return null;
    }



}
