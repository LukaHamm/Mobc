package app.thecity;

import java.io.Serializable;

import app.thecity.utils.AppConfigExt;

/**
 * Diese Datei enthält Konfigurationsdaten für die App. Sie können Werbung, Benachrichtigungen und allgemeine Daten in dieser Datei konfigurieren.
 * Einige Werte sind nicht erklärt und können anhand des Variablennamens leicht verstanden werden.
 * Der Wert kann optional remote geändert werden. Bitte lesen Sie die Dokumentation, um die Anweisungen zu befolgen.
 *
 * Variablen mit GROSSBUCHSTABEN werden NICHT von Remote-Konfiguration abgerufen / ersetzt.
 * Variablen mit KLEINBUCHSTABEN werden von Remote-Konfiguration abgerufen / ersetzt.
 * Siehe Video-Tutorial zur Remote-Konfiguration: https://www.youtube.com/watch?v=tOKXwOTqOzA
 */
public class AppConfig extends AppConfigExt implements Serializable {

    /* Legen Sie "true" fest, um die Konfiguration mit Firebase Remote Config abzurufen. */
    public static final boolean USE_REMOTE_CONFIG = false;

    /* Erzwinge die Layout-Richtung von rechts nach links (RTL) */
    public static final boolean RTL_LAYOUT = false;

    /* Konfiguration für die allgemeine Anwendung */
    public static class General implements Serializable {

        /* Bearbeiten Sie WEB_URL mit Ihrer URL. Stellen Sie sicher, dass am Ende der URL ein Schrägstrich ('/') vorhanden ist. */
        public String web_url = "https://demo.dream-space.web.id/the_city/";

        public String web_url_Mobc = "https://grumpy-calf-jacket.cyclic.cloud/";

        /* Standard-Zoomstufe für die Karte */
        public double city_lat = -6.9174639;
        public double city_lng = 107.6191228;

        /* Dieses Flag, wenn Sie das Menü "News Info" ausblenden möchten */
        public boolean enable_news_info = true;

        /* Wenn Sie mehr als 200 Elemente haben, setzen Sie dieses Flag auf TRUE */
        public boolean lazy_load = false;

        /* Flag für die Nachverfolgung von Analysen */
        public boolean enable_analytics = true;

        /* Bildcache löschen, wenn Push-Benachrichtigungen empfangen werden */
        public boolean refresh_img_notif = true;

        /* Wenn der Benutzer GPS aktiviert, werden Orte nach Entfernung sortiert */
        public boolean sort_by_distance = true;

        /* Entfernungsmetrik, füllen Sie entweder mit "KILOMETER" oder "MILE" */
        public String distance_metric_code = "KILOMETER";

        /* Zugehöriger UI-Anzeigestring für die Entfernungsmetrik */
        public String distance_metric_str = "Km";

        /* Flag für die Aktivierung / Deaktivierung der Theme-Farbwähler-Funktion in den Einstellungen */
        public boolean theme_color = true;

        /* "true" für das Öffnen von Links im internen App-Browser, nicht im externen Browser der App */
        public boolean open_link_in_app = true;

        /* Dieser Grenzwert wird verwendet, um eine Seitennummerierung (Anforderung und Anzeige) zu erhalten, um die Datenlast zu verringern */
        public int limit_place_request = 40;
        public int limit_loadmore = 40;
        public int limit_news_request = 40;


    }


}
