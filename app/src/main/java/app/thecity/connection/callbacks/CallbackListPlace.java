package app.thecity.connection.callbacks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import app.thecity.model.Place;

/**
 * Klasse "CallbackListPlace" repräsentiert eine Antwort (Callback) mit einer Liste von "Place"-Objekten.
 * Sie enthält Informationen über den Status, die Anzahl der erhaltenen "Place"-Objekte,
 * die Gesamtanzahl der verfügbaren "Place"-Objekte, die Anzahl der Seiten und eine Liste von "Place"-Objekten.
 */
public class CallbackListPlace implements Serializable {

    public String status = ""; // Der Status der Antwort (z.B. "success" oder "error")
    public int count = -1; // Die Anzahl der erhaltenen "Place"-Objekte in dieser Antwort
    public int count_total = -1; // Die Gesamtanzahl der verfügbaren "Place"-Objekte
    public int pages = -1; // Die Anzahl der Seiten, aufgeteilt basierend auf der Paginierung
    public List<Place> places = new ArrayList<>(); // Eine Liste von "Place"-Objekten, die in der Antwort enthalten sind

}
