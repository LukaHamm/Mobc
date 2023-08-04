package app.thecity.model;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Place implements Serializable, ClusterItem {
    // Eindeutige ID des Ortes
    public int place_id;
    // Name des Ortes
    public String name = "";
    // Name des Bildes (URL oder Pfad zum Bild) für den Ort
    public String image = "";
    // Adresse des Ortes
    public String address = "";
    // Telefonnummer des Ortes
    public String phone = "";
    // Webseite des Ortes
    public String website = "";
    // Beschreibung des Ortes
    public String description = "";
    // Längengrad des Ortes
    public double lng;
    // Breitengrad des Ortes
    public double lat;
    // Zeitstempel für das letzte Update des Ortes
    public long last_update;
    // Entfernung des Ortes (kann optional für Sortier- oder Filterzwecke verwendet werden)
    public float distance = -1;

    // Liste der Kategorien, zu denen der Ort gehört
    public List<Category> categories = new ArrayList<>();
    // Liste der Bilder des Ortes
    public List<Images> images = new ArrayList<>();

    // Methode zur Rückgabe der Position des Ortes als LatLng-Objekt für die Kartendarstellung
    @Override
    public LatLng getPosition() {
        return new LatLng(lat, lng);
    }

    // Methode zur Überprüfung, ob der Ort als Entwurf gekennzeichnet ist (keine vollständigen Informationen)
    public boolean isDraft() {
        return (address.equals("") && phone.equals("") && website.equals("") && description.equals(""));
    }

    // Methode zur Rückgabe des Titels des Ortes (für die ClusterItem-Schnittstelle, hier wird der Name des Ortes als Titel verwendet)
    @Nullable
    @Override
    public String getTitle() {
        return name;
    }

    // Methode zur Rückgabe des Snippets des Ortes (für die ClusterItem-Schnittstelle, hier wird null zurückgegeben)
    @Nullable
    @Override
    public String getSnippet() {
        return null;
    }

}
