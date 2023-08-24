package app.thecity.connection;

import app.thecity.connection.callbacks.CallbackDevice;
import app.thecity.connection.callbacks.CallbackListNewsInfo;
import app.thecity.connection.callbacks.CallbackListPlace;
import app.thecity.connection.callbacks.CallbackPlaceDetails;
import app.thecity.connection.callbacks.CallbackUser;
import app.thecity.model.DeviceInfo;
import app.thecity.model.User;
import app.thecity.model.UserInfo;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Schnittstellenbeschreibung für die API-Anfragen an den Server.
 * Enthält verschiedene Methodenaufrufe für "Place" und "NewsInfo" Informationen sowie die Geräte-Registrierung.
 */
public interface API {

    String CACHE = "Cache-Control: max-age=0";
    String AGENT = "User-Agent: Place";

    String CONTENT_TYPE_JSON = "Content-Type: application/json";

    /* Place API transaction ------------------------------- */

    // Ruft eine Liste von Orten basierend auf der Seitennummer, der Anzahl und dem Status (entwurf oder nicht) ab
    @Headers({CACHE, AGENT})
    @GET("app/services/listPlaces")
    Call<CallbackListPlace> getPlacesByPage(
            @Query("page") int page,
            @Query("count") int count,
            @Query("draft") int draft
    );

    // Ruft Details zu einem bestimmten Ort anhand der Ort-ID ab
    @Headers({CACHE, AGENT})
    @GET("app/services/getPlaceDetails")
    Call<CallbackPlaceDetails> getPlaceDetails(
            @Query("place_id") int place_id
    );

    @Headers({CONTENT_TYPE_JSON})
    @POST("api/user/login")
        Call<CallbackUser> login(
            @Body UserInfo userInfo
            );

    @Headers({CONTENT_TYPE_JSON})
    @POST("api/user/register")
    Call<String> register(
            @Body User user
    );

    /* News Info API transaction ------------------------------- */

    // Ruft eine Liste von "NewsInfo" Informationen basierend auf der Seitennummer und der Anzahl ab
    @Headers({CACHE, AGENT})
    @GET("app/services/listNewsInfo")
    Call<CallbackListNewsInfo> getNewsInfoByPage(
            @Query("page") int page,
            @Query("count") int count
    );

    // Registriert das Gerät und erhält eine Antwort als Callback zurück
    @Headers({CACHE, AGENT})
    @POST("app/services/insertGcm")
    Call<CallbackDevice> registerDevice(
            @Body DeviceInfo deviceInfo
    );

}
