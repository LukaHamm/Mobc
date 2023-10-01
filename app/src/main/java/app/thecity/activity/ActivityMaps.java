package app.thecity.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.thecity.AppConfig;
import app.thecity.R;
import app.thecity.connection.RestAdapter;
import app.thecity.data.Constant;
import app.thecity.model.Activity;
import app.thecity.model.Category;
import app.thecity.utils.ActivityType;
import app.thecity.utils.PermissionUtil;
import app.thecity.utils.Tools;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Diese Klasse stellt die Aktivität für die Kartenansicht dar.
 * Sie zeigt Orte (Places) auf einer Karte an und ermöglicht die Navigation zu den Details der Orte.
 */
public class ActivityMaps extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRA_OBJ = "key.EXTRA_OBJ";
    private GoogleMap mMap;
    private Toolbar toolbar;
    private ActionBar actionBar;

    private ClusterManager<Activity> mClusterManager;
    private View parent_view;
    private int cat[];
    private PlaceMarkerRenderer placeMarkerRenderer;
    private Activity ext_Activity = null;
    private boolean isSinglePlace;
    HashMap<String, Activity> hashMapActivity = new HashMap<>();
    private HashMap<String, Marker> markerPlaces = new HashMap<>();
    private HashMap<String, Integer> placesPosition = new HashMap<>();
    private List<Activity> items = new ArrayList<>();
    private List<Activity> fetchedActivities = new ArrayList<>();
    private int cat_id = -1;
    private Category cur_category;
    private ImageView icon, marker_bg;
    private View marker_view;
    private boolean showSlider = true;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;

    /**
     * Initialisiert die Ansicht, die Karte und ruft einige Hilfsmethoden auf,
     * um die ClusterManager-und ViewPager-Funktionen zu initialisieren.
     *
     * @param savedInstanceState Die gespeicherten Daten, falls die Aktivität wiederhergestellt wird.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        parent_view = findViewById(android.R.id.content);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        marker_view = inflater.inflate(R.layout.maps_marker, null);
        icon = (ImageView) marker_view.findViewById(R.id.marker_icon);
        marker_bg = (ImageView) marker_view.findViewById(R.id.marker_bg);
        viewPager = findViewById(R.id.view_pager);

        ext_Activity = (Activity) getIntent().getSerializableExtra(EXTRA_OBJ);
        isSinglePlace = (ext_Activity != null);

        initMapFragment();
        initToolbar();

        cat = getResources().getIntArray(R.array.id_category);
        if (!isSinglePlace) {
            this.fetchedActivities = (List<Activity>) getIntent().getSerializableExtra("activityList");
        }
        // for system bar in lollipop
        Tools.systemBarLolipop(this);
        Tools.RTLMode(getWindow());
    }

    /**
     * Diese Methode wird aufgerufen, wenn die Google Maps-Karte bereit ist. Sie initialisiert die
     * Karte, lädt Orte (Places) entweder für alle Orte der APP oder nur einen einzelnen Ort
     * Das Flag isSinglePlace wird in Abhängigkeit davon gesetzt, ob der Aktivität ein Ort übergeben wurde
     * (Ausruf aus ActivitiyPlace Detail) oder eine Liste (Aufruf vom MainActivity)
     * und initialisiert den ClusterManager für die Marker-Cluster-Funktionalität.
     *
     * @param googleMap Die Google Map-Instanz, die bereit ist.
     */
    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = Tools.configActivityMaps(googleMap);
        CameraUpdate location;
        if (isSinglePlace) {
            marker_bg.setColorFilter(getResources().getColor(R.color.marker_secondary));
            MarkerOptions markerOptions = new MarkerOptions().title(ext_Activity.title).position(ext_Activity.getPosition());
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(Tools.createBitmapFromView(ActivityMaps.this, marker_view)));
            mMap.addMarker(markerOptions);
            location = CameraUpdateFactory.newLatLngZoom(ext_Activity.getPosition(), 12);
            actionBar.setTitle(ext_Activity.title);

            loadClusterManager(new ArrayList<>());
        } else {

            location = CameraUpdateFactory.newLatLngZoom(new LatLng(AppConfig.general.city_lat, AppConfig.general.city_lng), 9);

            mClusterManager = new ClusterManager<>(this, this.mMap);
            placeMarkerRenderer = new PlaceMarkerRenderer(this, this.mMap, mClusterManager);
            initClusterWithSlider();
            mClusterManager.setRenderer(placeMarkerRenderer);
            this.mMap.setOnCameraIdleListener(mClusterManager);

            loadClusterManager(fetchedActivities);

        }
        mMap.animateCamera(location);
        mMap.setOnInfoWindowClickListener(marker -> {
            Activity activity;
            if (hashMapActivity.get(marker.getId()) != null) {
                activity = (Activity) hashMapActivity.get(marker.getId());
            } else {
                activity = ext_Activity;
            }
            ActivityPlaceDetail.navigate(ActivityMaps.this, parent_view, activity);
        });

        mMap.setOnMapClickListener(latLng -> toggleViewPager(!showSlider));

        showMyLocation();
        initViewPager();
    }

    /**
     * In dieser Methode wird der Slider initialisiert, um zwischen den Orten hin und her zu wischen
     * Dies wird mittels eines Viepagers gemacht
     */
    private void initClusterWithSlider() {
        mClusterManager.setOnClusterItemClickListener(item -> {
            Integer position = placesPosition.get(item.title + "");
            if (position != null) {
                viewPager.setCurrentItem(position, true);
            }
            return false;
        });
        mClusterManager.setOnClusterItemInfoWindowClickListener(item -> ActivityPlaceDetail.navigate(ActivityMaps.this, parent_view, item));
    }

    /**
      * Hier wird die Berechtigung zur Abfrage des Standortes überprüft
     *  Ist dies der Fall wird ein Button initialisert der bei Klick den eigenen Standort setzt
     **/
    @SuppressLint("MissingPermission")
    private void showMyLocation() {
        if (PermissionUtil.isLocationGranted(this)) {
            // Enable / Disable my location button
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    try {
                        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            showAlertDialogGps();
                        } else {
                            Location loc = Tools.getLastKnownLocation(ActivityMaps.this);
                            CameraUpdate myCam = CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 12);
                            mMap.animateCamera(myCam);
                        }
                    } catch (Exception e) {
                    }
                    return true;
                }
            });
        }
    }

    /**
      * Lädt die Liste der Orte in den ClusterManager und zeigt die Marker auf der Karte an.
     *  Dies wird nur im Falle des Aufrufs der Aktivität aus dem Hauptmenü initialisiert
     **/
    private void loadClusterManager(List<Activity> items) {
        this.items = new ArrayList<>();
        placesPosition.clear();
        if (ext_Activity != null) {
            placesPosition.put(ext_Activity.title + "", 0);
            this.items.add(ext_Activity);
        }
        int index = 0, last_size = placesPosition.size();
        for (Activity activity : items) {
            this.items.add(activity);
            placesPosition.put(activity.title + "", last_size + index);
            index++;
        }
        if (mClusterManager != null) {
            mClusterManager.clearItems();
            mClusterManager.cluster();
            mClusterManager.addItems(this.items);
        }
    }

    /**
     * Initialisiert die Toolbar (App-Aktionsleiste) oben auf der Aktivität.
      */

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(R.string.activity_title_maps);
        Tools.setActionBarColor(this, actionBar);
    }

    /**
     * Initialisiert das SupportMapFragment, um die Kartenansicht zu erhalten.
     */
    private void initMapFragment() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Klasse um ein Activity-Objekt in einen Marke auf der Karte umzuwandeln
     * die Klasse Activity muss das Interface ClusterItem implementieren und die Methoden implementieren
     */
    private class PlaceMarkerRenderer extends DefaultClusterRenderer<Activity> {
        public PlaceMarkerRenderer(Context context, GoogleMap map, ClusterManager<Activity> clusterManager) {
            super(context, map, clusterManager);
        }

        /**
         * Die Methode initialisiert die Daten die in dem Viepager angezeigt werden soll (Titel, Bild, etc..) und setzt Marker
         * @param item Der Ort der dargestellt werden soll
         * @param markerOptions Der Marker der die Position des Ortes auf der Karte präsentieren soll
         */
        @Override
        protected void onBeforeClusterItemRendered(Activity item, MarkerOptions markerOptions) {
            if (cat_id == -1) { // all place
                icon.setImageResource(R.drawable.round_shape);
            } else {
                icon.setImageResource(cur_category.icon);
            }
            marker_bg.setColorFilter(getResources().getColor(R.color.marker_primary));
            markerOptions.title(item.title);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(Tools.createBitmapFromView(ActivityMaps.this, marker_view)));
            if (ext_Activity != null && ext_Activity.title == item.title) {
                markerOptions.visible(false);
            }
        }

        /**
         *
         * Die Methode verknüpft Marker und Ort mittels Hashmap
         * @param item Der Ort der zur Karte hinzugefügt wurde
         * @param marker Der Marker der zum Ort gehört
         */
        @Override
        protected void onClusterItemRendered(Activity item, Marker marker) {
            hashMapActivity.put(marker.getId(), item);
            markerPlaces.put(item.title + "", marker);
            super.onClusterItemRendered(item, marker);
        }
    }

    /**
      Zeigt einen AlertDialog an, wenn das GPS des Geräts ausgeschaltet ist und ermöglicht
      dem Benutzer, es einzuschalten.
     */
    private void showAlertDialogGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_content_gps);
        builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton(R.string.NO, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Initialisiert den ViewPager, um eine horizontale Ansicht der Orte anzuzeigen.
     */
    private void initViewPager() {
        mMap.setPadding(0, 0, 0, Tools.dpToPx(this, 140));
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);

        viewPager.setClipToPadding(false);
        viewPager.setPadding(Tools.dpToPx(this, 20), 0, Tools.dpToPx(this, 20), 0);
        viewPager.setPageMargin(Tools.dpToPx(this, -6));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mMap.stopAnimation();
                CameraUpdate location = CameraUpdateFactory.newLatLngZoom(items.get(position).getPosition(), 16);
                mMap.animateCamera(location, new GoogleMap.CancelableCallback() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onFinish() {
                        Marker marker = markerPlaces.get(items.get(position).title + "");
                        if (marker != null && !marker.isInfoWindowShown()) {
                            marker.showInfoWindow();
                        } else {
                            new Handler().postDelayed(() -> {
                                Marker marker_ = markerPlaces.get(items.get(position).title + "");
                                if (marker_ != null && !marker_.isInfoWindowShown()) {
                                    marker_.showInfoWindow();
                                }
                            }, 1000);
                        }
                    }
                });
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    // Animiert das Ein- und Ausblenden des ViewPagers.
    private void toggleViewPager(boolean show) {
        showSlider = show;
        float heightMax = viewPager.getHeight();
        float start = show ? heightMax : 0, end = show ? 0 : heightMax;
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(viewPager, "translationY", start, end);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mMap.setPadding(0, 0, 0, show ? Tools.dpToPx(ActivityMaps.this, 140) : 0);
            }
        });
        objectAnimator.start();
    }

    /**
     * Diese innere Klasse erbt von der Klasse PagerAdapter und ermöglicht das Blättern zwischen den Orten auf der Karte
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;


        public MyViewPagerAdapter() {
        }

        /**
         * Diese Klasse bläst das Layout für die einzelenen Seiten innerhalb der Viewpager-Komponente, die inder layout.xml der Aktivität definiert ist
         * auf.
         * Es wird eine Imageview für das Bild eines Ortes sowie eine Textview für Titel und Adresse des Ortes initialisiert
         * Anhand der derzeitigen Seitenposition können die Daten aus der Liste (items) der Orte geladen und gesetzt werden
         * @param container The containing View in which the page will be shown.
         * @param position The page position to be instantiated.
         * @return
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Activity activity = items.get(position);
            View view = layoutInflater.inflate(R.layout.item_place_slider, container, false);
            ((TextView) view.findViewById(R.id.title)).setText(activity.title);
            ImageView image = view.findViewById(R.id.image);
            View lyt_address = view.findViewById(R.id.lyt_address);
            View lyt_distance = view.findViewById(R.id.lyt_distance);
            TextView address = view.findViewById(R.id.address);
            if (activity.images != null && !activity.images.isEmpty()) {
                Tools.displayImageThumb(ActivityMaps.this, image, Constant.getURLimgActivity(activity.images.get(0)), 0.5f);
            }
            //address.setText(activity.address);

            if (activity.distance == -1) {
                lyt_distance.setVisibility(View.GONE);
            } else {
                lyt_distance.setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.distance)).setText(Tools.getFormatedDistance(activity.distance));
            }

            view.findViewById(R.id.lyt_parent).setOnClickListener(v ->
                    ActivityPlaceDetail.navigate(ActivityMaps.this, image, activity)
            );
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
