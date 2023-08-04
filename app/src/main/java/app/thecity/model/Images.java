package app.thecity.model;

import java.io.Serializable;

public class Images implements Serializable {
    // ID des Ortes, dem dieses Bild gehört
    public int place_id;
    // Name des Bildes (URL oder Pfad zum Bild)
    public String name;

    public Images() {
    }

    public Images(int place_id, String name) {
        this.place_id = place_id;
        this.name = name;
    }

    // Methode, um die URL des Bildes abzurufen
    public String getImageUrl() {
        return name;
    }
}
