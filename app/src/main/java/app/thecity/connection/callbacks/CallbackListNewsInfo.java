package app.thecity.connection.callbacks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import app.thecity.model.NewsInfo;

/**
 * Klasse "CallbackListNewsInfo" repräsentiert eine Antwort (Callback) mit einer Liste von "NewsInfo"-Objekten.
 * Sie enthält Informationen über den Status, die Anzahl der erhaltenen "NewsInfo"-Objekte,
 * die Gesamtanzahl der verfügbaren "NewsInfo"-Objekte, die Anzahl der Seiten und eine Liste von "NewsInfo"-Objekten.
 */
public class CallbackListNewsInfo implements Serializable {

    public String status = ""; // Der Status der Antwort (z.B. "success" oder "error")
    public int count = -1; // Die Anzahl der erhaltenen "NewsInfo"-Objekte in dieser Antwort
    public int count_total = -1; // Die Gesamtanzahl der verfügbaren "NewsInfo"-Objekte
    public int pages = -1; // Die Anzahl der Seiten, aufgeteilt basierend auf der Paginierung
    public List<NewsInfo> news_infos = new ArrayList<>(); // Eine Liste von "NewsInfo"-Objekten, die in der Antwort enthalten sind

}
