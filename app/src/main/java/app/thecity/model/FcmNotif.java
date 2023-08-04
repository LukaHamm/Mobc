package app.thecity.model;

import java.io.Serializable;

public class FcmNotif implements Serializable {
    // Titel der Benachrichtigung
    public String title;
    // Inhalt der Benachrichtigung
    public String content;
    // Typ der Benachrichtigung
    public String type;
    // Ort-Objekt, falls die Benachrichtigung mit einem Ort verknüpft ist
    public Place place;
    // NewsInfo-Objekt, falls die Benachrichtigung mit einer Neuigkeiteninformation verknüpft ist
    public NewsInfo news;
}
