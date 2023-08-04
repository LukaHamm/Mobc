package app.thecity.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import app.thecity.AppConfig;
import app.thecity.R;
import app.thecity.data.SharedPref;
import app.thecity.data.ThisApplication;
import app.thecity.utils.PermissionUtil;
import app.thecity.utils.Tools;

/*
  Die Klasse ActivitySplash ist eine Android-Aktivität, die als Startbildschirm (Splash-Screen)
  der App fungiert und gleichzeitig einige initialisierende Aufgaben ausführt,
  bevor die Hauptaktivität (ActivityMain) gestartet wird.
 */
public class ActivitySplash extends AppCompatActivity {

    private SharedPref sharedPref;
    private View parent_view;

    /*
       Diese Methode wird aufgerufen, wenn die Aktivität erstellt wird.
       Sie initialisiert die Ansichtskomponenten, zeigt den Splash-Screen an und führt die
       Berechtigungsprüfung durch.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        parent_view = findViewById(R.id.parent_view);

        sharedPref = new SharedPref(this);
        parent_view.setBackgroundColor(sharedPref.getThemeColorInt());

        // permission checker for android M or higher
        if (Tools.needRequestPermission()) {
            String[] permission = PermissionUtil.getDeniedPermission(this);
            if (permission.length != 0) {
                requestPermissions(permission, 200);
            } else {
                requestRemoteConfig();
            }
        } else {
            requestRemoteConfig();
        }

        // for system bar in lollipop
        Tools.systemBarLolipop(this);

        Tools.RTLMode(getWindow());
    }

    /*
      Überprüft, ob Remote Config verwendet wird und ob eine Internetverbindung besteht. Falls ja,
      werden die Remote Config-Werte abgerufen und aktiviert. Andernfalls werden die Einstellungen
      aus den gespeicherten Präferenzen verwendet
     */
    private void requestRemoteConfig() {
        if (!AppConfig.USE_REMOTE_CONFIG || !Tools.cekConnection(this)) {
            AppConfig.setFromSharedPreference();
            startActivityMainDelay(false);
            return;
        }
        Log.d("REMOTE_CONFIG", "requestRemoteConfig");
        FirebaseRemoteConfig firebaseRemoteConfig = ThisApplication.getInstance().getFirebaseRemoteConfig();
        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("REMOTE_CONFIG", "SUCCESS");
                AppConfig.setFromRemoteConfig(firebaseRemoteConfig);
                startActivityMainDelay(true);
            } else {
                Log.d("REMOTE_CONFIG", "FAILED");
                AppConfig.setFromSharedPreference();
                startActivityMainDelay(true);
            }
        });
    }

    /*
       Verzögert den Start der Hauptaktivität (ActivityMain) um einen bestimmten Zeitraum,
       abhängig von der übergebenen Geschwindigkeit (fast). Dies wird verwendet,
       um den Splash-Screen für eine gewisse Zeit anzuzeigen, bevor die Hauptaktivität gestartet wird
     */
    private void startActivityMainDelay(boolean fast) {
        new Handler(this.getMainLooper()).postDelayed(() -> nextActivity(), fast ? 1000 : 2000);
    }

    // Startet die Hauptaktivität (ActivityMain) und beendet die aktuelle Aktivität (ActivitySplash).
    private void nextActivity() {
        Intent i = new Intent(ActivitySplash.this, ActivityMain.class);
        startActivity(i);
        finish(); // kill current activity
    }


    /*
      Wird aufgerufen, nachdem der Benutzer auf die Berechtigungsanfrage reagiert hat.
      Es überprüft, ob der Benutzer die Berechtigung verweigert und entscheidet,
      ob die Remote Config abgerufen werden soll oder nicht
     */
    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 200) {
            for (String perm : permissions) {
                boolean rationale = shouldShowRequestPermissionRationale(perm);
                sharedPref.setNeverAskAgain(perm, !rationale);
            }
            requestRemoteConfig();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
