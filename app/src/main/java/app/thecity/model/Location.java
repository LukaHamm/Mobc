package app.thecity.model;

import java.io.Serializable;

public class Location implements Serializable {
    public double latitude;
    public double longitude;

    public String locationname;

    public Location(int latitude, int longitude, String locationname) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationname = locationname;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLocationname() {
        return locationname;
    }

    public void setLocationname(String locationname) {
        this.locationname = locationname;
    }
}
