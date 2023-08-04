package app.thecity.connection.callbacks;

import java.io.Serializable;

/**
 * Klasse "CallbackDevice" repräsentiert eine Antwort (Callback) von einem Gerät.
 * Sie enthält Informationen über den Status und eine Nachricht.
 */
public class CallbackDevice implements Serializable {
    public String status = ""; // Der Status der Antwort (z.B. "success" oder "error")
    public String message = ""; // Die Nachricht, die in der Antwort enthalten ist (z.B. Erfolgsmeldung oder Fehlermeldung)
}
