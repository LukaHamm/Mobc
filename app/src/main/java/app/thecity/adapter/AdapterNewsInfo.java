package app.thecity.adapter;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.thecity.AppConfig;
import app.thecity.R;
import app.thecity.data.Constant;
import app.thecity.model.NewsInfo;
import app.thecity.utils.Tools;

/*
    Diese Klasse mit dem Namen "AdapterNewsInfo" ist ein RecyclerView-Adapter für die Anzeige einer
    Liste von Nachrichten-Informationen in Android Studio. Der Adapter zeigt die Informationen zu den
    Nachrichten in einer RecyclerView an, ermöglicht das Klicken auf jede Nachricht für weitere Aktionen
    und unterstützt das Laden weiterer Elemente durch Scrollen (endlose Scrollen).
 */
public class AdapterNewsInfo extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private List<NewsInfo> items = new ArrayList<>();
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;


    public interface OnItemClickListener {
        /*
        Schnittstelle, die eine Methode "onItemClick" definiert. Jedes Mal, wenn eine Nachricht
        geklickt wird, wird diese Methode aufgerufen und die Nachrichteninformation und die Position
        der Nachricht werden übergeben
         */
        void onItemClick(View view, NewsInfo obj, int position);
    }

    /*
        Diese Methode wird verwendet, um einen "OnItemClickListener" für den Adapter zu setzen,
        der aufgerufen wird, wenn ein Benutzer auf eine Nachricht in der Liste klickt.
    */
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterNewsInfo(Context context, RecyclerView view, List<NewsInfo> items) {
        this.items = items;
        ctx = context;
        lastItemViewDetector(view);
    }

    /*
        Klasse hält die Referenzen zu den Views, die jedes Element in der RecyclerView repräsentieren.
         Hier sind es ein TextView "title" und "brief_content" für den Titel und den kurzen Inhalt der
         Nachricht, ein ImageView "image" für das Bild der Nachricht und ein LinearLayout "lyt_parent"
         für das Kachel-Layout der Nachricht
     */
    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView brief_content;
        public ImageView image;
        public LinearLayout lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            brief_content = (TextView) v.findViewById(R.id.brief_content);
            image = (ImageView) v.findViewById(R.id.image);
            lyt_parent = (LinearLayout) v.findViewById(R.id.lyt_parent);
        }
    }

    /*
        Klasse repräsentiert einen einfachen Fortschrittsbalken (ProgressBar), der angezeigt wird,
        während weitere Elemente geladen werden
     */
    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }

    /*
       Hier wird das Layout für jedes Element in der RecyclerView aufgeblasen (inflated) und ein
       ViewHolder erstellt, der die Views hält. Je nachdem, ob es sich um ein reguläres Element
       oder um den Fortschrittsbalken handelt, wird der entsprechende ViewHolder zurückgegeben
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_info, parent, false);
            vh = new OriginalViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    /*
        Hier werden die Daten für jedes Element in der RecyclerView gebunden. Der reguläre ViewHolder
        zeigt die Informationen zur Nachricht an und fügt ein Klickereignis für "lyt_parent" hinzu,
        um den OnItemClickListener auszulösen. Der Fortschrittsbalken wird einfach angezeigt,
        wenn der Ladevorgang aktiv ist
     */

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            final NewsInfo o = items.get(position);
            OriginalViewHolder vItem = (OriginalViewHolder) holder;
            vItem.title.setText(o.title);
            vItem.brief_content.setText(o.brief_content);
            Tools.displayImageThumb(ctx, vItem.image, Constant.getURLimgNews(o.image), 0.5f);
            vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, o, position);
                    }
                }
            });
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    /*
        Diese Methode gibt die Anzahl der Elemente in der Liste zurück. Sie wird vom RecyclerView
        verwendet, um zu wissen, wie viele Elemente angezeigt werden sollen
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

     /*
         Methode gibt den View-Typ für ein Element anhand seiner Position zurück. Abhängig davon,
         ob das Element ein reguläres Element oder der Fortschrittsbalken ist, wird der entsprechende
         View-Typ zurückgegeben
      */
    @Override
    public int getItemViewType(int position) {
        return this.items.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    /*
        Methode wird verwendet, um weitere Daten in die Liste einzufügen, wenn beim endlosen Scrollen
        neue Elemente geladen werden. Sie aktualisiert die Anzeige, um die neuen Elemente anzuzeigen
     */
    public void insertData(List<NewsInfo> items) {
        setLoaded();
        int positionStart = getItemCount();
        int itemCount = items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    /*
        Methoden werden verwendet, um den Ladezustand des Adapters zu setzen. "setLoaded()" gibt an,
        dass der Ladevorgang abgeschlossen ist, während "setLoading()" den Adapter in den Lademodus
        versetzt und den Fortschrittsbalken hinzufügt
     */
    public void setLoaded() {
        loading = false;
        for (int i = 0; i < getItemCount(); i++) {
            if (items.get(i) == null) {
                items.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    /*
        Methoden werden verwendet, um den Ladezustand des Adapters zu setzen. "setLoaded()" gibt an,
        dass der Ladevorgang abgeschlossen ist, während "setLoading()" den Adapter in den Lademodus
        versetzt und den Fortschrittsbalken hinzufügt
    */
    public void setLoading() {
        if (getItemCount() != 0) {
            this.items.add(null);
            notifyItemInserted(getItemCount() - 1);
            loading = true;
        }
    }

    /*
        Methode wird verwendet, um die Liste von Nachrichteninformationen zurückzusetzen.
        Sie wird normalerweise verwendet, wenn du eine neue Liste von Nachrichten erhalten und
        anzeigen möchtest
     */
    public void resetListData() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

    /*
        Methode wird verwendet, um einen OnLoadMoreListener zu setzen, der aufgerufen wird,
        wenn neue Elemente geladen werden müssen
     */
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    /*
        Methode erkennt das letzte sichtbare Element in der RecyclerView, um das endlose Scrollen
        zu aktivieren. Wenn das letzte Element sichtbar ist und weitere Elemente geladen werden müssen,
         wird der OnLoadMoreListener aufgerufen
     */
    private void lastItemViewDetector(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int lastPos = layoutManager.findLastVisibleItemPosition();
                    if (!loading && lastPos == getItemCount() - 1 && onLoadMoreListener != null) {
                        if (onLoadMoreListener != null) {
                            int current_page = getItemCount() / AppConfig.general.limit_news_request;
                            onLoadMoreListener.onLoadMore(current_page);
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }

}