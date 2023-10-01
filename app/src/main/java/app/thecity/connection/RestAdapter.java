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


public class RestAdapter {

    /**
     * Erstellt eine Retrofit-Instanz für die API-Kommunikation.
     * Verwendet eine längere Verbindungstimeout-Dauer und protokolliert HTTP-Anfragen und -Antworten für Debugging-Zwecke.
     * @return Eine API-Instanz, um API-Anfragen zu erstellen.
     */

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




}
