package de.tastykatana.zeiterfassung;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;

/**
 * Created by matthias on 08.02.17.
 */

public class MyApp extends Application {
    public static final String SAVEFILENAME = "savefile";
    public static final String PREFERENCES = "de.tastykatana.zeiterfassung";
    public static final String RUNNING_SINCE_PREF_KEY = "zeiterfassungrunningsince";

    private File savefile;  // TODO cleanup savefile remaints

    public static Zeiterfassung zeiterfassung;
    private static SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();

        savefile = openOrCreateSavefile();
        initializeRunningSince();
    }

    public File getSavefile() {
        return savefile;
    }

    private void initializeRunningSince() {
        prefs = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        long runningSinceMillis = prefs.getLong(RUNNING_SINCE_PREF_KEY, 0);
        zeiterfassung = new Zeiterfassung(runningSinceMillis, this);
        Log.d("startup","zeiterfassung-instance created");
    }

    private File openOrCreateSavefile() {
        File safefile = new File(getFilesDir(), SAVEFILENAME);
        if (!safefile.exists()) {
            try {
                safefile.createNewFile();
                Log.d("startup","savefile created");
            } catch (IOException e) {
                Log.d("startup","could not create savefile");
                e.printStackTrace();
            }
        } else {
            Log.d("startup","savefile already exists");
        }
        return safefile;
    }

    public static void setRunningSincePref(DateTime runningSince) {
        prefs.edit().putLong(RUNNING_SINCE_PREF_KEY, runningSince.getMillis()).apply();
    }
}
