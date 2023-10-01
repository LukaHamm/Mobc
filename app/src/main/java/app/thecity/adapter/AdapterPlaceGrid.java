package app.thecity.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.thecity.AppConfig;
import app.thecity.R;
import app.thecity.connection.API;
import app.thecity.connection.RestAdapter;
import app.thecity.data.Constant;
import app.thecity.model.Activity;
import app.thecity.model.Image;
import app.thecity.model.Place;
import app.thecity.utils.Tools;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;



public class AdapterPlaceGrid extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private Context ctx;
    private List<Activity> items = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private int lastPosition = -1;
    private boolean clicked = false;


    /*
        Jedes Mal, wenn ein Ort geklickt wird, wird diese Methode aufgerufen und die Informationen
        zum Ort werden übergeben.
     */
    public interface OnItemClickListener {
        void onItemClick(View view, Activity viewModel);
    }

    /**
        Ist der ClickListener für das Starten der Detailansicht
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public ImageView image;
        public TextView distance;
        public LinearLayout lyt_distance;
        public MaterialRippleLayout lyt_parent;

        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            image = (ImageView) v.findViewById(R.id.image);
            distance = (TextView) v.findViewById(R.id.distance);
            lyt_distance = (LinearLayout) v.findViewById(R.id.lyt_distance);
            lyt_parent = (MaterialRippleLayout) v.findViewById(R.id.lyt_parent);
        }
    }

    /*
        repräsentiert einen einfachen Fortschrittsbalken (ProgressBar), der angezeigt wird,
        während weitere Elemente geladen werden
     */
    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }

    public AdapterPlaceGrid(Context ctx, RecyclerView view, List<Activity> items) {
        this.ctx = ctx;
        this.items = items;

    }

    /**
         Layout für jedes Element in der RecyclerView aufgeblasen (inflated) und ein ViewHolder erstellt,
         der die Views hält. Je nachdem, ob es sich um ein reguläres Element oder den Fortschrittsbalken
         handelt, wird der entsprechende ViewHolder zurückgegeben
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
            vh = new ViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    /**
        Hier werden die Daten für jedes Element in der RecyclerView gebunden.
        Der reguläre ViewHolder zeigt die Informationen zum Ort an, fügt ein Klickereignis
        für "lyt_parent" hinzu, um den OnItemClickListener auszulösen, und wendet eine Animation
        auf das Element an, wenn es gebunden wird
        Der OnclickListener wechselt zur Detailansicht und übergibt das Ort-Objekt
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder vItem = (ViewHolder) holder;
            final Activity activity = items.get(position);
            vItem.title.setText(activity.title);
            if (activity.images != null && !activity.images.isEmpty()) {
                Tools.displayImageThumb(ctx, vItem.image, Constant.getURLimgActivity(activity.images.get(0)), 0.5f);
            }


            if (activity.distance == -1) {
                vItem.lyt_distance.setVisibility(View.GONE);
            } else {
                vItem.lyt_distance.setVisibility(View.VISIBLE);
                vItem.distance.setText(Tools.getFormatedDistance(activity.distance));
            }

            // Here you apply the animation when the view is bound
            setAnimation(vItem.lyt_parent, position);

            vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (!clicked && onItemClickListener != null) {
                        clicked = true;
                        onItemClickListener.onItemClick(v, activity);
                    }
                }
            });
            clicked = false;
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
        if (getItemViewType(position) == VIEW_PROG) {
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
        } else {
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(false);
        }

    }




    /**
       @return Anzahl Orte
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
        gibt den View-Typ für ein Element anhand seiner Position zurück. Abhängig davon, ob es sich
        um ein reguläres Element oder den Fortschrittsbalken handelt, wird der entsprechende
        View-Typ zurückgegeben
     */
    @Override
    public int getItemViewType(int position) {
        return this.items.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
        Diese Methode wird verwendet, um eine Animation auf jedes Element in der Rasteransicht
        anzuwenden, wenn es gebunden wird. Dadurch wird ein Einblenden-Effekt für die Elemente
        erzeugt, wenn sie auf dem Bildschirm erscheinen.
     */
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(ctx, R.anim.slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    /**
     * Methode um die Listdaten zu altualisieren
     * @param items
     */
    public void insertData(List<Activity> items) {
        int positionStart = getItemCount();
        int itemCount = items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(positionStart, itemCount);
    }


    /**
        Diese Methode wird verwendet, um die Liste von Orten zurückzusetzen.
        Sie wird normalerweise verwendet, wenn du eine neue Liste von Orten erhalten und
        anzeigen möchtest
     */
    public void resetListData() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }





}