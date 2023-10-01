package app.thecity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;

import java.util.ArrayList;
import java.util.List;

import app.thecity.R;
import app.thecity.data.Constant;
import app.thecity.utils.Tools;


/*
    Diese Klasse mit dem Namen "AdapterImageList" ist ein RecyclerView-Adapter für die Anzeige
    einer Liste von Bildern in einer RecyclerView in Android Studio. Der Adapter zeigt die Bilder
    in einer Kachelansicht an und ermöglicht das Klicken auf jedes Bild, um eine Aktion auszuführen
 */
public class AdapterImageList extends RecyclerView.Adapter<AdapterImageList.ViewHolder> {

    private List<String> items = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    private int lastPosition = -1;
    private Context ctx;

    // hält die Referenzen zu den Views, die jedes Element in der RecyclerView repräsentieren
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView image;
        public MaterialRippleLayout lyt_parent;

        public ViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            lyt_parent = (MaterialRippleLayout) v.findViewById(R.id.lyt_parent);
        }
    }

    /*
        Diese Methode wird verwendet, um einen "OnItemClickListener" für den Adapter zu setzen,
        der aufgerufen wird, wenn ein Benutzer auf ein Bild in der Liste klickt
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /*
        Konstruktor dieses Adapters wird verwendet, um eine Instanz des Adapters zu erstellen
        und die erforderlichen Daten zu übergeben
     */
    public AdapterImageList(Context ctx, List<String> items) {
        this.ctx = ctx;
        this.items = items;
    }

    /*
        Layout für jedes Element in der RecyclerView aufgeblasen (inflated) und ein ViewHolder
        erstellt, der die Views hält.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    /*
        Hier werden die Daten für jedes Element in der RecyclerView gebunden. Das Bild wird mit
        Hilfe der "Tools.displayImage"-Methode in die ImageView geladen. Außerdem wird ein
        Klickereignis für das "lyt_parent" (das Kachel-Layout) hinzugefügt, das den
        OnItemClickListener auslöst
     */
    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final String p = items.get(position);
        Tools.displayImage(ctx, holder.image, Constant.getURLimgActivity(p));
        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // Give some delay to the ripple to finish the effect
                onItemClickListener.onItemClick(v, p, position);
            }
        });
    }

    /*
         Methode gibt die Anzahl der Elemente in der Liste zurück. Sie wird vom RecyclerView verwendet,
         um zu wissen, wie viele Elemente angezeigt werden sollen.
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /*
        Schnittstelle, die eine Methode "onItemClick" definiert. Jedes Mal, wenn ein Bild geklickt wird,
         wird diese Methode aufgerufen und die Position des Bildes und der Bild-URL werden übergeben.
     */
    public interface OnItemClickListener {
        void onItemClick(View view, String viewModel, int pos);
    }
}