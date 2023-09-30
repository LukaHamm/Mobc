package app.thecity.data;

import android.app.Application;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import app.thecity.connection.callbacks.CallbackDevice;
import retrofit2.Call;

public class ThisApplication extends Application {

    private Call<CallbackDevice> callback = null;
    private static ThisApplication mInstance;


    private Location location = null;
    private SharedPref sharedPref;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(Constant.LOG_TAG, "onCreate : ThisApplication");
        mInstance = this;
        sharedPref = new SharedPref(this);



    }

    public static synchronized ThisApplication getInstance() {
        return mInstance;
    }



    // Bildschirmansicht verfolgen
    public void trackScreenView(String event) {
        Bundle params = new Bundle();
        event = event.replaceAll("[^A-Za-z0-9_]", "");
        params.putString("event", event);
    }


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
