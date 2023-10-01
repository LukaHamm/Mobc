package app.thecity.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.thecity.R;
import app.thecity.activity.ActivityMain;
import app.thecity.activity.ActivityPlaceDetail;
import app.thecity.adapter.AdapterPlaceGrid;

import app.thecity.connection.RestAdapter;

import app.thecity.data.ThisApplication;
import app.thecity.model.Activity;

import app.thecity.model.User;
import app.thecity.utils.ActivityType;
import app.thecity.utils.Tools;

import retrofit2.Call;
import retrofit2.Response;


// Import-Anweisungen ...

public class FragmentCategory extends Fragment {

    // Statische Variable für das Tag der Kategorie
    public static String TAG_CATEGORY = "key.TAG_CATEGORY";

    // Private Variablen für die Fragment-UI-Elemente und andere Objekte

    private int category_id;
    private View root_view;
    private RecyclerView recyclerView;
    private View lyt_progress;
    private View lyt_not_found;
    private TextView text_progress;


    private AdapterPlaceGrid adapter;

    private List<Activity> activityList;
    private Call<List<Activity>> callActivityList;

    /**
     * Laden des Fragments in das Frame Layout
     * @param inflater LayoutInflater-Objekt zum Aufblasen der View
     * @param container FrameLayout das in xml definiert ist
     * @param savedInstanceState übergebene Parameter (für die Kategorie-Ids)
     *
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Aufblasen des Layouts für das Fragment
        root_view = inflater.inflate(R.layout.fragment_category, null);

        // Fragment-Menü aktivieren
        setHasOptionsMenu(true);

        // Initialisierung der UI-Elemente und der Datenbank
        category_id = getArguments().getInt(TAG_CATEGORY);

        // RecyclerView initialisieren
        recyclerView = (RecyclerView) root_view.findViewById(R.id.recycler);
        lyt_progress = root_view.findViewById(R.id.lyt_progress);
        lyt_not_found = root_view.findViewById(R.id.lyt_not_found);
        text_progress = (TextView) root_view.findViewById(R.id.text_progress);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(Tools.getGridSpanCount(getActivity()), StaggeredGridLayoutManager.VERTICAL));

        // Daten und Adapter für die RecyclerView setzen
        adapter = new AdapterPlaceGrid(getActivity(), recyclerView, new ArrayList<Activity>());
        recyclerView.setAdapter(adapter);

        // OnClickListener für die Liste festlegen
        adapter.setOnItemClickListener((v, obj) -> {
            ActivityPlaceDetail.navigate((ActivityMain) getActivity(), v.findViewById(R.id.lyt_content), obj);
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView v, int state) {
                super.onScrollStateChanged(v, state);
                if (state == v.SCROLL_STATE_DRAGGING || state == v.SCROLL_STATE_SETTLING) {
                    ActivityMain.animateNewplace(true);
                } else {
                    ActivityMain.animateNewplace(false);
                }
            }
        });
        fetch();
        return root_view;
    }

    /**
     * Methode zum Abfragen der Orte in Abhängigkeit der Kategorie-Id
     */
    private void fetch() {
        ActivityType activityType = ActivityType.getbyCategoryId(category_id);
        if (activityType == null) {
            showNoItemView();
        } else if (activityType.equals(ActivityType.own)) {
            fetchOwnActivities();
        }else {
            fetchActivities(activityType);
        }
    }

    /**
     * Laden aller Aktivitäten in Abhängigkeit von der Kategorie
     * @param activityType
     */
    private void fetchActivities(ActivityType activityType){
            callActivityList = RestAdapter.createMobcApi().getActivities(activityType.name());
            callActivityList.enqueue(new retrofit2.Callback<List<Activity>>() {
                @Override
                public void onResponse(Call<List<Activity>> call, Response<List<Activity>> response) {
                    activityList = response.body();
                    for (Activity activity : activityList) {
                        if (activity.location != null) {
                            activity.distance = Tools.getDistanceToCurrentLocation(getContext(), activity.getPosition());
                        }
                    }
                    if (activityList != null) {
                        adapter.insertData(activityList);
                        System.out.println(activityList.size());
                    }
                }

                @Override
                public void onFailure(Call<List<Activity>> call, Throwable t) {
                    if (call != null && !call.isCanceled()) {
                        showNoItemView();
                        Log.e("onFailure", t.getMessage());
                    }
                }
            });

    }

    /**
     * Laden der eigenen Aktivitäten
     */
    private void fetchOwnActivities(){
        User user = Tools.readuser(getContext());
        String header = "bearer " + user.token;
        callActivityList = RestAdapter.createMobcApi().getOwnActivities(header);
        callActivityList.enqueue(new retrofit2.Callback<List<Activity>>() {
            @Override
            public void onResponse(Call<List<Activity>> call, Response<List<Activity>> response) {
                List<Activity> activityList = response.body();
                for (Activity activity : activityList) {
                    if (activity.location != null) {
                        activity.distance = Tools.getDistanceToCurrentLocation(getContext(), activity.getPosition());
                    }
                }
                if (activityList != null) {
                    adapter.insertData(activityList);
                    System.out.println(activityList.size());
                }
            }

            @Override
            public void onFailure(Call<List<Activity>> call, Throwable t) {
                if (call != null && !call.isCanceled()) {
                    showNoItemView();
                    Log.e("onFailure", t.getMessage());
                }
            }
        });
    }


    /**
     * Callbacks sauber beenden
     */
    @Override
    public void onDestroyView() {
        if (callActivityList != null && callActivityList.isExecuted()) {
            callActivityList.cancel();
        }
        super.onDestroyView();
    }

    /**
     *Die RecyclerView bei der Wiederaufnahme des Fragments aktualisieren
     */

    @Override
    public void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    /**
     *Menüoptionen für das Fragment erstellen
     * @param menu Optionsmenü.
     *
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_category, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //

    /**
     * Menüaktionen verarbeiten
     * @param item Das Ausgewählte Menü-Item.
     *
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            ThisApplication.getInstance().setLocation(null);
            text_progress.setText("");
            adapter.resetListData();
            fetch();
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Anzeigen, dass keine Elemente gefunden wurden
     */
    private void showNoItemView() {
        if (adapter.getItemCount() == 0) {
            lyt_not_found.setVisibility(View.VISIBLE);
        } else {
            lyt_not_found.setVisibility(View.GONE);
        }
    }

    public List<Activity> getActivityList() {
        return activityList;
    }
}
