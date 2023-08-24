package app.thecity.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import app.thecity.AppConfig;
import app.thecity.R;
import app.thecity.data.DatabaseHandler;
import app.thecity.data.SharedPref;
import app.thecity.fragment.FragmentCategory;
import app.thecity.utils.PermissionUtil;
import app.thecity.utils.Tools;

// Hauptaktivität der App
public class ActivityMain extends AppCompatActivity {

    public ActionBar actionBar;
    public Toolbar toolbar;
    private int[] categories;
    private FloatingActionButton fab;
    private NavigationView navigationView;
    private DatabaseHandler db;
    private SharedPref sharedPref;
    private RelativeLayout nav_header_lyt;

    static ActivityMain activityMain;

    /*
     In dieser Methode wird die Aktivität initialisiert. Es werden die Toolbar, das Navigationsmenü
     und die Kategorien initialisiert. Die erste Kategorie (FragmentCategory) wird angezeigt,
     und ein Klick auf den FAB (Floating Action Button) öffnet die ActivitySearch.
     Es wird auch die Benachrichtigungsberechtigung überprüft und die Systemleiste für
     Lollipop-Geräte angepasst
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setze das Layout der Hauptaktivität
        setContentView(R.layout.activity_main);
        // Weise die Hauptaktivität dieser Variable zu
        activityMain = this;

        // Initialisiere die Schwebende Aktionsschaltfläche (Floating Action Button) HerzButton
        fab = (FloatingActionButton) findViewById(R.id.fab);
        // Initialisiere die Datenbank-Handler-Klasse
        db = new DatabaseHandler(this);
        // Initialisiere die gemeinsamen Einstellungen
        sharedPref = new SharedPref(this);

        // Initialisiere die Toolbar
        initToolbar();
        // Initialisiere das Navigationsmenü im Seitenmenü
        initDrawerMenu();
        // Lade die Kategorien-IDs aus den Ressourcen
        categories = getResources().getIntArray(R.array.id_category);

        // Wähle standardmäßig das erste Element im Seitenmenü aus
        onItemSelected(R.id.nav_all, getString(R.string.title_nav_all));

        // Füge einen Klicklistener zur Schwebenden Aktionsschaltfläche hinzu
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Öffne die Aktivität für die Suche
                Intent searchIntent = new Intent(ActivityMain.this, ActivitySearch.class);
                startActivity(searchIntent);
            }
        });

        // Überprüfe und beantrage gegebenenfalls Berechtigungen für Benachrichtigungen
        PermissionUtil.checkAndRequestNotification(this);

        // Konfiguriere die Systemleiste für Lollipop
        Tools.systemBarLolipop(this);

        // Konfiguriere die Rechts-nach-links-Schriftartunterstützung
        Tools.RTLMode(getWindow());

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    // Initialisiere die Toolbar
    private void initToolbar() {
        // Finde die Toolbar in der Layout-Datei und setze sie als Aktionsleiste
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Erhalte eine Referenz zur Aktionsleiste
        actionBar = getSupportActionBar();
        // Zeige den Zurück-Pfeil in der Aktionsleiste an
        actionBar.setDisplayHomeAsUpEnabled(true);
        // Aktiviere die Home-Button-Funktionalität der Aktionsleiste
        actionBar.setHomeButtonEnabled(true);
        // Setze die Farbe der Aktionsleiste
        Tools.setActionBarColor(this, actionBar);
    }


    /*
      In dieser Methode wird das Navigationsmenü (Drawer) initialisiert. Es werden Menüoptionen
      festgelegt und die Klickaktionen für verschiedene Menüpunkte werden definiert. Außerdem wird
      die Navigation Header-View angepasst und Klickaktionen für die Menüelemente "Einstellungen"
      und "Karte" festgelegt.
       */
    private void initDrawerMenu() {
        // Finde die Schublade (DrawerLayout) in der Layout-Datei
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Erstelle einen Toggle-Listener für die Schublade, um den Schubladenstatus zu überwachen
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                // Aktualisiere die Anzahl der Favoriten im Navigationselement, wenn die Schublade geöffnet wird
                updateFavoritesCounter(navigationView, R.id.nav_favorites, db.getFavoritesSize());
                super.onDrawerOpened(drawerView);
            }
        };
        // Setze den Toggle-Listener für die Schublade
        drawer.setDrawerListener(toggle);
        // Synchronisiere den Schubladenstatus mit dem Toggle-Status (z. B. zeige/hide den Hamburger-Button)
        toggle.syncState();

        // Finde das Navigationselement in der Layout-Datei und setze einen Listener für Navigationselement-Auswahlereignisse
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Verarbeite das ausgewählte Navigationselement
                return onItemSelected(item.getItemId(), item.getTitle().toString());
            }
        });

        // Entferne das Navigationsmenüelement "News Info", wenn es deaktiviert ist
        if (!AppConfig.general.enable_news_info) navigationView.getMenu().removeItem(R.id.nav_news);

        // Setze das Aussehen des Navigationskopfbereichs
        View nav_header = navigationView.getHeaderView(0);
        nav_header_lyt = (RelativeLayout) nav_header.findViewById(R.id.nav_header_lyt);
        nav_header_lyt.setBackgroundColor(Tools.colorBrighter(sharedPref.getThemeColorInt()));

        // Klick Listener Menükopf Einstellungen
        (nav_header.findViewById(R.id.menu_nav_setting)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Starte die Einstellungsaktivität bei Klick auf das Einstellungen-Element im Navigationskopf
                Intent openSettingsIntent = new Intent(getApplicationContext(), ActivitySetting.class);
                startActivity(openSettingsIntent);
            }
        });

        // Klick Listener Menükopf Karte
        (nav_header.findViewById(R.id.menu_nav_map)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Starte die Kartenaktivität bei Klick auf das Karten-Element im Navigationskopf
                Intent openMapIntent = new Intent(getApplicationContext(), ActivityMaps.class);
                startActivity(openMapIntent);
            }
        });
    }

    /*
    Wenn die Zurück-Taste gedrückt wird, wird das Navigationsmenü geöffnet, wenn es geschlossen ist.
    Wenn es bereits geöffnet ist, wird die doExitApp-Methode aufgerufen, um die App zu beenden.
     */
    @Override
    public void onBackPressed() {
        // Zuerst wird versucht, das Schubladensystem (DrawerLayout) in der Layout-Datei zu finden
        // draweelayout entspricht der navigationsleiste rechts mit den 3 balken
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (!drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
        } else {
            doExitApp();
        }
    }

    /*
    onCreateOptionsMenu und onOptionsItemSelected: Diese Methoden handhaben das Erstellen und
    Verarbeiten des Optionsmenüs der Toolbar, einschließlich Optionen wie "Einstellungen",
    "Bewerten" und "Über"
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent openSettingsIntentSideBar = new Intent(getApplicationContext(), ActivitySetting.class);
            startActivity(openSettingsIntentSideBar);
        } else if (id == R.id.action_about) {
            Tools.aboutAction(ActivityMain.this);
        }

        return super.onOptionsItemSelected(item);
    }

    /*
      Diese Methode wird aufgerufen, wenn ein Element im Navigationsmenü ausgewählt wird.
      Abhängig von der ausgewählten Kategorie wird das entsprechende Fragment (Kategorie) angezeigt.
       */
    public boolean onItemSelected(int id, String title) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        //sub menu
        /* IMPORTANT : cat[index_array], index is start from 0
         */
        if (id == R.id.nav_all) {
            //All Places
            fragment = new FragmentCategory();
            bundle.putInt(FragmentCategory.TAG_CATEGORY, -1);
            actionBar.setTitle(title);
        } else if (id == R.id.nav_favorites) {
            //Favourites
            fragment = new FragmentCategory();
            bundle.putInt(FragmentCategory.TAG_CATEGORY, -2);
            actionBar.setTitle(title);
        } else if (id == R.id.nav_news) {
            // News Info
            Intent openNewsInfosIntent = new Intent(this, ActivityNewsInfo.class);
            startActivity(openNewsInfosIntent);
        } else if (id == R.id.nav_ownplaces) {
            fragment = new FragmentCategory();
            bundle.putInt(FragmentCategory.TAG_CATEGORY, categories[10]);
            actionBar.setTitle(title);
        } else if (id == R.id.nav_calesthenics) {
            fragment = new FragmentCategory();
            bundle.putInt(FragmentCategory.TAG_CATEGORY, categories[0]);
            actionBar.setTitle(title);
        } else if (id == R.id.nav_parcouring) {
            fragment = new FragmentCategory();
            bundle.putInt(FragmentCategory.TAG_CATEGORY, categories[1]);
            actionBar.setTitle(title);
        } else if (id == R.id.nav_outdoor) {
            fragment = new FragmentCategory();
            bundle.putInt(FragmentCategory.TAG_CATEGORY, categories[2]);
            actionBar.setTitle(title);
        } else if (id == R.id.nav_outdoorGyms) {
            fragment = new FragmentCategory();
            bundle.putInt(FragmentCategory.TAG_CATEGORY, categories[3]);
            actionBar.setTitle(title);
        }

        if (fragment != null) {
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_content, fragment);
            fragmentTransaction.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
    Diese Methode wird verwendet, um die virtuelle Tastatur zu verstecken, wenn sie geöffnet ist.
     */
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private long exitTime = 0;

    /*
    Wenn die Zurück-Taste erneut gedrückt wird, wird diese Methode aufgerufen,um die App zu beenden.
    Wenn die Zurück-Taste innerhalb von 2 Sekunden erneut gedrückt wird, wird die App beendet.
     */
    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    /*
    Diese Methoden werden verwendet, um die App-Oberfläche und die Farbgebung der ActionBar
    bei Bedarf zu aktualisieren.
     */
    @Override
    protected void onResume() {
        updateFavoritesCounter(navigationView, R.id.nav_favorites, db.getFavoritesSize());
        if (actionBar != null) {
            Tools.setActionBarColor(this, actionBar);
            // for system bar in lollipop
            Tools.systemBarLolipop(this);
        }
        if (nav_header_lyt != null) {
            nav_header_lyt.setBackgroundColor(Tools.colorBrighter(sharedPref.getThemeColorInt()));
        }
        super.onResume();
    }

    static boolean active = false;
    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        active = false;
    }

    /*
    Diese Methode wird verwendet, um die Anzahl der Favoriten im Navigationsmenü anzuzeigen.
     */
    private void updateFavoritesCounter(NavigationView nav, @IdRes int itemId, int count) {
        TextView view = (TextView) nav.getMenu().findItem(itemId).getActionView().findViewById(R.id.counter);
        view.setText(String.valueOf(count));
    }



    /*
    Diese beiden Methoden sind statische Methoden, um eine Instanz der ActivityMain zu erhalten und
    die Animation des Floating Action Buttons zu steuern.
     */
    public static ActivityMain getInstance() {
        return activityMain;
    }

    public static void animateFab(final boolean hide) {
        FloatingActionButton f_ab = (FloatingActionButton) activityMain.findViewById(R.id.fab);
        int moveY = hide ? (2 * f_ab.getHeight()) : 0;
        f_ab.animate().translationY(moveY).setStartDelay(100).setDuration(400).start();
    }
}
