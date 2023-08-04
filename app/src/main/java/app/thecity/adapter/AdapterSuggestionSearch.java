package app.thecity.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.thecity.R;

/**
 * RecyclerView-Adapter für die Anzeige von Suchvorschlägen basierend auf einer Suchverlaufsfunktion.
 * Der Adapter zeigt die Suchvorschläge in einer Liste an und ermöglicht es dem Benutzer, auf einen Vorschlag zu klicken,
 * um eine Suche mit diesem Begriff auszuführen oder weitere Aktionen durchzuführen.
 */
public class AdapterSuggestionSearch extends RecyclerView.Adapter<AdapterSuggestionSearch.ViewHolder> {

    private static final String SEARCH_HISTORY_KEY = "_SEARCH_HISTORY_KEY";
    private static final int MAX_HISTORY_ITEMS = 5;

    private List<String> items = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private SharedPreferences prefs;

    /**
     * ViewHolder-Klasse, die die Referenzen zu den Views, die jedes Element (Suchvorschlag) in der RecyclerView repräsentieren, enthält.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public LinearLayout lyt_parent;

        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            lyt_parent = (LinearLayout) v.findViewById(R.id.lyt_parent);
        }
    }

    /**
     * Schnittstelle für das Klickereignis des Suchvorschlags.
     */
    public interface OnItemClickListener {
        void onItemClick(View view, String viewModel, int pos);
    }

    /**
     * Setzt den OnItemClickListener für den Adapter.
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * Konstruktor für den Adapter. Er erstellt eine Instanz des Adapters und liest den Suchverlauf aus den SharedPreferences.
     */
    public AdapterSuggestionSearch(Context context) {
        prefs = context.getSharedPreferences("PREF_RECENT_SEARCH", Context.MODE_PRIVATE);
        this.items = getSearchHistory();
        Collections.reverse(this.items);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String p = items.get(position);
        final int pos = position;
        holder.title.setText(p);
        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onItemClickListener.onItemClick(v, p, pos);
            }
        });
    }

    /**
     * Gibt die Anzahl der Suchvorschläge in der Liste zurück.
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Hilfsklasse, um den Suchverlauf als JSON-String in den SharedPreferences zu speichern.
     */
    private class SearchObject implements Serializable {
        public SearchObject(List<String> items) {
            this.items = items;
        }

        public List<String> items = new ArrayList<>();
    }

    /**
     * Aktualisiert die Liste der Suchvorschläge und aktualisiert die Anzeige.
     */
    public void refreshItems() {
        this.items = getSearchHistory();
        Collections.reverse(this.items);
        notifyDataSetChanged();
    }

    /**
     * Fügt einen Suchbegriff zum Suchverlauf hinzu.
     */
    public void addSearchHistory(String s) {
        SearchObject searchObject = new SearchObject(getSearchHistory());
        if (searchObject.items.contains(s)) searchObject.items.remove(s);
        searchObject.items.add(s);
        if (searchObject.items.size() > MAX_HISTORY_ITEMS) searchObject.items.remove(0);
        String json = new Gson().toJson(searchObject, SearchObject.class);
        prefs.edit().putString(SEARCH_HISTORY_KEY, json).apply();
    }

    /**
     * Liest den Suchverlauf aus den SharedPreferences und gibt eine Liste der Suchvorschläge zurück.
     */
    private List<String> getSearchHistory() {
        String json = prefs.getString(SEARCH_HISTORY_KEY, "");
        if (json.equals("")) return new ArrayList<>();
        SearchObject searchObject = new Gson().fromJson(json, SearchObject.class);
        return searchObject.items;
    }
}
