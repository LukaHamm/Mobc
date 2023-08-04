package app.thecity.model;

import java.io.Serializable;

public class NewsInfo implements Serializable {
    // Eindeutige ID der News
    public int id;
    // Titel der News
    public String title;
    // Kurzer Inhalt der News
    public String brief_content;
    // Vollständiger Inhalt der News
    public String full_content;
    // Name des Bildes (URL oder Pfad zum Bild) für die News
    public String image;
    // Zeitstempel für das letzte Update der News
    public long last_update;
}
