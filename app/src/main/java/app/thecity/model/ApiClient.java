package app.thecity.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ApiClient implements Serializable {
    // Listen für die Daten: Orte, Kategorien von Orten und Bilder
    public List<Place> places = new ArrayList<>();
    public List<PlaceCategory> place_category = new ArrayList<>();
    public List<Images> images = new ArrayList<>();

    // Standardkonstruktor
    public ApiClient() {
    }

    // Parameterisierter Konstruktor, um die Listen zu initialisieren
    public ApiClient(List<Place> places, List<PlaceCategory> place_category, List<Images> images) {
        this.places = places;
        this.place_category = place_category;
        this.images = images;
    }
}
