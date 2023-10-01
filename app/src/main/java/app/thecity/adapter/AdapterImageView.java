package app.thecity.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.thecity.R;


public class AdapterImageView extends RecyclerView.Adapter<AdapterImageView.ImageListViewHolder>  {

    private Context context;
    private List<Bitmap> imageList;

    public AdapterImageView(Context context, List<Bitmap> imagelist) {
        this.context = context;
        this.imageList=imagelist;
    }

    /**
     * Erstellt das Layout für das Anzeigen eines Bildes in einer ImageView
     * @param parent   Die Recyclerview in der Layout-xml
     * @param viewType Der View Typ
     * @return
     */
    @NonNull
    @Override
    public ImageListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new AdapterImageView.ImageListViewHolder(view);
    }

    /**
     * Initialisiert die Bilddaten, Setzt die Bitmap
     * @param holder   Das Viewholder Objekt das mit der Imageview initialisiert wurde
     * @param position Die Position der DAten
     */
    @Override
    public void onBindViewHolder(@NonNull ImageListViewHolder holder, int position) {
        Bitmap bitmap = imageList.get(position);
        holder.imageView.setImageBitmap(bitmap);
    }

    /**
     *
     * @return Anzahl der Bilder
     */
    @Override
    public int getItemCount() {
        return imageList.size();
    }


    public class ImageListViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        public ImageListViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image);
        }
    }


    /**
     * Mehode zur Aktualisierung der Bilddaten
     * @param bitmap
     */
    public void insertImage(Bitmap bitmap){
        this.imageList.add(bitmap);
        notifyDataSetChanged();

    }

    public List<Bitmap> getImageList() {
        return imageList;
    }
}
