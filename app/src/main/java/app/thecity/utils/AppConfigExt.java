package app.thecity.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;


import com.google.gson.Gson;

import app.thecity.AppConfig;
import app.thecity.data.ThisApplication;


public class AppConfigExt {

    // Definiere statische Variable für die allgemeine App-Konfiguration
    public static AppConfig.General general = new AppConfig.General();

/*
    // Setze Daten aus dem Remote Config
    public static void setFromRemoteConfig(FirebaseRemoteConfig remote) {
        // Allgemeine Konfiguration
        if (!remote.getString("web_url").isEmpty())
            AppConfig.general.web_url = remote.getString("web_url");

        if (!remote.getString("city_lat").isEmpty()) {
            try {
                AppConfig.general.city_lat = Double.parseDouble(remote.getString("city_lat"));
            } catch (Exception ignored) {
            }
        }

        if (!remote.getString("city_lng").isEmpty()) {
            try {
                AppConfig.general.city_lng = Double.parseDouble(remote.getString("city_lng"));
            } catch (Exception ignored) {
            }
        }

        if (!remote.getString("enable_news_info").isEmpty()) {
            AppConfig.general.enable_news_info = Boolean.parseBoolean(remote.getString("enable_news_info"));
        }


        // Weitere Werbekonfigurationseinstellungen

        saveToSharedPreference(); // Speichere die Konfiguration in den Shared Preferences
    }
*/
    // Setze Daten aus den Shared Preferences
    public static void setFromSharedPreference() {
        Context context = ThisApplication.getInstance();
        SharedPreferences pref = context.getSharedPreferences("CONFIG", Context.MODE_PRIVATE);
        String jsonGeneral = pref.getString("APP_CONFIG_GENERAL", null);

        if (!TextUtils.isEmpty(jsonGeneral)) {
            AppConfig.general = new Gson().fromJson(jsonGeneral, AppConfig.General.class);
        }

    }

    // Speichere die Konfigurationsdaten in den Shared Preferences
    private static void saveToSharedPreference() {
        Context context = ThisApplication.getInstance();
        SharedPreferences pref = context.getSharedPreferences("CONFIG", Context.MODE_PRIVATE);
        pref.edit().putString("APP_CONFIG_GENERAL", new Gson().toJson(AppConfig.general)).apply();

    }
}
