package app.thecity.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;

import app.thecity.AppConfig;
import app.thecity.R;

import app.thecity.data.Constant;
import app.thecity.data.SharedPref;
import app.thecity.data.ThisApplication;
import app.thecity.model.NewsInfo;
import app.thecity.utils.Tools;

public class ActivityNewsInfoDetails extends AppCompatActivity {

    private static final String EXTRA_OBJECT = "key.EXTRA_OBJECT";
    private static final String EXTRA_FROM_NOTIF = "key.EXTRA_FROM_NOTIF";

    /*
      Eine statische Methode zum Navigieren zur ActivityNewsInfoDetails von einer anderen Aktivität
      aus. Sie akzeptiert ein NewsInfo-Objekt und einen from_notif-Boolean, der angibt, ob die
      Aktivität von einer Benachrichtigung gestartet wurde.
     */
    public static void navigate(Activity activity, NewsInfo obj, Boolean from_notif) {
        Intent i = navigateBase(activity, obj, from_notif);
        activity.startActivity(i);
    }

    /*
      Eine weitere statische Methode zum Erstellen des Absichtsobjekts zum Navigieren zur
      ActivityNewsInfoDetails. Sie akzeptiert dieselben Parameter wie navigate().
     */
    public static Intent navigateBase(Context context, NewsInfo obj, Boolean from_notif) {
        Intent i = new Intent(context, ActivityNewsInfoDetails.class);
        i.putExtra(EXTRA_OBJECT, obj);
        i.putExtra(EXTRA_FROM_NOTIF, from_notif);
        return i;
    }

    private Boolean from_notif;
    // extra obj
    private NewsInfo newsInfo;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private View parent_view;
    private WebView webview;


    /*
      Diese Methode wird aufgerufen, wenn die Aktivität erstellt wird. Sie initialisiert die Ansicht,
      die Toolbar und zeigt die Daten der Nachrichteninformationen an.
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_info_details);

        newsInfo = (NewsInfo) getIntent().getSerializableExtra(EXTRA_OBJECT);
        from_notif = getIntent().getBooleanExtra(EXTRA_FROM_NOTIF, false);

        initComponent();
        initToolbar();
        displayData();


        Tools.RTLMode(getWindow());
        // analytics tracking
        ThisApplication.getInstance().trackScreenView("View News Info : " + newsInfo.title);
    }

    // Initialisiert die Hauptansichtskomponente.
    private void initComponent() {
        parent_view = findViewById(android.R.id.content);
    }

    /*
      Initialisiert die Toolbar (App-Aktionenleiste) oben auf der Aktivität und setzt die
      Hintergrundfarbe des App-Layouts entsprechend dem Thema.
     */
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("");

        Tools.systemBarLolipop(this);
        Tools.setActionBarColor(this, actionBar);
        ((AppBarLayout) findViewById(R.id.appbar)).setBackgroundColor(new SharedPref(this).getThemeColorInt());
    }

    /*
      Zeigt die Daten der Nachrichteninformationen an, einschließlich Titel, Inhalt (als WebView),
      Datum, Bild und eine Schaltfläche, um das Bild in voller Größe anzuzeigen.
     */
    private void displayData() {
        ((TextView) findViewById(R.id.title)).setText(Html.fromHtml(newsInfo.title));

        webview = (WebView) findViewById(R.id.content);
        String html_data = "<style>img{max-width:100%;height:auto;} iframe{width:100%;}</style> ";
        html_data += newsInfo.full_content;
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings();
        webview.getSettings().setBuiltInZoomControls(true);
        webview.setBackgroundColor(Color.TRANSPARENT);
        webview.setWebChromeClient(new WebChromeClient());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            webview.loadDataWithBaseURL(null, html_data, "text/html; charset=UTF-8", "utf-8", null);
        } else {
            webview.loadData(html_data, "text/html; charset=UTF-8", null);
        }
        // disable scroll on touch
        webview.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

        ((TextView) findViewById(R.id.date)).setText(Tools.getFormattedDate(newsInfo.last_update));
        Tools.displayImage(this, (ImageView) findViewById(R.id.image), Constant.getURLimgNews(newsInfo.image));

        ((MaterialRippleLayout) findViewById(R.id.lyt_image)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> images_list = new ArrayList<>();
                images_list.add(Constant.getURLimgNews(newsInfo.image));
                Intent i = new Intent(ActivityNewsInfoDetails.this, ActivityFullScreenImage.class);
                i.putStringArrayListExtra(ActivityFullScreenImage.EXTRA_IMGS, images_list);
                startActivity(i);
            }
        });
    }

    /*
      Wird aufgerufen, wenn die Aktivität pausiert wird. Hier wird sichergestellt, dass die WebView
      pausiert wird, um Ressourcen zu sparen.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (webview != null) webview.onPause();
    }

    /*
      Wird aufgerufen, wenn die Aktivität wieder aufgenommen wird. Hier wird sichergestellt,
      dass die WebView fortgesetzt wird
     */
    @Override
    protected void onResume() {
        if (webview != null) webview.onResume();
        super.onResume();
    }

    // Erstellt das Optionsmenü in der Aktionsleiste.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_details, menu);
        return true;
    }

    // Reagiert auf Klicks auf die Menüelemente, z. B. Teilen der Nachricht.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackAction();
            return true;
        } else if (id == R.id.action_share) {
            Tools.methodShareNews(this, newsInfo);
        }
        return super.onOptionsItemSelected(item);
    }

    /*
      Wird aufgerufen, wenn die Zurück-Taste des Geräts gedrückt wird. Hier wird festgelegt,
      wie die Aktivität beendet wird, basierend auf dem Wert von from_notif
     */
    @Override
    public void onBackPressed() {
        onBackAction();
    }

    /*
      Eine Hilfsmethode, die festlegt, wie die Aktivität beendet wird, abhängig davon,
      ob sie von einer Benachrichtigung gestartet wurde oder nicht.
     */
    private void onBackAction() {
        if (from_notif) {
            if (ActivityMain.active) {
                finish();
            } else {
                Intent intent = new Intent(getApplicationContext(), ActivitySplash.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        } else {
            super.onBackPressed();
        }
    }

}
