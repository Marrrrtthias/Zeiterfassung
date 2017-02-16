package de.tastykatana.zeiterfassung;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import org.joda.time.DateTime;

import java.io.File;

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

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // initialize all settings in preferences.xml with default values on first launch
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // set the export directory to Documents/Stundenzettel if it hasn't been set by the user yet
        File sdCard = Environment.getExternalStorageDirectory();
        if (prefs.getString(getString(R.string.filepath_preference_key), null).equals(getString(R.string.filepath_default_value))) {
            prefs.edit()
                    .putString(getString(R.string.filepath_preference_key), sdCard.getAbsolutePath() + File.separator + "Documents" + File.separator + "Stundenzettel")
                    .commit();
        }

        initializeRunningSince();
    }

    private void initializeRunningSince() {
        long runningSinceMillis = prefs.getLong(RUNNING_SINCE_PREF_KEY, 0);
        zeiterfassung = new Zeiterfassung(runningSinceMillis, this);
        Log.d("startup","zeiterfassung-instance created");
    }

    public static void setRunningSincePref(DateTime runningSince) {
        prefs.edit().putLong(RUNNING_SINCE_PREF_KEY, runningSince.getMillis()).apply();
    }

    public static SharedPreferences getPrefs() {
        return prefs;
    }
}
