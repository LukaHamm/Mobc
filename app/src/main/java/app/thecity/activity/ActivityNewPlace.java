package app.thecity.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import app.thecity.R;
import app.thecity.adapter.AdapterImageView;
import app.thecity.connection.RestAdapter;
import app.thecity.model.Activity;
import app.thecity.model.Location;
import app.thecity.model.User;
import app.thecity.utils.ActivityType;
import app.thecity.utils.Tools;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * Diese Klasse repräsentiert die Aktivität zum Hinzufügen eines neuen Ortes.
 * Benutzer können Informationen über den Ort, wie Titel, Adresse, Beschreibung und Bilder, eingeben
 * und den Ort speichern.
 */
public class ActivityNewPlace extends AppCompatActivity {

    // ActionBar für die Aktivität
    public ActionBar actionBar;

    // Anfragecodes für die Bildauswahl und die Berechtigungsanfrage
    private static final int SELECT_IMAGE_REQUEST = 3;
    private static final int PERMISSION_REQUEST_CODE = 4;

    // UI-Elemente
    private EditText addressEditText;
    private EditText descriptionEditText;
    private RecyclerView recyclerView;
    private AdapterImageView adapterImageView;
    private EditText titleEditText;
    private Button selectImageButton;
    private Button saveNewPlaceButton;
    private ActivityType selectedCategoryType;

    /**
     * Diese Methode wird aufgerufen, wenn die Aktivität erstellt wird.
     * Hier werden die UI-Elemente initialisiert und die Toolbar konfiguriert.
     *
     * @param savedInstanceState Ein Bundle-Objekt, das den Zustand der Aktivität enthält.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_place);
        initToolbar();
        init_UI_Elements();
    }

    /**
     * Initialisiert die UI-Elemente der Aktivität, darunter RecyclerView, Buttons, EditTexts und Spinner.
     * Legt auch Klickereignisse für Buttons und Spinner fest.
     */
    private void init_UI_Elements() {
        // Initialisieren der RecyclerView für Bilder
        adapterImageView = new AdapterImageView(getApplicationContext(), new ArrayList<>());
        recyclerView = findViewById(R.id.images_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapterImageView);

        // Initialisieren anderer UI-Elemente
        descriptionEditText = findViewById(R.id.description);
        selectImageButton = findViewById(R.id.selectImageButton);
        saveNewPlaceButton = findViewById(R.id.safe_new_place_button);
        addressEditText = findViewById(R.id.address);
        titleEditText = findViewById(R.id.title);

        // Spinner für die Auswahl der Aktivitätskategorie
        Spinner spinner = findViewById(R.id.dropdown_menu);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.dropdown_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        /**
         * Clicklistener für das Dropdown-Menü zum Setzen der Kategorie
         */
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                evaluateCategorySelection(adapterView.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(ActivityNewPlace.this, "Bitte wählen Sie eine Kategorie aus.", Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * Hier wird der Clicklistener für das Auswählen einer neuen Aktivität initialisiert
         * Es wird die Aktivität für das Öffnen der Bildergalerie aufgerufen
         */
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                        return;
                    }
                }

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_IMAGE_REQUEST);
            }
        });

        /**
         * Hier wird ein ClickListener für das Anlegen einer neuen Aktivität eingebaut
         */
        saveNewPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postActivity();
            }
        });
    }

    /**
     * Der User kann über das Dropdown die entsprechende Aktivität setzen abhängig vom übergebenen String wird das Enum ActivityType gesetzt.
     *
     * @param selectedItem Der ausgewählte Eintrag im Dropdown-Menü.
     */
    private void evaluateCategorySelection(String selectedItem) {
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

    /**
     * Initialisiert die Toolbar (App-Aktionenleiste) oben auf der Aktivität.
     */
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(R.string.newPlace_Titel);
        Tools.setActionBarColor(this, actionBar);
        Tools.systemBarLolipop(this);
    }

    /**
     * Sendet die erstellte Aktivität an den Server
     * Die Bilder werden im Erfolgsfall mittels der Methode uploadImage übertragen
     */
    private void postActivity() {
        User user = Tools.readuser(getApplicationContext());
        String header = "bearer " + user.token;
        LatLng curLocation = Tools.getCurLocation(getApplicationContext());
        Location location = new Location(curLocation.latitude, curLocation.longitude, "");
        Activity activity = new Activity(titleEditText.getText().toString(), selectedCategoryType.name().toString(), null, descriptionEditText.getText().toString(), null, location, addressEditText.getText().toString(), null);

        Call<Activity> callPostActivity = RestAdapter.createMobcApi().postActivity(header, activity);
        callPostActivity.enqueue(new Callback<Activity>() {
            @Override
            public void onResponse(Call<Activity> call, Response<Activity> response) {
                Activity activityRes = response.body();
                if (activityRes != null) {
                    String id = activityRes._id;
                    for (int i = 0; i < adapterImageView.getItemCount(); i++) {
                        try {
                            uploadImage(activityRes, adapterImageView.getImageList().get(i), i);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ActivityNewPlace.this, "Fehler beim Hochladen eines Bildes.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    Intent intent = new Intent(ActivityNewPlace.this, ActivityMain.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Activity> call, Throwable t) {
                Log.e("onFailure", t.getMessage());
            }
        });
    }

    /**
     * Lädt ein Bild auf den Server hoch und fügt es zur Aktivität hinzu.
     *
     * @param activity Die Aktivität, zu der das Bild hinzugefügt wird.
     * @param bitmap   Das zu hochladende Bild.
     * @param count    Die Reihenfolge des Bilds im Adapter.
     */
    private void uploadImage(Activity activity, Bitmap bitmap, int count) {
        File file = new File(getCacheDir(), "image" + count + ".jpg");
        User user = Tools.readuser(getApplicationContext());
        String header = "bearer " + user.token;

        try {
            OutputStream os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestBody);

        Call<ResponseBody> call = RestAdapter.createMobcApi().uploadImage(imagePart, activity._id, header);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Erfolgreich hochgeladen
                    // Hier können Sie die Antwort verarbeiten
                } else {
                    // Fehler beim Hochladen des Bildes
                    // Hier können Sie den Fehler verarbeiten
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Netzwerkfehler oder allgemeiner Fehler
                // Hier können Sie den Fehler verarbeiten
            }
        });
    }

    /**
     * Wird aufgerufen, wenn eine Bildauswahl-Aktivität beendet wird.
     * Hier wird das ausgewählte Bild der RecyclerView hinzugefügt.
     *
     * @param requestCode Der Anfragecode der Aktivität.
     * @param resultCode  Der Ergebniscode der Aktivität.
     * @param data        Die Intent-Daten, die das ausgewählte Bild enthalten.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                adapterImageView.insertImage(bitmap);
            } catch (IOException e) {
                Log.e("ImageLoadError", e.getMessage());
            }
        }
    }

    /**
     * Wird aufgerufen, wenn ein Menüelement ausgewählt wird.
     * Hier wird überprüft, ob das Zurück-Pfeil-Element ausgewählt wurde, und die Aktion entsprechend ausgeführt.
     *
     * @param item Das ausgewählte Menüelement.
     * @return true, wenn das Ereignis verarbeitet wurde, andernfalls false.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
