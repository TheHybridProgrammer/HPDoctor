package com.hybridco.android.hpdoctor;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.hybridco.android.hpdoctor.utilities.Utilities;

public class SettingsActivity extends AppCompatActivity {

    public static Activity activity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        // Shared Preferences init & language to display
        SharedPreferences sharedPref =
                this.getSharedPreferences("com.hybridco.android.hpdoctor_preferences",
                        MODE_PRIVATE);

        String langValue = sharedPref.getString("language_list", "");
        Resources resources = getResources();

        if (langValue.equals("1")) {
            Utilities.changeLang("en", resources);
        } else if (langValue.equals("2")) {
            Utilities.changeLang("ro", resources);
        }

        setContentView(R.layout.settings_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.drawer_menu_settings));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /** Method called when back toolbar button is pressed */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            // NightMode switch
            SwitchPreferenceCompat nightMode = findPreference(this.getResources().getString(R.string.settings_night_mode));
            nightMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if(nightMode.isChecked()) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        nightMode.setChecked(false);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        nightMode.setChecked(true);
                    }
                    return false;
                }
            });

            // Checks the language selected and then sets SettingsActivity string
            // to selected language and recreates activity
            ListPreference langPref = findPreference(this.getResources().getString(R.string.settings_language_list));
            langPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int listLang = Integer.parseInt(newValue.toString());
                    Resources resources = getResources();

                    if(listLang == 1) {
                        Utilities.changeLang("en", resources);
                        activity.recreate();
                    } else if (listLang == 2 ) {
                        Utilities.changeLang("ro", resources);
                        activity.recreate();
                    }
                    return true;
                }
            });
        }
    }

    /** Returns OK for Main onActivity Result, so that it refreshes main activity */
    @Override
    public void onBackPressed() {
        Intent returnIntent = getIntent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}