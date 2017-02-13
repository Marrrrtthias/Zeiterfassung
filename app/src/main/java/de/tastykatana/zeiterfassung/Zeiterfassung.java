package de.tastykatana.zeiterfassung;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by matthias on 08.02.17.
 */

public class Zeiterfassung {
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private DateTime runningSince;    // when was the zeiterfassung last started? null if not running

    public Zeiterfassung(long runningSinceMillis, Context context) {
        if (runningSinceMillis == 0) {
            runningSince = null;
        } else {
            runningSince = new DateTime(runningSinceMillis);
        }
        dbHelper = new MyDatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
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

    public SortedSet<WorkSession> getAllSessions() {
        Cursor cursor = database.rawQuery("select * from " + MyDatabaseHelper.TABLE_NAME_SESSIONS, null);
        SortedSet<WorkSession> result = new TreeSet<>(new WorkSessionComparator());

        while (cursor.moveToNext()) {
            long startMillis = cursor.getLong(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_START));
            long endMillis = cursor.getLong(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_END));
            result.add(new WorkSession(startMillis, endMillis));
        }

        return result;
    }

    public DateTime getRunningSince() {
        return runningSince;
    }

    public void deleteAll() {
        database.execSQL("delete from " + MyDatabaseHelper.TABLE_NAME_SESSIONS);
        Log.d("zeiterfassung", "deleted all contents from table '" + MyDatabaseHelper.TABLE_NAME_SESSIONS + "'");
    }
}
