package app.thecity.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

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

import java.io.Serializable;

import app.thecity.R;
import app.thecity.data.SharedPref;
import app.thecity.fragment.FragmentCategory;
import app.thecity.model.User;
import app.thecity.utils.PermissionUtil;
import app.thecity.utils.Tools;
/**
 * Diese Klasse stellt die Hauptaktivität der App dar.
 * Sie enthält das Hauptmenü und ermöglicht die Navigation zu verschiedenen Funktionen der App.
 */
public class ActivityMain extends AppCompatActivity {

    public ActionBar actionBar;
    public Toolbar toolbar;
    private int[] categories;
    private static boolean active = false;

    private FloatingActionButton newPlace;
    private NavigationView navigationView;
    private SharedPref sharedPref;
    private RelativeLayout navHeaderLayout;
    private static ActivityMain menueActivityMain;
    private Fragment fragment;

    /**
     * Initialisiert die Aktivität, setzt das Layout und führt notwendige Initialisierungen durch.
     * @param savedInstanceState Das Bundle-Objekt, das den Zustand der Aktivität enthält.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        menueActivityMain = this;

        newPlace = findViewById(R.id.newPlace);

        sharedPref = new SharedPref(this);

        initToolbar();
        initDrawerMenu();

        categories = getResources().getIntArray(R.array.id_category);
        onItemSelected(R.id.nav_all, getString(R.string.title_nav_all));

        newPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newPlaceIntent = new Intent(ActivityMain.this, ActivityNewPlace.class);
                startActivity(newPlaceIntent);
            }
        });

        PermissionUtil.checkAndRequestNotification(this);
        Tools.systemBarLolipop(this);
        Tools.RTLMode(getWindow());

        User user = Tools.readuser(getApplicationContext());

        if (user == null || user.token == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        // Finde das Navigationselement in der Layout-Datei und setze einen Listener für Navigationselement-Auswahlereignisse
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                return onItemSelected(item.getItemId(), item.getTitle().toString());
            }
        });

        // Setze das Aussehen des Navigationskopfbereichs
        View nav_header = navigationView.getHeaderView(0);
        navHeaderLayout = nav_header.findViewById(R.id.nav_header_lyt);
    }

    /**
     * Initialisiert die Toolbar und setzt sie als ActionBar.
     */
    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        Tools.setActionBarColor(this, actionBar);
    }

    /**
     * Initialisiert das Navigationsmenü (Drawer) der App.
     */
    private void initDrawerMenu() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent openSettingsIntentSideBar = new Intent(getApplicationContext(), ActivitySetting.class);
            startActivity(openSettingsIntentSideBar);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Behandelt die Auswahl eines Elements im Navigationsmenü.
     *
     * @param id    Die ID des ausgewählten Elements.
     * @param title Der Titel des ausgewählten Elements.
     * @return true, wenn die Auswahl erfolgreich verarbeitet wurde, andernfalls false.
     */
    public boolean onItemSelected(int id, String title) {
        Bundle bundle = new Bundle();
        if (id == R.id.profile) {
            Intent openNewsInfosIntent = new Intent(this, ProfileActivity.class);
            startActivity(openNewsInfosIntent);
        } else if (id == R.id.maps_menue) {
            Intent openMapIntent = new Intent(getApplicationContext(), ActivityMaps.class);
            if (((FragmentCategory) fragment).getActivityList() != null) {
                openMapIntent.putExtra("activityList", (Serializable) ((FragmentCategory) fragment).getActivityList());
            }
            startActivity(openMapIntent);
        } else if (id == R.id.nav_all) {
            fragment = new FragmentCategory();
            bundle.putInt(FragmentCategory.TAG_CATEGORY, -1);
            actionBar.setTitle(title);
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Versteckt die virtuelle Tastatur, wenn sie geöffnet ist.
     */
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private long exitTime = 0;

    /**
     * Beendet die App, wenn die Zurück-Taste erneut innerhalb von 2 Sekunden gedrückt wird.
     * Ansonsten wird eine Benachrichtigung angezeigt.
     */
    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (actionBar != null) {
            Tools.setActionBarColor(this, actionBar);
            Tools.systemBarLolipop(this);
        }

        if (navHeaderLayout != null) {
            navHeaderLayout.setBackgroundColor(Tools.colorBrighter(sharedPref.getThemeColorInt()));
        }
    }


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

    /**
     * Gibt eine Instanz der ActivityMain zurück.
     *
     * @return Die Instanz der ActivityMain.
     */
    public static ActivityMain getInstance() {
        return menueActivityMain;
    }

    /**
     * Animiert den Floating Action Button (FAB) für die Erstellung eines neuen Ortes.
     *
     * @param hide true, wenn der FAB ausgeblendet werden soll, andernfalls false.
     */
    public static void animateNewplace(final boolean hide) {
        FloatingActionButton f_ab = menueActivityMain.findViewById(R.id.newPlace);
        int moveY = hide ? (2 * f_ab.getHeight()) : 0;
        f_ab.animate().translationY(moveY).setStartDelay(100).setDuration(400).start();
    }
}
