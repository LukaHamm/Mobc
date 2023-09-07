package app.thecity.data;

import android.app.Application;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import app.thecity.AppConfig;
import app.thecity.connection.API;
import app.thecity.connection.RestAdapter;
import app.thecity.connection.callbacks.CallbackDevice;
import app.thecity.model.DeviceInfo;
import app.thecity.utils.Tools;
import retrofit2.Call;
import retrofit2.Response;

public class ThisApplication extends Application {

    private Call<CallbackDevice> callback = null;
    private static ThisApplication mInstance;
    private FirebaseAnalytics firebaseAnalytics;
    private FirebaseRemoteConfig firebaseRemoteConfig;
    private Location location = null;
    private SharedPref sharedPref;
    private int fcm_count = 0;
    private final int FCM_MAX_COUNT = 5;

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

    // Ereignis verfolgen
    public void trackEvent(String category, String action, String label) {
        Bundle params = new Bundle();
        category = category.replaceAll("[^A-Za-z0-9_]", "");
        action = action.replaceAll("[^A-Za-z0-9_]", "");
        label = label.replaceAll("[^A-Za-z0-9_]", "");
        params.putString("category", category);
        params.putString("action", action);
        params.putString("label", label);
    }

    public FirebaseRemoteConfig getFirebaseRemoteConfig() {
        return firebaseRemoteConfig;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
