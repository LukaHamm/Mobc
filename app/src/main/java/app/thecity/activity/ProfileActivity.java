package app.thecity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import app.thecity.R;
import app.thecity.model.User;
import app.thecity.utils.Tools;
/**
 * Benutzerprofilansicht
 *
 * @author Niklas Tarkel
 */

public class ProfileActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private TextView usernameTextView;
    private TextView useremailTextView;

    private Button logoutButton;
    /**
     * - setzt das passende Profil Layout
     * - initialisiert die Toolbar über Funktionsaufruf
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean userdeleted = Tools.deleteUser(getApplicationContext());
                if (userdeleted){
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        initToolbar();
        set_User_Profile();

    }

    private void set_User_Profile(){
        //Finde die benötigten Textviews
        usernameTextView = findViewById(R.id.profile_name);
        useremailTextView = findViewById(R.id.profile_email);
        // Hier solltest du den Benutzernamen aus deinen Benutzerdaten abrufen und setzen
        User user = Tools.readuser(getApplicationContext());
        String username = user.getName(); // Ersetze getUserUsername() durch die Methode, um den Benutzernamen abzurufen
        String useremail = user.getEmail();
        usernameTextView.setText(username);
        useremailTextView.setText(useremail);
        Log.d("MeineActivity", "Benutzername: " + username);
    }


    // Initialisiert die Toolbar (App-Aktionenleiste) oben auf der Aktivität.
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(R.string.profile);
        Tools.systemBarLolipop(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /*
  Wird aufgerufen, wenn die Aktivität wieder aufgenommen wird. Es aktualisiert die
  Aktionenleistenfarbe und die Systemleiste (nur für Android Lollipop und höher).
 */
    @Override
    protected void onResume() {
        if (actionBar != null) {
            Tools.setActionBarColor(this, actionBar);
            // for system bar in lollipop
            Tools.systemBarLolipop(this);
        }
        super.onResume();
    }
}
