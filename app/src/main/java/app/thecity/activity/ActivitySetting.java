package app.thecity.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import app.thecity.AppConfig;
import app.thecity.R;
import app.thecity.data.SharedPref;
import app.thecity.utils.Tools;

public class ActivitySetting extends PreferenceActivity {

    private AppCompatDelegate mDelegate;
    private ActionBar actionBar;
    private View parent_view;
    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_);
        parent_view = findViewById(android.R.id.content);
        Tools.RTLMode(getWindow());

        sharedPref = new SharedPref(getApplicationContext());

        Preference themePref = findPreference(getString(R.string.pref_key_theme));
        Preference aboutPref = findPreference("key_about");
        final Preference prefTerm = findPreference(getString(R.string.pref_title_term));

        if (!AppConfig.general.theme_color) {
            PreferenceCategory categoryOthers = (PreferenceCategory) findPreference(getString(R.string.pref_category_display));
            categoryOthers.removePreference(themePref);
        }

        aboutPref.setOnPreferenceClickListener(preference -> {
            Tools.aboutAction(ActivitySetting.this);
            return true;
        });

        themePref.setOnPreferenceClickListener(preference -> {
            dialogColorChooser(ActivitySetting.this);
            return true;
        });

        prefTerm.setOnPreferenceClickListener(preference -> {
            dialogTerm(ActivitySetting.this);
            return true;
        });
    }

    @Override
    protected void onResume() {
        initToolbar();
        super.onResume();
    }

    public void dialogTerm(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.pref_title_term));
        builder.setMessage(activity.getString(R.string.content_term));
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void dialogColorChooser(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_color_theme);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        ListView list = dialog.findViewById(R.id.list_view);
        final String stringArray[] = getResources().getStringArray(R.array.arr_main_color_name);
        final String colorCode[] = getResources().getStringArray(R.array.arr_main_color_code);
        list.setAdapter(new ArrayAdapter<String>(ActivitySetting.this, android.R.layout.simple_list_item_1, stringArray) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                textView.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
                textView.setBackgroundColor(Color.parseColor(colorCode[position]));
                textView.setTextColor(Color.WHITE);
                return textView;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                sharedPref.setThemeColor(colorCode[pos]);
                dialog.dismiss();
                onResume();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(
                preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), "")
        );
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                preference.setSummary(
                        index >= 0 ? listPreference.getEntries()[index] : null
                );
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    private void initToolbar() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(R.string.activity_title_settings);
        Tools.systemBarLolipop(this);
        Tools.setActionBarColor(this, actionBar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }
}
