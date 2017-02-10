package de.tastykatana.zeiterfassung;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by matthias on 08.02.17.
 */

public class Zeiterfassung {
    private SQLiteDatabase database;
    private DateTime runningSince;    // when was the zeiterfassung last started? null if not running
    private Context context;

    public Zeiterfassung(long runningSinceMillis, Context context) {
        if (runningSinceMillis == 0) {
            runningSince = null;
        } else {
            runningSince = new DateTime(runningSinceMillis);
        }
        this.context = context;
        database = (new MyDatabaseHelper(context)).getWritableDatabase();
        Log.d("zeiterfassung", "object constructed. running since " + (new DateTime(runningSinceMillis)).toString());
    }

    public boolean isRunning() {
        return runningSince != null;
    }

    /**
     * Starts the Zeiterfassung, does nothing if already running
     */
    public void start() {
        if (isRunning()) {
            return;
        }
        runningSince = DateTime.now();
        MyApp.setRunningSincePref(runningSince);
        Log.d("zeiterfassung", "zeiterfassung started at " + DateTime.now().toString());
    }

    /**
     * stops the Zeiterfassung and archives the WorkSession, does nothing if not running
     */
    public void stop() {
        // do nothing if Zeiterfassung hasn't been started
        if (runningSince == null) {
            return;
        }

        MyDatabaseHelper.addWorkSession(new WorkSession(runningSince, DateTime.now()), database);

        runningSince = null;
        MyApp.setRunningSincePref(new DateTime(0));
        Log.d("zeiterfassung", "zeiterfassung stopped at " + DateTime.now().toString());
    }

    private DateTime getDayStart(DateTime dt) {
        return dt.withMillisOfDay(0);
    }

    public DateTime getRunningSince() {
        return runningSince;
    }
}
