package app.thecity.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;

import app.thecity.activity.ActivityNewsInfoDetails;
import app.thecity.activity.ActivityPlaceDetail;
import app.thecity.activity.ActivitySplash;
import app.thecity.R;
import app.thecity.AppConfig;
import app.thecity.data.Constant;
import app.thecity.data.DatabaseHandler;
import app.thecity.data.SharedPref;
import app.thecity.model.FcmNotif;
import app.thecity.model.NewsInfo;
import app.thecity.model.Place;
import app.thecity.utils.Tools;
public class FcmMessagingService extends FirebaseMessagingService {

    // Konstante für die Vibrationsdauer
    private static int VIBRATION_TIME = 500; // in Millisekunden
    private SharedPref sharedPref;

    // Diese Methode wird aufgerufen, wenn ein neuer FCM-Token generiert wird.
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        sharedPref = new SharedPref(this);
        sharedPref.setFcmRegId(s);
    }

    // Diese Methode wird aufgerufen, wenn eine FCM-Nachricht empfangen wird.
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sharedPref = new SharedPref(this);

        // Setze die Flagge zum Aktualisieren der Ortsdaten.
        sharedPref.setRefreshPlaces(true);

        // Lösche den Bildcache im Hintergrund, falls in der Konfiguration aktiviert.
        if (AppConfig.general.refresh_img_notif) {
            Tools.clearImageCacheOnBackground(this);
        }

        // Überprüfe, ob Benachrichtigungen aktiviert sind.
        if (sharedPref.getNotification()) {
            final FcmNotif fcmNotif = new FcmNotif();
            if (remoteMessage.getData().size() > 0) {
                // Nachricht enthält Daten.
                Map<String, String> data = remoteMessage.getData();
                fcmNotif.title = data.get("title");
                fcmNotif.content = data.get("content");
                fcmNotif.type = data.get("type");

                // Lade Daten des Ortes, falls vorhanden.
                String place_str = data.get("place");
                fcmNotif.place = place_str != null ? new Gson().fromJson(place_str, Place.class) : null;

                // Lade Daten der NewsInfo, falls vorhanden.
                String news_str = data.get("news");
                fcmNotif.news = news_str != null ? new Gson().fromJson(news_str, NewsInfo.class) : null;

            } else if (remoteMessage.getNotification() != null) {
                // Nachricht enthält nur Benachrichtigungsinformationen.
                RemoteMessage.Notification rn = remoteMessage.getNotification();
                fcmNotif.title = rn.getTitle();
                fcmNotif.content = rn.getBody();
            }

            // Lade das Bild von der URL und rufe das entsprechende Callback auf.
            loadRetryImageFromUrl(this, fcmNotif, new CallbackImageNotif() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    displayNotificationIntent(fcmNotif, bitmap);
                }

                @Override
                public void onFailed(String string) {
                    displayNotificationIntent(fcmNotif, null);
                }
            });
        }
    }

    // Zeige die Benachrichtigung an und starte den entsprechenden Intent.
    private void displayNotificationIntent(FcmNotif fcmNotif, Bitmap bitmap) {
        playRingtoneVibrate(this);
        Intent intent = new Intent(this, ActivitySplash.class);

        // Erstelle den Intent basierend auf dem Typ der Benachrichtigung (Ort oder NewsInfo).
        if (fcmNotif.place != null) {
            intent = ActivityPlaceDetail.navigateBase(this, fcmNotif.place, true);
        } else if (fcmNotif.news != null) {
            new DatabaseHandler(this).refreshTableNewsInfo();
            intent = ActivityNewsInfoDetails.navigateBase(this, fcmNotif.news, true);
        }

        // Füge die Flagge hinzu, um die vorherigen Activities zu löschen.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }

        // Erstelle die Benachrichtigung mit den entsprechenden Eigenschaften.
        String channelId = getString(R.string.default_notification_channel_id);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setContentTitle(fcmNotif.title);
        builder.setContentText(fcmNotif.content);
        builder.setDefaults(Notification.DEFAULT_LIGHTS);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);

        // Priorisiere die Benachrichtigung auf Android-Versionen ab Jelly Bean.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setPriority(Notification.PRIORITY_HIGH);
        }

        // Füge ein großes Bild zur Benachrichtigung hinzu, falls vorhanden.
        if (bitmap != null) {
            builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).setSummaryText(fcmNotif.content));
        } else {
            // Ansonsten zeige nur den Textinhalt.
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(fcmNotif.content));
        }

        // Zeige die Benachrichtigung über den NotificationManager an.
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Erstelle den Notification Channel für Android-Versionen ab Oreo.
            NotificationChannel channel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }
        // Generiere eine eindeutige ID für die Benachrichtigung.
        int unique_id = (int) System.currentTimeMillis();
        notificationManager.notify(unique_id, builder.build());
    }

    // Spiele den Klingelton ab und lasse das Gerät vibrieren.
    private void playRingtoneVibrate(Context context) {
        try {
            // Vibration abspielen, falls aktiviert.
            if (sharedPref.getVibration()) {
                ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(VIBRATION_TIME);
            }
            // Klingelton abspielen.
            RingtoneManager.getRingtone(context, Uri.parse(sharedPref.getRingtone())).play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lade das Bild erneut von der URL.
    private void loadRetryImageFromUrl(final Context ctx, final FcmNotif fcmNotif, final CallbackImageNotif callback) {
        String url = "";
        if (fcmNotif.place != null) {
            url = Constant.getURLimgPlace(fcmNotif.place.image);
        } else if (fcmNotif.news != null) {
            url = Constant.getURLimgNews(fcmNotif.news.image);
        } else {
            // Callback aufrufen, falls kein Bild vorhanden ist.
            callback.onFailed("");
            return;
        }

        // Lade das Bild mit Glide.
        glideLoadImageFromUrl(ctx, url, new CallbackImageNotif() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                callback.onSuccess(bitmap);
            }

            @Override
            public void onFailed(String string) {
                Log.e("onFailed", "on Failed");
                callback.onFailed("");
            }
        });
    }

    // Lade das Bild mit Glide und rufe das entsprechende Callback auf.
    Handler mainHandler = new Handler(Looper.getMainLooper());
    Runnable myRunnable;
    private void glideLoadImageFromUrl(final Context ctx, final String url, final CallbackImageNotif callback) {
       myRunnable = new Runnable() {
            @Override
            public void run() {
                Glide.with(ctx).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                        callback.onSuccess(bitmap);
                        mainHandler.removeCallbacks(myRunnable);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        callback.onFailed("On Load Failed");
                        mainHandler.removeCallbacks(myRunnable);
                    }
                });
            }
        };
        mainHandler.post(myRunnable);
    }

    // Callback-Interface für das Laden des Bildes
    public interface CallbackImageNotif {

        void onSuccess(Bitmap bitmap);

        void onFailed(String string);

    }
}
