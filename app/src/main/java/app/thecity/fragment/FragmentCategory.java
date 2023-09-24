package app.thecity.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
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

import com.google.android.gms.common.api.Api;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.thecity.AppConfig;
import app.thecity.R;
import app.thecity.activity.ActivityMain;
import app.thecity.activity.ActivityPlaceDetail;
import app.thecity.adapter.AdapterPlaceGrid;
import app.thecity.connection.API;
import app.thecity.connection.RestAdapter;
import app.thecity.connection.callbacks.CallbackListPlace;
import app.thecity.connection.callbacks.CallbackUser;
import app.thecity.data.DatabaseHandler;
import app.thecity.data.SharedPref;
import app.thecity.data.ThisApplication;
import app.thecity.model.Activity;
import app.thecity.model.Place;
import app.thecity.utils.Tools;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;


// Import-Anweisungen ...

public class FragmentCategory extends Fragment {

    // Statische Variable für das Tag der Kategorie
    public static String TAG_CATEGORY = "key.TAG_CATEGORY";

    // Private Variablen für die Fragment-UI-Elemente und andere Objekte
    private int count_total = 0;
    private int category_id;
    private View root_view;
    private RecyclerView recyclerView;
    private View lyt_progress;
    private View lyt_not_found;
    private TextView text_progress;
    private Snackbar snackbar_retry;
    private DatabaseHandler db;
    private SharedPref sharedPref;
    private AdapterPlaceGrid adapter;
    private Call<CallbackListPlace> callback;

    private Call<List<Activity>> callActivityList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Aufblasen des Layouts für das Fragment
        root_view = inflater.inflate(R.layout.fragment_category, null);
        callActivityList = RestAdapter.createMobcApi().getActivities();
        callActivityList.enqueue(new retrofit2.Callback<List<Activity>>() {
            @Override
            public void onResponse(Call<List<Activity>> call, Response<List<Activity>> response) {
                List<Activity> activityList = response.body();
                for (Activity activity:activityList){
                    if (activity.location != null){
                        activity.distance = Tools.getDistanceToCurrentLocation(getContext(),activity.getPosition());
                    }
                }
                if (activityList != null) {
                    adapter.insertData(activityList);
                    System.out.println(activityList.size());
                }
            }

            @Override
            public void onFailure(Call<List<Activity>> call, Throwable t) {

            }
        });
        // Fragment-Menü aktivieren
        setHasOptionsMenu(true);

        // Initialisierung der UI-Elemente und der Datenbank
        db = new DatabaseHandler(getActivity());
        sharedPref = new SharedPref(getActivity());
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
        /*adapter.setOnItemClickListener((v, obj) -> {
            ActivityPlaceDetail.navigate((ActivityMain) getActivity(), v.findViewById(R.id.lyt_content), obj);
        });*/

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView v, int state) {
                super.onScrollStateChanged(v, state);
                if (state == v.SCROLL_STATE_DRAGGING || state == v.SCROLL_STATE_SETTLING) {
                    ActivityMain.animateFab(true);
                } else {
                    ActivityMain.animateFab(false);
                }
            }
        });
        // Daten aktualisieren, wenn notwendig, sonst die Adapterdaten setzen
        /*if (sharedPref.isRefreshPlaces() || db.getPlacesSize() == 0) {
            actionRefresh(sharedPref.getLastPlacePage());
        } else {
            startLoadMoreAdapter();
        }*/
        return root_view;
    }


    // Aufräumen und Callbacks aufheben, wenn die Ansicht zerstört wird
    @Override
    public void onDestroyView() {
        if (snackbar_retry != null) snackbar_retry.dismiss();
        if (callback != null && callback.isExecuted()) {
            callback.cancel();
        }
        super.onDestroyView();
    }

    // Die RecyclerView bei der Wiederaufnahme des Fragments aktualisieren
    @Override
    public void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    // Menüoptionen für das Fragment erstellen
    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_category, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // Menüaktionen verarbeiten
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            ThisApplication.getInstance().setLocation(null);
            sharedPref.setLastPlacePage(1);
            sharedPref.setRefreshPlaces(true);
            text_progress.setText("");
            if (snackbar_retry != null) snackbar_retry.dismiss();
            actionRefresh(sharedPref.getLastPlacePage());
        }
        return super.onOptionsItemSelected(item);
    }

    // Adapter mit den Daten der ersten Seite laden
    private void startLoadMoreAdapter() {
        adapter.resetListData();
        List<Place> items = db.getPlacesByPage(category_id, AppConfig.general.limit_loadmore, 0);
        //adapter.insertData(items);
        showNoItemView();
        final int item_count = (int) db.getPlacesSize(category_id);
        // Auf Ereignis warten, wenn das Scrollen das Ende erreicht
        adapter.setOnLoadMoreListener(current_page -> {
            if (item_count > adapter.getItemCount() && current_page != 0) {
                displayDataByPage(current_page);
            } else {
                adapter.setLoaded();
            }
        });
    }

    // Daten für eine bestimmte Seite anzeigen
    private void displayDataByPage(final int next_page) {
        adapter.setLoading();
        new Handler().postDelayed(() -> {
            List<Place> items = db.getPlacesByPage(category_id, AppConfig.general.limit_loadmore, (next_page * AppConfig.general.limit_loadmore));
            //adapter.insertData(items);
            showNoItemView();
        }, 500);
    }

    // Vor dem Aktualisieren der Daten einige Bedingungen prüfen
    private void actionRefresh(int page_no) {
        boolean conn = Tools.cekConnection(getActivity());
        if (conn) {
            if (!onProcess) {
                onRefresh(page_no);
            } else {
                Snackbar.make(root_view, R.string.task_running, Snackbar.LENGTH_SHORT).show();
            }
        } else {
            onFailureRetry(page_no, getString(R.string.no_internet));
        }
    }

    // Variable zum Überprüfen, ob eine Aktualisierung im Gange ist
    private boolean onProcess = false;

    // Daten aktualisieren
    private void onRefresh(final int page_no) {
        onProcess = true;
        showProgress(onProcess);
        //hier eigene Orte abfragen
        callback = RestAdapter.createAPI().getPlacesByPage(page_no, AppConfig.general.limit_place_request, (AppConfig.general.lazy_load ? 1 : 0));
        callback.enqueue(new retrofit2.Callback<CallbackListPlace>() {
            @Override
            public void onResponse(Call<CallbackListPlace> call, Response<CallbackListPlace> response) {
                CallbackListPlace resp = response.body();
                if (resp != null) {
                    count_total = resp.count_total;
                    if (page_no == 1) db.refreshTablePlace();
                    db.insertListPlaceAsync(resp.places);  // Ergebnisse in der Datenbank speichern
                    sharedPref.setLastPlacePage(page_no + 1);
                    delayNextRequest(page_no);
                    String str_progress = String.format(getString(R.string.load_of), (page_no * AppConfig.general.limit_place_request), count_total);
                    text_progress.setText(str_progress);
                } else {
                    onFailureRetry(page_no, getString(R.string.refresh_failed));
                }
            }

            @Override
            public void onFailure(Call<CallbackListPlace> call, Throwable t) {
                if (call != null && !call.isCanceled()) {
                    Log.e("onFailure", t.getMessage());
                    boolean conn = Tools.cekConnection(getActivity());
                    if (conn) {
                        onFailureRetry(page_no, getString(R.string.refresh_failed));
                    } else {
                        onFailureRetry(page_no, getString(R.string.no_internet));
                    }
                }
            }
        });
    }

    // Fortschrittsanzeige anzeigen
    private void showProgress(boolean show) {
        if (show) {
            lyt_progress.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            lyt_not_found.setVisibility(View.GONE);
        } else {
            lyt_progress.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    // Anzeigen, dass keine Elemente gefunden wurden
    private void showNoItemView() {
        if (adapter.getItemCount() == 0) {
            lyt_not_found.setVisibility(View.VISIBLE);
        } else {
            lyt_not_found.setVisibility(View.GONE);
        }
    }

    // Wenn ein Fehler auftritt, Retry-Snackbar anzeigen
    private void onFailureRetry(final int page_no, String msg) {
        onProcess = false;
        showProgress(onProcess);
        showNoItemView();
        startLoadMoreAdapter();
        try {
            snackbar_retry = Snackbar.make(root_view, msg, Snackbar.LENGTH_INDEFINITE);
            snackbar_retry.setAction(R.string.RETRY, v -> actionRefresh(page_no));
            snackbar_retry.show();
        } catch (Exception e) {
        }
    }

    // Nächsten Request verzögert ausführen
    private void delayNextRequest(final int page_no) {
        if (count_total == 0) {
            onFailureRetry(page_no, getString(R.string.refresh_failed));
            return;
        }
        if ((page_no * AppConfig.general.limit_place_request) > count_total) { // Wenn alle Daten geladen wurden
            onProcess = false;
            showProgress(onProcess);
            startLoadMoreAdapter();
            sharedPref.setRefreshPlaces(false);
            text_progress.setText("");
            Snackbar.make(root_view, R.string.load_success, Snackbar.LENGTH_LONG).show();
            return;
        }
        new Handler().postDelayed(() -> onRefresh(page_no + 1), 300);
    }
}
