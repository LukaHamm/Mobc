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

        initFirebase();
        initRemoteConfig();
        initFirebaseAnalytics();

        // Firebase Cloud Messaging-Token abrufen und Gerät auf dem Server registrieren
        obtainFirebaseToken();
    }

    public static synchronized ThisApplication getInstance() {
        return mInstance;
    }

    // Firebase Cloud Messaging-Token abrufen
    private void obtainFirebaseToken() {
        fcm_count++;
        Log.d("FCM_SUBMIT", "obtainFirebaseToken");
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d("FCM_SUBMIT", "obtainFirebaseToken : " + fcm_count + "-onFailure : " + task.getException().getMessage());
                if (fcm_count > FCM_MAX_COUNT) return;
                obtainFirebaseToken();
            } else {
                // Neue FCM-Registrierungstoken erhalten
                String token = task.getResult();
                Log.d("FCM_SUBMIT", "obtainFirebaseToken : " + fcm_count + "onSuccess");
                sharedPref.setFcmRegId(token);
                if (!TextUtils.isEmpty(token)) sendRegistrationToServer(token);
            }
        });
    }

    /**
     * --------------------------------------------------------------------------------------------
     * For Firebase Cloud Messaging
     */
    // Das Registrierungstoken an den Server senden
    private void sendRegistrationToServer(String token) {
        if (Tools.cekConnection(this) && !TextUtils.isEmpty(token)) {
            API api = RestAdapter.createAPI();
            DeviceInfo deviceInfo = Tools.getDeviceInfo(this);
            deviceInfo.setRegid(token);

            callback = api.registerDevice(deviceInfo);
            callback.enqueue(new retrofit2.Callback<CallbackDevice>() {
                @Override
                public void onResponse(Call<CallbackDevice> call, Response<CallbackDevice> response) {
                    CallbackDevice resp = response.body();
                }

                @Override
                public void onFailure(Call<CallbackDevice> call, Throwable t) {
                }
            });
        }
    }

    // Firebase und Firebase Analytics initialisieren
    private void initFirebase() {
        FirebaseApp.initializeApp(this);
        FirebaseAnalytics.getInstance(this);
    }

    /* Für Remote Config */
    private void initRemoteConfig() {
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        if (!AppConfig.USE_REMOTE_CONFIG) return;
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(60)
                .setFetchTimeoutInSeconds(3)
                .build();
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);
    }

    /* Für Google Analytics */
    public synchronized FirebaseAnalytics initFirebaseAnalytics() {
        if (firebaseAnalytics == null && AppConfig.general.enable_analytics) {
            firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        }
        return firebaseAnalytics;
    }

    // Bildschirmansicht verfolgen
    public void trackScreenView(String event) {
        if (firebaseAnalytics == null || !AppConfig.general.enable_analytics) return;
        Bundle params = new Bundle();
        event = event.replaceAll("[^A-Za-z0-9_]", "");
        params.putString("event", event);
        firebaseAnalytics.logEvent(event, params);
    }

    // Ereignis verfolgen
    public void trackEvent(String category, String action, String label) {
        if (firebaseAnalytics == null || !AppConfig.general.enable_analytics) return;
        Bundle params = new Bundle();
        category = category.replaceAll("[^A-Za-z0-9_]", "");
        action = action.replaceAll("[^A-Za-z0-9_]", "");
        label = label.replaceAll("[^A-Za-z0-9_]", "");
        params.putString("category", category);
        params.putString("action", action);
        params.putString("label", label);
        firebaseAnalytics.logEvent("EVENT", params);
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
