package app.thecity.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import app.thecity.R;

public class SharedPref {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences prefs;

    // SharedPreferences-Schlüssel
    private static final String FCM_PREF_KEY = "app.thecity.data.FCM_PREF_KEY";
    private static final String SERVER_FLAG_KEY = "app.thecity.data.SERVER_FLAG_KEY";
    private static final String THEME_COLOR_KEY = "app.thecity.data.THEME_COLOR_KEY";
    private static final String LAST_PLACE_PAGE = "LAST_PLACE_PAGE_KEY";

    // Schlüssel für die Aktualisierung der Benutzerdaten
    public static final String REFRESH_PLACES = "app.thecity.data.REFRESH_PLACES";

    public SharedPref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("MAIN_PREF", Context.MODE_PRIVATE);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    // Registrierungsstatus für den Server speichern und abrufen
    public void setRegisteredOnServer(boolean registered) {
        sharedPreferences.edit().putBoolean(SERVER_FLAG_KEY, registered).apply();
    }

    public boolean isRegisteredOnServer() {
        return sharedPreferences.getBoolean(SERVER_FLAG_KEY, false);
    }

    // Einstellungen für Benachrichtigungen abrufen
    public boolean getNotification() {
        return prefs.getBoolean(context.getString(R.string.pref_key_notif), true);
    }

    public String getRingtone() {
        return prefs.getString(context.getString(R.string.pref_key_ringtone), "content://settings/system/notification_sound");
    }

    public boolean getVibration() {
        return prefs.getBoolean(context.getString(R.string.pref_key_vibrate), true);
    }

    // Überprüfen, ob eine Aktualisierung der Benutzerdaten erforderlich ist
    public boolean isRefreshPlaces() {
        return sharedPreferences.getBoolean(REFRESH_PLACES, false);
    }

    public void setRefreshPlaces(boolean need_refresh) {
        sharedPreferences.edit().putBoolean(REFRESH_PLACES, need_refresh).apply();
    }

    // App-Themenfarbe speichern und abrufen
    public void setThemeColor(String color) {
        sharedPreferences.edit().putString(THEME_COLOR_KEY, color).apply();
    }

    public String getThemeColor() {
        return sharedPreferences.getString(THEME_COLOR_KEY, "");
    }

    public int getThemeColorInt() {
        if (getThemeColor().equals("")) {
            return context.getResources().getColor(R.color.colorPrimary);
        }
        return Color.parseColor(getThemeColor());
    }

    // Letzte Seitennummer für Ortsanfragen speichern und abrufen
    public void setLastPlacePage(int page) {
        sharedPreferences.edit().putInt(LAST_PLACE_PAGE, page).apply();
    }

    public int getLastPlacePage() {
        return sharedPreferences.getInt(LAST_PLACE_PAGE, 1);
    }

    // Dialogberechtigungszustände speichern und abrufen
    public void setNeverAskAgain(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean getNeverAskAgain(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    // Zähler für Interstitial-Anzeigen speichern, abrufen und zurücksetzen
    public void setIntersCounter(int counter) {
        sharedPreferences.edit().putInt("INTERS_COUNT", counter).apply();
    }

    public int getIntersCounter() {
        return sharedPreferences.getInt("INTERS_COUNT", 0);
    }

    public void clearIntersCounter() {
        sharedPreferences.edit().putInt("INTERS_COUNT", 0).apply();
    }

    // Methode zur Speicherung des Anmeldestatus
    public void setLoggedIn(boolean loggedIn) {
        sharedPreferences.edit().putBoolean("LOGGED_IN", loggedIn).apply();
    }

    // Methode zum Abrufen des Anmeldestatus
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean("LOGGED_IN", false);
    }

    // Methode zum Speichern von Benutzernamen und E-Mail-Adresse
    public void setUserInfo(String username, String email) {
        sharedPreferences.edit().putString("USERNAME", username).apply();
        sharedPreferences.edit().putString("EMAIL", email).apply();
    }

    // Methode zum Abrufen des Benutzernamens
    public String getUsername() {
        return sharedPreferences.getString("USERNAME", "");
    }

    // Methode zum Abrufen der E-Mail-Adresse
    public String getEmail() {
        return sharedPreferences.getString("EMAIL", "");
    }
}
