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

import java.io.IOException;

import app.thecity.R;
import app.thecity.data.SharedPref;
import app.thecity.utils.ActivityType;
import app.thecity.utils.Tools;

public class ActivityNewPlace extends AppCompatActivity {

    public ActionBar actionBar;

    private static final int SELECT_IMAGE_REQUEST = 3;
    private static final int PERMISSION_REQUEST_CODE = 4;

    private EditText addressEditText;
    private ImageView selectedImageView;
    private EditText descriptionEditText;
    private Button selectImageButton;
    private Button saveNewPlaceButton;
    private ActivityType selectedCategoryType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_place);

        initToolbar();

        selectedImageView = findViewById(R.id.selectedImageView);
        descriptionEditText = findViewById(R.id.description);
        selectImageButton = findViewById(R.id.selectImageButton);
        saveNewPlaceButton = findViewById(R.id.safe_new_place_button);
        addressEditText = findViewById(R.id.address);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setContentScrimColor(new SharedPref(this).getThemeColorInt());

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        selectedCategoryType = ActivityType.all;

        Spinner spinner = findViewById(R.id.dropdown_menu);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.dropdown_items, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedItem = adapterView.getItemAtPosition(position).toString();

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
                Toast.makeText(ActivityNewPlace.this, "Bitte wählen Sie eine Kategorie aus.", Toast.LENGTH_SHORT).show();
            }
        });

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

        saveNewPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String activityInfo = generateActivityInfo();
                Toast.makeText(ActivityNewPlace.this, activityInfo, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ActivityNewPlace.this, ActivityMain.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                selectedImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                Log.e("ImageLoadError", e.getMessage());
            }
        }
    }

    @Override
    protected void onResume() {
        if (actionBar != null) {
            Tools.setActionBarColor(this, actionBar);
            Tools.systemBarLolipop(this);
        }
        super.onResume();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(R.string.title_nav_news);
        Tools.systemBarLolipop(this);
    }

    private String generateActivityInfo() {
        String activityInfo = "Aktivität: " + selectedCategoryType + "\n";
        activityInfo += "Adresse: " + addressEditText.getText().toString() + "\n";
        activityInfo += "Beschreibung: " + descriptionEditText.getText().toString() + "\n";
        return activityInfo;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Wenn die Berechtigung gewährt wird, öffnen Sie die Galerie erneut
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_IMAGE_REQUEST);
            } else {
                // Wenn die Berechtigung verweigert wird, informieren Sie den Benutzer
                Toast.makeText(this, "Berechtigung zum Lesen des externen Speichers verweigert", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
