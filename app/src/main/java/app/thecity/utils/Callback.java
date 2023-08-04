package app.thecity.utils;
// Das Callback-Interface ist eine generische Schnittstelle, die für asynchrone Rückrufe verwendet wird.
// Es hat zwei Methoden, onSuccess und onError, die aufgerufen werden, wenn das Ergebnis erfolgreich ist oder einen Fehler enthält.

public interface Callback<T> {

    // Diese Methode wird aufgerufen, wenn das Ergebnis erfolgreich ist.
    // Das generische Typargument T stellt den Datentyp des erfolgreichen Ergebnisses dar.
    void onSuccess(T result);

    // Diese Methode wird aufgerufen, wenn ein Fehler auftritt.
    // Das Argument result enthält die Fehlermeldung oder Fehlerinformationen.
    void onError(String result);
}

