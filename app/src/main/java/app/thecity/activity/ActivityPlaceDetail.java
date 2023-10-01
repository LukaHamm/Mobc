package app.thecity.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import app.thecity.R;
import app.thecity.adapter.AdapterComments;
import app.thecity.adapter.AdapterImageList;
import app.thecity.connection.RestAdapter;
import app.thecity.data.Constant;
import app.thecity.data.SharedPref;
import app.thecity.data.ThisApplication;
import app.thecity.model.Activity;
import app.thecity.model.Evaluation;
import app.thecity.model.Place;
import app.thecity.model.User;
import app.thecity.utils.Tools;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Diese Klasse repräsentiert eine Android-Aktivität, die die Details zu einem bestimmten Ort anzeigt.
 * Die Aktivität zeigt Informationen wie Name, Adresse, Beschreibung, Bilder und Bewertungen des Ortes an.
 */

public class ActivityPlaceDetail extends AppCompatActivity {

    private static final String EXTRA_OBJ = "key.EXTRA_OBJ";
    private static final String EXTRA_NOTIF_FLAG = "key.EXTRA_NOTIF_FLAG";

    /**
     * Navigiert von einer anderen Aktivität zur ActivityPlaceDetail (Detailansicht des Ortes).
     * Akzeptiert die Startaktivität, eine freigegebene Ansicht für die Aktivitätstransition und ein Place-Objekt.
     *
     * @param activity    Die Startaktivität.
     * @param sharedView  Die freigegebene Ansicht für die Aktivitätstransition.
     * @param activityModel Das Place-Objekt, das die Details des Ortes enthält.
     */
    public static void navigate(AppCompatActivity activity, View sharedView, Activity activityModel) {
        // intentNavigation  = Startet Aktivit Place Details
        Intent intentNavigation = new Intent(activity, ActivityPlaceDetail.class);
        intentNavigation.putExtra(EXTRA_OBJ, activityModel); //putExtra fügt zusätzliche Daten ans Intent
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, sharedView, EXTRA_OBJ);
        ActivityCompat.startActivity(activity, intentNavigation, options.toBundle());
    }

    /**
     * Erstellt ein Intent-Objekt zur Navigation von einer Quell-Aktivität zur Ziel-Aktivität
     * und fügt ein Place-Objekt sowie eine Flagge hinzu, die angibt, ob der Ort über eine Benachrichtigung aufgerufen wurde.
     *
     * @param context    Der Kontext der Quell-Aktivität.
     * @param obj        Das Place-Objekt, das die Details des Ortes enthält.
     * @param from_notif Eine Flagge, die angibt, ob der Ort über eine Benachrichtigung aufgerufen wurde.
     * @return Ein Intent-Objekt für die Navigation.
     */
    public static Intent navigateBase(Context context, Place obj, Boolean from_notif) {
        Intent navigation_from_notifi = new Intent(context, ActivityPlaceDetail.class);
        navigation_from_notifi.putExtra(EXTRA_OBJ, obj);
        navigation_from_notifi.putExtra(EXTRA_NOTIF_FLAG, from_notif);
        return navigation_from_notifi;
    }

    private Activity activityModel = null;

    private TextView description = null;
    private View parent_view = null;
    private GoogleMap googleMap;

    private boolean onProcess = false;
    private boolean isFromNotif = false;
    private View lyt_progress;
    private View lyt_distance;
    private float distance = -1;
    private Snackbar snackbar;
    private ArrayList<String> new_images_str = new ArrayList<>();

    private RecyclerView recyclerView;

    private EditText editText;

    private Button button;

    private AdapterComments adapterComments;


    /**
     * Initialisiert die Ansicht, die Toolbar und die Google Map, wenn die Aktivität erstellt wird.
     * Hier wird der von der Hauptaktivität übergebene Ort entgegen genommen und in einer Instanzvariable gespeichert
     * @param savedInstanceState Ein Bundle-Objekt, das den Zustand der Aktivität enthält.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Hier wird die Methode onCreate der übergeordneten Klasse aufgerufen, um die Standardinitialisierungen für die Aktivität durch zuführen.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
        parent_view = findViewById(android.R.id.content);
        recyclerView = (RecyclerView) findViewById(R.id.comments);
        adapterComments = new AdapterComments(getApplicationContext(),new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        editText = findViewById(R.id.commentbody);
        description = findViewById(R.id.description);
        button = findViewById(R.id.commenpostButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postComment();
            }
        });
        recyclerView.setAdapter(adapterComments);
        // animation transition --> App Bas layout siehe Place Details in App
        ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), EXTRA_OBJ);

        // Hier wird der Place aus dem übergebenden Intent (vorherige Activity) extrahiert
        activityModel = (Activity) getIntent().getSerializableExtra(EXTRA_OBJ);
        description.setText(activityModel.description);
        isFromNotif = getIntent().getBooleanExtra(EXTRA_NOTIF_FLAG, false);

        // Initialisierung von Views
        lyt_progress = findViewById(R.id.lyt_progress);
        lyt_distance = findViewById(R.id.lyt_distance);
            if (activityModel.images != null && !activityModel.images.isEmpty()) {
                Tools.displayImage(this, (ImageView) findViewById(R.id.image), Constant.getURLimgActivity(activityModel.images.get(0)));
            }


        // Konfiguration der Toolbar und Initialisierung der Google Map
        setupToolbar(activityModel.title == null ? "" : activityModel.title);
        initMap();


        // for system bar in lollipop
        Tools.systemBarLolipop(this);
        Tools.RTLMode(getWindow());

        // analytics tracking
        ThisApplication.getInstance().trackScreenView("View place : " + (activityModel.title == null ? "name" : activityModel.title));
        displayDataWithDelay(activityModel);
        fetchComments();
    }
    /**
     * Diese Methode ruft Kommentare für die Aktivität vonm Server ab und aktualisiert die Benutzeroberfläche
     * mit den abgerufenen Kommentaren, wenn die Anfrage erfolgreich ist.
     */
    private void fetchComments() {
        // Erstelle eine Anfrage an den Server, um Kommentare für die Aktivität abzurufen
        Call<List<Evaluation>> evaluationCall = RestAdapter.createMobcApi().getComments(activityModel._id);
        evaluationCall.enqueue(new retrofit2.Callback<List<Evaluation>>() {
            @Override
            public void onResponse(Call<List<Evaluation>> call, Response<List<Evaluation>> response) {
                // Verarbeite die Antwort des Servers und aktualisiere die Benutzeroberfläche
                List<Evaluation> evaluationList = response.body();
                if (evaluationList != null) {
                    adapterComments.insertData(evaluationList);
                }
            }

            @Override
            public void onFailure(Call<List<Evaluation>> call, Throwable t) {
                // Behandle den Fehlerfall und protokolliere eventuelle Fehlermeldungen
                Log.e("onFailure", t.getMessage());
            }
        });
    }

    /**
     * Diese Methode postet einen neuen Kommentar zur Aktivität an den Server und aktualisiert die
     * Kommentarliste, wenn der Postvorgang erfolgreich ist.
     */
    private void postComment() {
        // Holen des eingegebenen Text aus dem EditText-Feld
        String text = editText.getText().toString();
        editText.setText("");
        editText.clearFocus();

        // Lesen des Benutzers aus der Anwendung
        User user = Tools.readuser(getApplicationContext());

        // Erstellen eines Authentifizierungs-Headers für die Anfrage
        String header = "bearer " + user.token;

        // Überprüfen, ob der eingegebene Text nicht leer ist
        if (text != null && !text.isEmpty()) {
            // Erstellen des Evaluation-Objekts mit dem eingegebenen Text
            Evaluation evaluation = new Evaluation(text, "", 0, null, "");

            // Erstellen der Anfrage an den Server, um den Kommentar zu posten
            Call<ResponseBody> callPostComment = RestAdapter.createMobcApi().postCommentsToActivity(header, activityModel._id, evaluation);
            callPostComment.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    // Wenn der Postvorgang erfolgreich ist, wird die Methode fetchComments aufgerufen, um die Kommentare zu aktualisieren
                    fetchComments();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // Behandle den Fehlerfall und protokolliere eventuelle Fehlermeldungen
                    Log.e("onFailure", t.getMessage());
                }
            });
        }
    }

    /**
     * Diese Methode zeigt die Daten des angegebenen Place-Objekts an, einschließlich Name, Adresse, Telefon,
     * Website, Beschreibung und Bildergalerie.
     *
     * @param activity Das Ort-Objekt, dessen Daten angezeigt werden sollen.
     */
    private void displayData(Activity activity) {
        // Konfiguriere die Toolbar mit dem Ortsnamen
        setupToolbar(activityModel.title);

        // Zeige das Bild des Ortes in einem ImageView an
        if (activityModel.images != null && !activityModel.images.isEmpty()) {
            Tools.displayImage(this, (ImageView) findViewById(R.id.image), Constant.getURLimgActivity(activityModel.images.get(0)));
        }

        // Setze die Adresse, Telefonnummer und Website des Ortes in den entsprechenden TextViews
        ((TextView) findViewById(R.id.address)).setText(activity.address);


        // Setze die Entfernungsinformation des Ortes in den TextView für die Entfernung
        distance = activityModel.distance;
        if (distance == -1) {
            lyt_distance.setVisibility(View.GONE);
        } else {
            lyt_distance.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.distance)).setText(Tools.getFormatedDistance(distance));
        }

        // Zeige die Bildergalerie des Ortes anhand der Bilder in der Datenbank
        setImageGallery(activityModel.images);
    }



    /**
     * Eine Methode, die aufgerufen wird, wenn auf bestimmte Layout-Elemente wie Adresse, Telefon
     * oder Website geklickt wird, um die entsprechenden Aktionen auszuführen
     * Es wird die Maps-Aktivität mit dem entsprechnden Ort-Objekt sowie Lämgen- und Breitengrad aufgerufen
     *
     * @param view Das geklickte Layout-Element.
     */
    public void clickLayout(View view) {
        int id = view.getId();
        if (id == R.id.lyt_address) {
            if (!activityModel.isDraft()) {
                Uri uri = Uri.parse("http://maps.google.com/maps?q=loc:" + activityModel.location.latitude + "," + activityModel.location.longitude);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        }
    }

    /**
     * Diese Methode zeigt eine Bildergalerie des Ortes an, die aus Bildern besteht, die im Ort-Objekt enthalten sind.
     *
     * @param images Die Liste der Bild-IDs.
     */
    private void setImageGallery(List<String> images) {
        // add optional image into list
        List<String> new_images = new ArrayList<>();
        new_images.addAll(images);
        new_images_str = new ArrayList<>();
        for (String img : new_images) {
            new_images_str.add(Constant.getURLimgActivity(img));
        }

        RecyclerView galleryRecycler = (RecyclerView) findViewById(R.id.galleryRecycler);
        galleryRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        AdapterImageList adapter = new AdapterImageList(this, new_images);
        galleryRecycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new AdapterImageList.OnItemClickListener() {
            @Override
            public void onItemClick(View view, String viewModel, int pos) {
                openImageGallery(pos);
            }
        });
    }

    /**
     * Öffnet die Bildergalerie mit dem angegebenen Startbild.
     *
     * @param position Die Position des Startbilds.
     */
    private void openImageGallery(int position) {
        Intent openImageIntent = new Intent(ActivityPlaceDetail.this, ActivityFullScreenImage.class);
        openImageIntent.putExtra(ActivityFullScreenImage.EXTRA_POS, position);
        openImageIntent.putStringArrayListExtra(ActivityFullScreenImage.EXTRA_IMGS, new_images_str);
        startActivity(openImageIntent);
    }

    /**
     * Konfiguriert die Toolbar und CollapsingToolbarLayout und zeigt den Namen des Ortes an.
     *
     * @param name Der Name des Ortes.
     */
    private void setupToolbar(String name) {
        // Finde die Toolbar-View mit der ID "toolbar" und setze sie als Support-Actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Erhalte eine Referenz zur Support-Actionbar und zeige den Zurück-Pfeil an
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Setze den Titel der Actionbar auf leer, um den Standardtitel zu verbergen
        actionBar.setTitle("");

        // Setze den Namen des Ortes als Text im Titel der Toolbar
        ((TextView) findViewById(R.id.toolbar_title)).setText(name);

        // Finde das CollapsingToolbarLayout und setze die Farbe für den Hintergrund
        CollapsingToolbarLayout collapsing_toolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsing_toolbar.setContentScrimColor(new SharedPref(this).getThemeColorInt());

        // Füge einen OnClickListener zum ImageView mit der ID "image" hinzu
        (findViewById(R.id.image)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Überprüfe, ob es Bilder in der Bildergalerie gibt
                if (new_images_str == null || new_images_str.size() <= 0) return;
                // Öffne die Bildergalerie mit dem ersten Bild (Position 0)
                openImageGallery(0);
            }
        });
    }


    /**
     * Reagiert auf Klicks auf die Menüelemente, das Teilen des Ortes oder Zurücktaste.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            backAction();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Initialisiert die Google Map und konfiguriert die Kartenansicht in den FrameLayout.
     */
    private void initMap() {
        if (googleMap == null) {
            MapFragment mapFragment1 = (MapFragment) getFragmentManager().findFragmentById(R.id.mapPlaces);
            mapFragment1.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap gMap) {
                    googleMap = gMap;
                    if (googleMap == null) {
                        Snackbar.make(parent_view, R.string.unable_create_map, Snackbar.LENGTH_SHORT).show();
                    } else {
                        // Konfiguriere die Karte
                        googleMap = Tools.configStaticMap(ActivityPlaceDetail.this, googleMap, activityModel);
                    }
                }
            });
        }

        /**
         * KlickListner auf das Distanzfeld um die Maps-Aktivität zu starten
         */
        ((Button) findViewById(R.id.bt_navigate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent navigation = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=" + activityModel.location.latitude + "," + activityModel.location.longitude));
                startActivity(navigation);
            }
        });
        /**
         * KlickListner für den Ansicht-Button um die MapsActivity zu starten
         */
        ((Button) findViewById(R.id.bt_view)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlaceInMap();
            }
        });
        /**
         * KlickListner für die Karte um die MapsActivity zu starten
         */
        ((LinearLayout) findViewById(R.id.map)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlaceInMap();
            }
        });
    }

    /**
     * Öffnet den ausgewählten Ort in der Google Maps-Anwendung.
     * Übergibt das Ort-Objekt der Maps-Activity
     */
    private void openPlaceInMap() {
        Intent openPlaceMap = new Intent(this, ActivityMaps.class);
        openPlaceMap.putExtra(ActivityMaps.EXTRA_OBJ, activityModel);
        startActivity(openPlaceMap);
    }



    /**
     * Diese Methode wird aufgerufen, wenn die Zurück-Taste des Geräts gedrückt wird. Hier wird festgelegt,
     * wie die Aktivität beendet wird, basierend auf dem Wert von isFromNotif.
     */
    @Override
    public void onBackPressed() {
        backAction();
    }


    /**
     * Diese Hilfsmethode legt fest, wie die Aktivität beendet wird, abhängig davon,
     * ob sie von einer Benachrichtigung gestartet wurde oder nicht.
     */
    private void backAction() {
        if (isFromNotif) {
            Intent openMainActivity = new Intent(this, ActivityMain.class);
            startActivity(openMainActivity);
        }
        finish();
    }

    /**
     * Diese Methode zeigt die Daten des Ortes mit einer leichten Verzögerung an,
     * um die Benutzeroberfläche responsiver zu machen.
     *
     * @param resp Das Place-Objekt mit den Daten.
     */
    private void displayDataWithDelay(final Activity resp) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showProgressbar(false);
                onProcess = false;
                displayData(resp);
            }
        }, 1000);
    }

    /**
     * Diese Methode zeigt die Snackbar-Nachricht periodisch erneut an, wenn sie noch nicht angezeigt wird.
     */
    private void retryDisplaySnackbar() {
        if (snackbar != null && !snackbar.isShown()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    retryDisplaySnackbar();
                }
            }, 1000);
        }
    }

    /**
     * Diese Methode zeigt oder verbirgt die Fortschrittsanzeige.
     *
     * @param show Ein Wert, der angibt, ob die Fortschrittsanzeige angezeigt oder verborgen werden soll.
     */
    private void showProgressbar(boolean show) {
        lyt_progress.setVisibility(show ? View.VISIBLE : View.GONE);
    }


}
