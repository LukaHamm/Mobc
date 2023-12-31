package app.thecity.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;
import java.util.List;




public class Activity implements Serializable, ClusterItem  {

    public String title;

    public String _id;
    public String activityType;
    public String uploadDate;
    public String description;

    public String address;

    public List<String> images;

    public Location location;

    public float distance = -1;

    public Activity(String title, String activityType, String uploadDate, String description,List<String> images,Location location,String address, String id) {
        this.title = title;
        this.activityType = activityType;
        this.uploadDate = uploadDate;
        this.description = description;
        this.images=images;
        this.location=location;
        this.address= address;
        this._id = id;


    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return new LatLng(location.latitude,location.longitude);
    }
    @Nullable
    @Override
    public String getTitle() {
        return title;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return null;
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

    public boolean isDraft() {
        return (title.equals("") && description.equals("")  && description.equals(""));
    }

}
