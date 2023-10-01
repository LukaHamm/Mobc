package app.thecity.data;

import app.thecity.AppConfig;

/**
 * Hilfsklasse "Constant" enthält verschiedene Konstanten und Methoden, die in der Anwendung verwendet werden.
 */
public class Constant {

    /**
     * Baut die URL für die Orte
     * @param file_name
     * @return
     */
    public static String getURLimgActivity(String file_name) {
        return AppConfig.general.web_url_Mobc + "api/activities/image/" + file_name;
    }

    // Konstante für das Tag von Suchprotokollen (Logging)
    public static final String LOG_TAG = "CITY_LOG";

}
