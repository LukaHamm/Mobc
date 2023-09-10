package app.thecity.model;

public class Location {
    public int latitude;
    public int longitude;

    public String locationname;

    public Location(int latitude, int longitude, String locationname) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationname = locationname;
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public String getLocationname() {
        return locationname;
    }

    public void setLocationname(String locationname) {
        this.locationname = locationname;
    }
}
