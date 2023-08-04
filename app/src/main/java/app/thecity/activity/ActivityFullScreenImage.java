package app.thecity.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import java.util.ArrayList;
import app.thecity.R;
import app.thecity.adapter.AdapterFullScreenImage;
import app.thecity.utils.Tools;

/*
    Die ActivityFullScreenImage  ist dafür verantwortlich, eine Vollbildbildansicht mit
    Wischfunktion zu erstellen, um zwischen den Bildern zu navigieren!
 */

public class ActivityFullScreenImage extends AppCompatActivity {
    public static final String EXTRA_POS = "key.EXTRA_POS";
    public static final String EXTRA_IMGS = "key.EXTRA_IMGS";
    private AdapterFullScreenImage adapter;
    private ViewPager viewPager;
    private TextView text_page;

    /*
    In der Methode onCreate wird die Aktivität initialisiert und die Benutzeroberfläche mit dem
    Layout "activity_full_screen_image" erstellt. Dabei werden die übergebenen Daten aus dem Intent
    verarbeitet, um die gewählte Bildposition und die Liste der Bilder zu erhalten.
    Die entsprechenden Daten werden in den entsprechenden Ansichten angezeigt, und der ViewPager
    wird konfiguriert, um die Bilder anzuzeigen!
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);


        text_page = (TextView) findViewById(R.id.text_page);

        ArrayList<String> items = new ArrayList<>();
        Intent i = getIntent();
        final int position = i.getIntExtra(EXTRA_POS, 0);
        items = i.getStringArrayListExtra(EXTRA_IMGS);
        adapter = new AdapterFullScreenImage(ActivityFullScreenImage.this, items);
        final int total = adapter.getCount();
        viewPager.setAdapter(adapter);

        text_page.setText(String.format(getString(R.string.image_of), (position + 1), total));

        // ausgewaehltes Bild zuerst anzeigen
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int pos) {
                text_page.setText(String.format(getString(R.string.image_of), (pos + 1), total));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        // for system bar in lollipop
        Tools.systemBarLolipop(this);
        Tools.RTLMode(getWindow());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
