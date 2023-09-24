package app.thecity.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import app.thecity.R;
import app.thecity.data.SharedPref;
import app.thecity.databinding.ActivityNewPlaceBinding;
import app.thecity.utils.Tools;

public class ActivityNewPlace extends AppCompatActivity {

    public ActionBar actionBar;
    private ActivityNewPlaceBinding binding;

    private EditText phoneEditText;
    private EditText websiteEditText;
    private EditText addressEditText;
    private ImageView selectedImageView;
    private EditText descriptionEditText;
    private Button selectImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewPlaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initToolbar(); // Initialisiere die Toolbar

        // Initialisieren Sie die UI-Elemente
        phoneEditText = findViewById(R.id.phone);
        websiteEditText = findViewById(R.id.website);
        addressEditText = findViewById(R.id.address);
        selectedImageView = findViewById(R.id.selectedImageView);
        descriptionEditText = findViewById(R.id.description);
        selectImageButton = findViewById(R.id.selectImageButton);

        CollapsingToolbarLayout collapsing_toolbar = findViewById(R.id.collapsing_toolbar);
        collapsing_toolbar.setContentScrimColor(new SharedPref(this).getThemeColorInt());

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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

    //Wird aufgerufen, wenn die Aktivität wieder aufgenommen wird. Es aktualisiert die
    //Aktionenleistenfarbe und die Systemleiste (nur für Android Lollipop und höher).

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


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
