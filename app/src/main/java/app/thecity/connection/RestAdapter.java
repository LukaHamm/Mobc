package app.thecity.connection;

import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import app.thecity.AppConfig;
import app.thecity.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Klasse RestAdapter dient dazu, eine Verbindung zur API herzustellen, über die Inhalte für die App
 * geladen werden können. Hier wird Retrofit verwendet, um HTTP-Anfragen an die API zu senden und die
 * Antwort zu verarbeite
 */
public class RestAdapter {

    /**
     * Erstellt eine Retrofit-Instanz für die API-Kommunikation.
     * Verwendet eine längere Verbindungstimeout-Dauer und protokolliert HTTP-Anfragen und -Antworten für Debugging-Zwecke.
     * @return Eine API-Instanz, um API-Anfragen zu erstellen.
     */

    public static API createAPI() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(BuildConfig.DEBUG ? Level.BODY : Level.NONE);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .cache(null)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConfig.general.web_url)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .client(okHttpClient)
                .build();

        return retrofit.create(API.class);
    }

    public static API createMobcApi (){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(BuildConfig.DEBUG ? Level.BODY : Level.NONE);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .cache(null)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConfig.general.web_url_Mobc)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .client(okHttpClient)
                .build();

        return retrofit.create(API.class);
    }

    /**
     * Erstellt eine spezielle kürzere Retrofit-Instanz für die GCM-Registrierung.
     * Verwendet eine kürzere Verbindungstimeout-Dauer, um schneller eine Registrierung durchzuführen.
     * @return Eine API-Instanz, um API-Anfragen für die GCM-Registrierung durchzuführen.
     */
    public static API createShortAPI() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(BuildConfig.DEBUG ? Level.BODY : Level.NONE);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .cache(null)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConfig.general.web_url)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .client(okHttpClient)
                .build();

        return retrofit.create(API.class);
    }
}
