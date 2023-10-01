package app.thecity.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

import app.thecity.R;
import app.thecity.utils.Tools;



public class AdapterFullScreenImage extends PagerAdapter {

    private Activity act;
    private List<String> imagePaths;
    private LayoutInflater inflater;

    /**
     * Kosntruktor Instanz des Adapters zu erstellen und die erforderlichen Daten zu übergeben
     * @param activity
     * @param imagePaths
     */

    public AdapterFullScreenImage(Activity activity, List<String> imagePaths) {
        this.act = activity;
        this.imagePaths = imagePaths;
    }

    /**
        gibt die Anzahl der Bilder in der Liste zurück. Sie wird vom ViewPager verwendet,
        um zu wissen, wie viele Elemente in der Ansicht angezeigt werden sollen.
     @return Anzahl der Bilder
     */
    @Override
    public int getCount() {
        return this.imagePaths.size();
    }

    /**
        Diese Methode wird verwendet, um zu überprüfen, ob eine Ansicht (View) zu einem bestimmten
        Objekt (Object) gehört. In diesem Fall wird überprüft, ob die Ansicht mit dem übergebenen
        Objekt (hier ein RelativeLayout) übereinstimmt.
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    /**
        Hier wird eine neue Ansicht für das Bild an der gegebenen Position erstellt und dem ViewPager
        hinzugefügt. Diese Methode wird aufgerufen, wenn der ViewPager die aktuelle Seite anzeigen möchte
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView image;
        inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.item_fullscreen_image, container, false);

        image = (PhotoView) viewLayout.findViewById(R.id.image);
        Tools.displayImage(act, image, imagePaths.get(position));
        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    /**
        Diese Methode wird aufgerufen, wenn ein Bild nicht mehr benötigt wird und aus dem ViewPager
        entfernt werden soll
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

}
