package app.thecity.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
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
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import app.thecity.AppConfig;
import app.thecity.R;
import app.thecity.adapter.AdapterImageList;

import app.thecity.connection.RestAdapter;
import app.thecity.connection.callbacks.CallbackPlaceDetails;
import app.thecity.data.Constant;
import app.thecity.data.DatabaseHandler;
import app.thecity.data.SharedPref;
import app.thecity.data.ThisApplication;
import app.thecity.model.Activity;
import app.thecity.model.Images;
import app.thecity.model.Place;
import app.thecity.utils.Tools;
import retrofit2.Call;
import retrofit2.Response;

/*
       BEARBEITET
     Android-Aktivität (Activity), die die Details zu einem bestimmten Ort anzeigt
 */

public class ActivityPlaceDetail extends AppCompatActivity {

    private static final String EXTRA_OBJ = "key.EXTRA_OBJ";
    private static final String EXTRA_NOTIF_FLAG = "key.EXTRA_NOTIF_FLAG";

    /*
      Eine statische Methode zum Navigieren zur ActivityPlaceDetail (Detailsansicht des Standortes)
      von einer anderen Aktivität aus. Sie akzeptiert die Startaktivität (activity),
      eine freigegebene Ansicht (sharedView) für die Aktivitätstransition und ein Place-Objekt (p),
      das die Details des Ortes enthält.
     */
    public static void navigate(AppCompatActivity activity, View sharedView, Activity activityModel) {
        // intentNavigation  = Startet Aktivit Place Details
        Intent intentNavigation = new Intent(activity, ActivityPlaceDetail.class);
        intentNavigation.putExtra(EXTRA_OBJ, activityModel); //putExtra fügt zusätzliche Daten ans Intent
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, sharedView, EXTRA_OBJ);
        ActivityCompat.startActivity(activity, intentNavigation, options.toBundle());
    }

    /*
    Die statische Methode "navigateBase" erstellt ein Intent-Objekt zur Navigation von einer
    Quell-Aktivität zur Ziel-Aktivität und fügt ein "Place"-Objekt sowie eine Flagge hinzu,
    die angibt, ob der Standort über eine Benachrichtigung aufgerufen wurde.
     */
    public static Intent navigateBase(Context context, Place obj, Boolean from_notif) {
        Intent navigation_from_notifi = new Intent(context, ActivityPlaceDetail.class);
        navigation_from_notifi.putExtra(EXTRA_OBJ, obj);
        navigation_from_notifi.putExtra(EXTRA_NOTIF_FLAG, from_notif);
        return navigation_from_notifi;
    }

    private Activity activityModel = null;

    private FloatingActionButton fab;
    private WebView description = null;
    private View parent_view = null;
    private GoogleMap googleMap;


    private boolean onProcess = false;
    private boolean isFromNotif = false;
    private Call<CallbackPlaceDetails> callback;
    private View lyt_progress;
    private View lyt_distance;
    private float distance = -1;
    private Snackbar snackbar;
    private ArrayList<String> new_images_str = new ArrayList<>();

    // Initialisiert die Ansicht, die Toolbar und die Google Map.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Hier wird die Methode onCreate der übergeordneten Klasse aufgerufen, um die Standardinitialisierungen für die Aktivität durch zuführen.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
        parent_view = findViewById(android.R.id.content);

        // animation transition --> App Bas layout siehe Place Details in App
        ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), EXTRA_OBJ);

        // Hier wird der Place aus dem übergebenden Intent (vorherige Activity) extrahiert
        activityModel = (Activity) getIntent().getSerializableExtra(EXTRA_OBJ);
        isFromNotif = getIntent().getBooleanExtra(EXTRA_NOTIF_FLAG, false);

        // Initialisierung von Views
        fab = (FloatingActionButton) findViewById(R.id.fab);
        lyt_progress = findViewById(R.id.lyt_progress);
        lyt_distance = findViewById(R.id.lyt_distance);
            if (activityModel.images != null && activityModel.images.isEmpty()) {
                Tools.displayImage(this, (ImageView) findViewById(R.id.image), Constant.getURLimgActivity(activityModel.images.get(0)));
            }

        // Methode zum Steuern des Favoriten-Buttons
        //favAktualisieren();

        // Konfiguration der Toolbar und Initialisierung der Google Map
        setupToolbar(activityModel.title == null ? "" : activityModel.title);
        initMap();


        // for system bar in lollipop
        Tools.systemBarLolipop(this);
        Tools.RTLMode(getWindow());

        // analytics tracking
        ThisApplication.getInstance().trackScreenView("View place : " + (activityModel.title == null ? "name" : activityModel.title));
        displayDataWithDelay(activityModel);
    }


    /*
      Zeigt die Daten des angegebenen Place-Objekts an, einschließlich Name, Adresse, Telefon,
      Website, Beschreibung und Bildergalerie.
     */
    private void displayData(Activity activity) {
        // Konfiguriere die Toolbar mit dem Ortsnamen
        setupToolbar(activityModel.title);

        // Zeige das Bild des Ortes in einem ImageView an
        Tools.displayImage(this, (ImageView) findViewById(R.id.image), Constant.getURLimgActivity(activityModel.images.get(0)));

        // Setze die Adresse, Telefonnummer und Website des Ortes in den entsprechenden TextViews
        /*((TextView) findViewById(R.id.address)).setText(activity.address);
        ((TextView) findViewById(R.id.phone)).setText(activity.phone.equals("-") || activity.phone.trim().equals("") ? getString(R.string.no_phone_number) : activity.phone);
        ((TextView) findViewById(R.id.website)).setText(activity.website.equals("-") || activity.website.trim().equals("") ? getString(R.string.no_website) : activity.website);
        */
        // Zeige die Beschreibung des Ortes in einer WebView an
        description = (WebView) findViewById(R.id.description);
        String html_data = "<style>img{max-width:100%;height:auto;} iframe{width:100%;}</style> ";
        html_data += activity.description;
        description.getSettings().setBuiltInZoomControls(true);
        description.setBackgroundColor(Color.TRANSPARENT);
        description.setWebChromeClient(new WebChromeClient());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            description.loadDataWithBaseURL(null, html_data, "text/html; charset=UTF-8", "utf-8", null);
        } else {
            description.loadData(html_data, "text/html; charset=UTF-8", null);
        }
        description.getSettings().setJavaScriptEnabled(true);

        // Deaktiviere das Scrollen in der Beschreibungsansicht durch Berührung
        description.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

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

    /*
       Wird aufgerufen, wenn die Aktivität wieder aufgenommen wird. Hier wird loadPlaceData()
       aufgerufen, um die Daten des Ortes anzuzeigen.
     */
    @Override
    protected void onResume() {
        if (description != null) description.onResume();
        super.onResume();
    }

    /*
      Eine Methode, die aufgerufen wird, wenn auf bestimmte Layout-Elemente wie Adresse, Telefon
      oder Website geklickt wird, um die entsprechenden Aktionen auszuführen.
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

    /*
      Zeigt eine Bildergalerie des Ortes an, die aus Bildern besteht, die im Place-Objekt enthalten sind.
     */
    private void setImageGallery(List<String> images) {
        // add optional image into list
        List<String> new_images = new ArrayList<>();
        //new_images.add(new Images(place.place_id, place.image));
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

    // Öffnet die Bildergalerie mit dem angegebenen Startbild.
    private void openImageGallery(int position) {
        Intent openImageIntent = new Intent(ActivityPlaceDetail.this, ActivityFullScreenImage.class);
        openImageIntent.putExtra(ActivityFullScreenImage.EXTRA_POS, position);
        openImageIntent.putStringArrayListExtra(ActivityFullScreenImage.EXTRA_IMGS, new_images_str);
        startActivity(openImageIntent);
    }



    // Konfiguriert die Toolbar und CollapsingToolbarLayout und zeigt den Namen des Ortes an.
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

        // Füge einen OnOffsetChangedListener zur AppBarLayout hinzu
        ((AppBarLayout) findViewById(R.id.app_bar_layout)).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                // Überprüfe, ob die Höhe des CollapsingToolbarLayouts minus der vertikalen Verschiebung kleiner als das Doppelte der minimalen Höhe des CollapsingToolbarLayouts ist
                if (collapsing_toolbar.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(collapsing_toolbar)) {
                    // Zeige den FloatingActionButton, wenn der Bedingung erfüllt ist
                    fab.show();
                } else {
                    // Verberge den FloatingActionButton, wenn der Bedingung nicht erfüllt ist
                    fab.hide();
                }
            }
        });

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


    // Erstellt das Optionsmenü in der Aktionsleiste.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_details, menu);
        return true;
    }

    // Reagiert auf Klicks auf die Menüelemente,  das Teilen des Ortes oder zurücktaste.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            backAction();
            return true;
        } else if (id == R.id.action_share) {
            if (!activityModel.isDraft()) {
                Tools.methodShare(ActivityPlaceDetail.this, activityModel);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // Initialisiert die Google Map und konfiguriert die Kartenansicht.
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
                        // config map
                        googleMap = Tools.configStaticMap(ActivityPlaceDetail.this, googleMap, activityModel);
                    }
                }
            });
        }

        ((Button) findViewById(R.id.bt_navigate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(),"OPEN", Toast.LENGTH_LONG).show();
                Intent navigation = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=" + activityModel.location.latitude + "," + activityModel.location.longitude));
                startActivity(navigation);
            }
        });
        ((Button) findViewById(R.id.bt_view)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlaceInMap();
            }
        });
        ((LinearLayout) findViewById(R.id.map)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlaceInMap();
            }
        });
    }

    // Öffnet den ausgewählten Ort in der Google Maps-Anwendung.
    private void openPlaceInMap() {
        Intent openPlaceMap = new Intent(this, ActivityMaps.class);
        openPlaceMap.putExtra(ActivityMaps.EXTRA_OBJ, activityModel);
        startActivity(openPlaceMap);
    }

    /*
      Wird aufgerufen, wenn die Aktivität zerstört wird. Hier wird überprüft, ob der API-Aufruf
      noch ausgeführt wird und gegebenenfalls abgebrochen
     */
    @Override
    protected void onDestroy() {
        if (callback != null && callback.isExecuted()) callback.cancel();
        super.onDestroy();
    }

    /*
     Wird aufgerufen, wenn die Zurück-Taste des Geräts gedrückt wird. Hier wird festgelegt,
     wie die Aktivität beendet wird, basierend auf dem Wert von isFromNotif
     */
    @Override
    public void onBackPressed() {
        backAction();
    }

    /*
      Wird aufgerufen, wenn die Aktivität pausiert wird. Hier wird sichergestellt,
      dass die WebView pausiert wird, um Ressourcen zu sparen.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (description != null) description.onPause();
    }

    /*
      Eine Hilfsmethode, die festlegt, wie die Aktivität beendet wird, abhängig davon,
      ob sie von einer Benachrichtigung gestartet wurde oder nicht
     */
    private void backAction() {
        if (isFromNotif) {
            Intent openMainAcitvity = new Intent(this, ActivityMain.class);
            startActivity(openMainAcitvity);
        }
        finish();
    }


    /*
      Zeigt die Daten des Ortes mit einer leichten Verzögerung an,
      um die Benutzeroberfläche responsiver zu machen.
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



    // Zeigt die Snackbar-Nachricht periodisch erneut an, wenn sie noch nicht angezeigt wird
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

    // Zeigt oder verbirgt die Fortschrittsanzeige.
    private void showProgressbar(boolean show) {
        lyt_progress.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
