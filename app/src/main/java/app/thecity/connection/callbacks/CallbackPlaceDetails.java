package app.thecity.connection.callbacks;

import java.io.Serializable;

import app.thecity.model.Place;

/**
 * Klasse "CallbackPlaceDetails" repräsentiert eine Antwort (Callback) mit einem einzigen "Place"-Objekt.
 * Sie enthält Informationen über den Ort.
 */
public class CallbackPlaceDetails implements Serializable {

    public Place place = null; // Das "Place"-Objekt, das in der Antwort enthalten ist

}
