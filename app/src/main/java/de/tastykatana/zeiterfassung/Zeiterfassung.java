package de.tastykatana.zeiterfassung;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        cursor.close();
        return result;
    }

    public DateTime getRunningSince() {
        return runningSince;
    }

    public void deleteAll() {
        database.execSQL("delete from " + MyDatabaseHelper.TABLE_NAME_SESSIONS);
        Log.d("zeiterfassung", "deleted all contents from table '" + MyDatabaseHelper.TABLE_NAME_SESSIONS + "'");
    }

    /**
     * builds a formatted Stundenzettel for the specified month
     *
     * @param context
     * @param month all WorkSessions in the same mont as this DateTime are added to the returned Stundenzettel
     * @return
     */
    public ViewGroup buildStundenzettelForMonth(Context context, DateTime month) {
        // create main layout for document (this is drawn to the page in the end
        LinearLayout result = new LinearLayout(context);
        result.setOrientation(LinearLayout.VERTICAL);

        // create headline for Document
        TextView headline = new TextView(context);
        headline.setText(context.getString(R.string.stundenzettel_headline));
        headline.setTextSize(TypedValue.COMPLEX_UNIT_PX, 24);
        result.addView(headline);
        headline.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // add name and jobdescription
        TextView userAndJobname = new TextView(context);
        StringBuilder content = new StringBuilder();
        // append username
        content.append(PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.user_name_preference_key), null));
        content.append("\t\t\t");
        // append jobname
        content.append(PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.job_name_preference_key), null));
        userAndJobname.setText(content.toString());
        userAndJobname.setTextSize(TypedValue.COMPLEX_UNIT_PX, 12);
        result.addView(userAndJobname);

        // add timeframe
        TextView timeFrame = new TextView(context);
        timeFrame.setText(context.getString(R.string.timeframe) + ": " + month.toString("MM.yyyy"));
        timeFrame.setTextSize(TypedValue.COMPLEX_UNIT_PX, 12);
        result.addView(timeFrame);

        // insert spacer
        Space spacer1 = new Space(context);
        spacer1.setMinimumHeight(30);
        result.addView(spacer1);

        // make headline for Workday table
        TextView tableHeadline = new TextView(context);
        tableHeadline.setText(context.getString(R.string.stundenzettel_table_headline));
        tableHeadline.setTextSize(TypedValue.COMPLEX_UNIT_PX, 8);
        result.addView(tableHeadline);

        // make Workday table
        DateTime zettelStart = month.withMillisOfDay(0).withDayOfMonth(1);
        DateTime zettelEnd = month.withMillisOfDay(DateTimeConstants.MILLIS_PER_DAY-1).dayOfMonth().withMaximumValue();
        Map<DateTime, Workday> workdayMap = getWorkdayMap(zettelStart, zettelEnd);

        // go through all days in the specified month and add them to the Stundenzettel
        for (DateTime i = zettelStart; i.isBefore(zettelEnd) || i.equals(zettelEnd); i = i.dayOfMonth().addToCopy(1)) {
            TextView workdayView = new TextView(context);
            Workday currentWorkday = workdayMap.get(i);
            if (currentWorkday == null) {
                // no work was done on day i
                workdayView.setText(i.toString("dd.MM.yyyy"));
            } else {
                workdayView.setText(currentWorkday.toFormattedString());
            }
            workdayView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 8);
            result.addView(workdayView);
        }

        // add another spacer
        Space spacer2 = new Space(context);
        spacer2.setMinimumHeight(30);
        result.addView(spacer2);

        // add signature field
        TextView signatureLine = new TextView(context);
        signatureLine.setTextSize(TypedValue.COMPLEX_UNIT_PX, 24);
        signatureLine.setText(context.getString(R.string.signature_line, DateTime.now().toString("dd.MM.yyyy")));
        result.addView(signatureLine);
        TextView signatureLabelling = new TextView(context);
        signatureLabelling.setTextSize(TypedValue.COMPLEX_UNIT_PX, 8);
        signatureLabelling.setText(context.getString(R.string.signature_line_labelling));
        result.addView(signatureLabelling);

        // set textcolor to black
        for (int i = 0; i<result.getChildCount(); i++) {
            if(result.getChildAt(i) instanceof TextView) {
                ((TextView) result.getChildAt(i)).setTextColor(Color.BLACK);
            }
        }

        return result;
    }

    private Map<DateTime,Workday> getWorkdayMap(DateTime start, DateTime end) {
        Map<DateTime, Workday> result = new HashMap<>();

        for (WorkSession session : getAllSessions()) {
            // get the start of the day the session started on
            DateTime sessionDay = session.getStart().withMillisOfDay(0);

            // add session to result if it started in the specified time interval
            if (session.getStart().isAfter(start) && session.getStart().isBefore(end)) {
                if (result.get(sessionDay) == null) {
                    result.put(sessionDay, new Workday(session));
                } else {
                    result.get(sessionDay).addSession(session);
                }
            }
        }

        return result;
    }

    /**
     * builds a sorted Array with exactly one DateTime instance in each month that has recorded Worksessions in it
     * @return
     */
    public DateTime[] getMonths() {
        SortedSet<DateTime> resultSet = new TreeSet<>();

        for (WorkSession session : getAllSessions()) {
            DateTime month = getDayStart(session.getStart()).withDayOfMonth(1);
            if (!resultSet.contains(month)) {
                resultSet.add(month);
            }
        }

        DateTime[] resultArray = new DateTime[resultSet.size()];
        resultSet.toArray(resultArray);
        return resultArray;
    }
}
