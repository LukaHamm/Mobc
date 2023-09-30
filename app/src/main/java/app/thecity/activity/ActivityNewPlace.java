package app.thecity.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import app.thecity.R;
import app.thecity.data.SharedPref;
import app.thecity.databinding.ActivityNewPlaceBinding;
import app.thecity.utils.ActivityType;
import app.thecity.utils.Tools;

public class ActivityNewPlace extends AppCompatActivity {

    public ActionBar actionBar;
    private ActivityNewPlaceBinding binding;
    private static final int SELECT_ADDRESS_REQUEST = 2; // Anpassen Sie die Anforderungsnummer

    private EditText addressEditText;
    private ImageView selectedImageView;
    private EditText descriptionEditText;
    private Button selectImageButton;
    private Button save_new_place_button;
    private ActivityType selectedCategoryType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewPlaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initToolbar(); // Initialisiere die Toolbar

        // Initialisieren Sie die UI-Elemente
        selectedImageView = findViewById(R.id.selectedImageView);
        descriptionEditText = findViewById(R.id.description);
        selectImageButton = findViewById(R.id.selectImageButton);
        save_new_place_button = findViewById(R.id.safe_new_place_button);
        addressEditText = findViewById(R.id.address);

        CollapsingToolbarLayout collapsing_toolbar = findViewById(R.id.collapsing_toolbar);
        collapsing_toolbar.setContentScrimColor(new SharedPref(this).getThemeColorInt());

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Initialisieren der ausgewählten Kategorie mit "all" als Standardwert
        selectedCategoryType = ActivityType.all;

        // Initialisieren des Spinners
        Spinner spinner = findViewById(R.id.dropdown_menu);

        // Erstellen eines ArrayAdapter für den Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.dropdown_items, android.R.layout.simple_spinner_item);

        // Legen Sie das Layout für den Spinner fest
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Setzen Sie den Adapter auf den Spinner
        spinner.setAdapter(adapter);

        // Hinzufügen eines OnItemSelectedListener, um Änderungen im Spinner zu verfolgen
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // Hier wird der ausgewählte Wert des Spinners abgerufen
                String selectedItem = adapterView.getItemAtPosition(position).toString();

                // Verwende den ausgewählten Spinner-Wert, um den Kategorie-Typ zu setzen
                if (selectedItem.equals("Calisthenics")) {
                    selectedCategoryType = ActivityType.calisthenics;
                } else if (selectedItem.equals("Parkouring")) {
                    selectedCategoryType = ActivityType.parkour;
                } else if (selectedItem.equals("Outdoor")) {
                    selectedCategoryType = ActivityType.outdoor;
                } else if (selectedItem.equals("Outdoor-Gyms")) {
                    selectedCategoryType = ActivityType.outdoor_gym;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Implementiere diese Methode, um sicherzustellen, dass der Benutzer eine Kategorie auswählt.
                Toast.makeText(ActivityNewPlace.this, "Bitte wählen Sie eine Kategorie aus.", Toast.LENGTH_SHORT).show();
            }
        });



        // OnClickListener für den "Adresse auswählen" Button
        findViewById(R.id.adress_image).setOnClickListener(view -> openGoogleMapsForAddressSelection());

        save_new_place_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hier können Sie den String mit der angelegten Aktivität ausgeben
                String activityInfo = generateActivityInfo();
                Toast.makeText(ActivityNewPlace.this, activityInfo, Toast.LENGTH_SHORT).show();

                // Zur Hauptaktivität zurückkehren
                Intent intent = new Intent(ActivityNewPlace.this, ActivityMain.class);
                startActivity(intent);

                // Beenden Sie diese Aktivität
                finish();
            }
        });
    }



    // Methode zum Öffnen der Google Maps-App zur Adressauswahl
    private void openGoogleMapsForAddressSelection() {
        // Starten Sie die Google Maps-App zur Auswahl einer Adresse
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivityForResult(mapIntent, SELECT_ADDRESS_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_ADDRESS_REQUEST && resultCode == RESULT_OK) {
            // Die ausgewählte Adresse aus dem Intent extrahieren
            String selectedAddress = data.getDataString();

            // Die ausgewählte Adresse in das EditText-Feld für die Adresse einfügen
            addressEditText.setText(selectedAddress);
        }
    }

    private String generateActivityInfo() {
        // Hier können Sie den gewünschten String mit den Aktivitätsinformationen generieren
        // basierend auf den Benutzereingaben und der ausgewählten Kategorie
        String activityInfo = "Aktivität: " + selectedCategoryType + "\n";
        activityInfo += "Adresse: " + addressEditText.getText().toString() + "\n";
        activityInfo += "Beschreibung: " + descriptionEditText.getText().toString() + "\n";
        return activityInfo;
    }

    // Initialisiert die Toolbar (App-Aktionenleiste) oben auf der Aktivität.
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(R.string.title_nav_news);
        Tools.systemBarLolipop(this);
    }

    // Wird aufgerufen, wenn die Aktivität wieder aufgenommen wird. Es aktualisiert die
    // Aktionenleistenfarbe und die Systemleiste (nur für Android Lollipop und höher).
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
