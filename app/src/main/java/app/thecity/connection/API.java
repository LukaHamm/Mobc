package app.thecity.connection;

import java.util.List;


import app.thecity.connection.callbacks.CallbackUser;
import app.thecity.model.Activity;
import app.thecity.model.Evaluation;
import app.thecity.model.User;
import app.thecity.model.UserInfo;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Schnittstellenbeschreibung für die API-Anfragen an den Server.
 * Enthält verschiedene Methodenaufrufe für "Place" und "NewsInfo" Informationen sowie die Geräte-Registrierung.
 */
public interface API {

    String CONTENT_TYPE_JSON = "Content-Type: application/json";

    /**
     * HTTP-POST zum Einloggen des Users
     *
     * @param userInfo Passwort und username
     * @return Das User-Objekt mit Token (Authentifizierung)
     */
    @Headers({CONTENT_TYPE_JSON})
    @POST("api/user/login")
        Call<CallbackUser> login(
            @Body UserInfo userInfo
            );

    /**
     * HTTP-POST zum Regestrieren des Nutzers
     * @param user Userdaten (Name, email, Passwort, etc.)
     * @return
     */
    @Headers({CONTENT_TYPE_JSON})
    @POST("api/user/register")
    Call<String> register(
            @Body User user
    );

    /**
     * HTTP-GET zum holen der Aktivitäten in Abhängigkeit von der Kategorie
     * @param category Kategorietyp zum Filtern der Aktivitäten
     * @return Liste von Aktivitäten
     */
    @Headers({CONTENT_TYPE_JSON})
    @GET("/api/activities/")
    Call<List<Activity>> getActivities(
            @Query("category") String category
    );

    /**
     * HTTP-GET um die eigenen geposteten Aktivitäten abzufragen
     * @param token token zur Authentifizierung
     * @return
     */
    @Headers({CONTENT_TYPE_JSON})
    @GET("/api/activities/userId")
    Call<List<Activity>> getOwnActivities(
        @Header("token") String token
    );

    /**
     * HTTP-POST zum Anlegen eines neuen Kommentars zu einer Aktivität
     * @param token zur Authentiizierung
     * @param id der Aktivität
     * @param evaluation Kommentar der gepostet werden soll
     * @return
     */
    @Headers({CONTENT_TYPE_JSON})
    @POST("api/comments/activity/{id}")
    Call<ResponseBody> postCommentsToActivity(
            @Header("token") String token,
            @Path("id") String id,
            @Body Evaluation evaluation
    );

    /**
     * HTTP-POST zum Anlegen einer neuen Aktivität
     * @param token zur Authentifizierung
     * @param activity Aktivität die gepostet werden soll
     * @return
     */
    @Headers({CONTENT_TYPE_JSON})
    @POST("api/activities/")
    Call<Activity> postActivity(
            @Header("token") String token,
            @Body Activity activity
    );

    /**
     * HTTP-GET zum Laden aller Kommentare einer Aktivität
     * @param id der Aktivität
     * @return Liste der Kommentare
     */
    @Headers({CONTENT_TYPE_JSON})
    @GET("/api/comments//activity/{id}")
    Call<List<Evaluation>> getComments(
            @Path("id") String id
    );

    /**
     * HTTP-POST zum Anlegen eines Bildes zu einer Aktivität
     * @param image das Hochgeladen werden soll
     * @param id der Aktivität
     * @param token zur Authenifizierung
     * @return
     */
    @Multipart
    @POST("api/activities/image/{id}") // Der Endpunkt, an den das Bild hochgeladen wird
    Call<ResponseBody> uploadImage(
            @Part MultipartBody.Part image,
            @Path("id") String id,
            @Header("token") String token
    );




}
