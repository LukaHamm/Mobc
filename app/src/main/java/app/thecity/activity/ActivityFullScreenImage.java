package app.thecity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

import app.thecity.R;
import app.thecity.adapter.AdapterFullScreenImage;
import app.thecity.utils.Tools;
/**
 * Die ActivityFullScreenImage ist dafür verantwortlich, eine Vollbildbildansicht mit
 * Wischfunktion zu erstellen, um zwischen den Bildern zu navigieren!
 * @author Niklas Tarkel
 */
public class ActivityFullScreenImage extends AppCompatActivity {
    public static final String EXTRA_POS = "key.EXTRA_POS";
    public static final String EXTRA_IMGS = "key.EXTRA_IMGS";
    private AdapterFullScreenImage adapter;
    private ViewPager viewPager;
    private TextView text_page;

    /**
     * In der Methode onCreate wird die Aktivität initialisiert und die Benutzeroberfläche mit dem
     * Layout "activity_full_screen_image" erstellt. Dabei werden die übergebenen Daten aus dem Intent
     * verarbeitet, um die gewählte Bildposition und die Liste der Bilder zu erhalten.
     * Die entsprechenden Daten werden in den entsprechenden Ansichten angezeigt, und der ViewPager
     * wird konfiguriert, um die Bilder anzuzeigen und durchzuscrollen!
     * @param savedInstanceState Das Bundle-Objekt, das den Zustand der Aktivität enthält.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        text_page = (TextView) findViewById(R.id.text_intro_MOBC_App);

        ArrayList<String> items = new ArrayList<>();
        Intent i = getIntent();
        final int position = i.getIntExtra(EXTRA_POS, 0);
        items = i.getStringArrayListExtra(EXTRA_IMGS);
        adapter = new AdapterFullScreenImage(ActivityFullScreenImage.this, items);
        final int total = adapter.getCount();
        viewPager.setAdapter(adapter);

        text_page.setText(String.format(getString(R.string.image_of), (position + 1), total));
        ImageView arrowLeft = findViewById(R.id.arrow_left);
        ImageView arrowRight = findViewById(R.id.arrow_right);
        // ausgewähltes Bild zuerst anzeigen
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {
                // Hier können Sie bei Bedarf Code hinzufügen
            }

            @Override
            public void onPageSelected(int pos) {
                text_page.setText(String.format(getString(R.string.image_of), (pos + 1), total));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Hier können Sie bei Bedarf Code hinzufügen
            }
        });
        arrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = viewPager.getCurrentItem();
                if (currentItem > 0) {
                    viewPager.setCurrentItem(currentItem - 1);
                }
            }
        });

        arrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = viewPager.getCurrentItem();
                if (currentItem < viewPager.getAdapter().getCount() - 1) {
                    viewPager.setCurrentItem(currentItem + 1);
                }
            }
        });

        // for system bar in lollipop
        Tools.systemBarLolipop(this);
        Tools.RTLMode(getWindow());
    }

    /**
     * Diese Methode wird aufgerufen, wenn die Aktivität wieder in den Vordergrund kommt.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }
}
