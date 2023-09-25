package app.thecity.utils;

public enum ActivityType {
    all(-1),calisthenics(1),parkour(2),outdoor(3),outdoor_gym(4),own(11);

    private int categoryId;
    ActivityType(int categoryid){
        this.categoryId = categoryid;
    }

    public static ActivityType getbyCategoryId(int categoryId){
        for (ActivityType activityType:values()){
            if (activityType.categoryId == categoryId){
                return activityType;
            }
        }
        return null;
    }
}
