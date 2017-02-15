package de.tastykatana.zeiterfassung;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.joda.time.DateTime;

/**
 * Created by matthias on 08.02.17.
 */

public class MyApp extends Application {
    public static final String PREFERENCES = "de.tastykatana.zeiterfassung";
    private static final String RUNNING_SINCE_PREF_KEY = "zeiterfassungrunningsince";

    public static Zeiterfassung zeiterfassung;
    private static SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize all settings in preferences.xml with default values on first launch
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        initializeRunningSince();
    }

    private void initializeRunningSince() {
        prefs = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        long runningSinceMillis = prefs.getLong(RUNNING_SINCE_PREF_KEY, 0);
        zeiterfassung = new Zeiterfassung(runningSinceMillis, this);
        Log.d("startup","zeiterfassung-instance created");
    }

    public static void setRunningSincePref(DateTime runningSince) {
        prefs.edit().putLong(RUNNING_SINCE_PREF_KEY, runningSince.getMillis()).apply();
    }
}
