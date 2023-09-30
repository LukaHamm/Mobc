package app.thecity.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import app.thecity.R;

public class SharedPref {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences prefs;

    // SharedPreferences-Schlüssel
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


    // Dialogberechtigungszustände speichern und abrufen
    public void setNeverAskAgain(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean getNeverAskAgain(String key) {
        return sharedPreferences.getBoolean(key, false);
    }


}
