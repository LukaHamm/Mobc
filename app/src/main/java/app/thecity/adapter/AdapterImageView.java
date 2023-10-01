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

    @NonNull
    @Override
    public ImageListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new AdapterImageView.ImageListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageListViewHolder holder, int position) {
        Bitmap bitmap = imageList.get(position);
        holder.imageView.setImageBitmap(bitmap);
    }

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


    public void insertImage(Bitmap bitmap){
        this.imageList.add(bitmap);
        notifyDataSetChanged();

    }

    public List<Bitmap> getImageList() {
        return imageList;
    }
}
