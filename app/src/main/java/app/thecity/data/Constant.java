package app.thecity.data;

import app.thecity.AppConfig;

/**
 * Hilfsklasse "Constant" enthält verschiedene Konstanten und Methoden, die in der Anwendung verwendet werden.
 */
public class Constant {

    // URL für Bilder von Orten
    public static String getURLimgPlace(String file_name) {
        return AppConfig.general.web_url + "uploads/place/" + file_name;
    }

    // URL für Bilder von NewsInfo
    public static String getURLimgNews(String file_name) {
        return AppConfig.general.web_url + "uploads/news/" + file_name;
    }

    // Konstante für das Tag von Suchprotokollen (Logging)
    public static final String LOG_TAG = "CITY_LOG";

    // Aufzählung für Google Analytics Ereignis Kategorien
    public enum Event {
        FAVORITES,    // Favoriten
        THEME,        // Thema (Design)
        NOTIFICATION, // Benachrichtigung
        REFRESH       // Aktualisieren
    }
}
