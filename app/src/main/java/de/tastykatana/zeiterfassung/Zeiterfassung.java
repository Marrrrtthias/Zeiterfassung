package de.tastykatana.zeiterfassung;

import android.util.Log;

import org.joda.time.DateTime;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by matthias on 08.02.17.
 */

public class Zeiterfassung {
    private DateTime runningSince;    // when was the zeiterfassung last started? null if not running
    private Map<DateTime, List<WorkSession>> sessionsMap;

    public Zeiterfassung(long runningSinceMillis) {
        if (runningSinceMillis == 0) {
            runningSince = null;
        } else {
            runningSince = new DateTime(runningSinceMillis);
        }
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
        Log.d("zeiterfassung", "zeiterfassung started at " + DateTime.now().toString());
    }

    /**
     * stops the Zeiterfassung and archives the WorkSession, does nothing if not running
     */
    public void stop() {
        if (runningSince == null) {
            return;
        }

        if (sessionsMap.containsKey(getDayStart(runningSince))) {
            sessionsMap.get(getDayStart(runningSince)).add(new WorkSession(runningSince, DateTime.now()));
        } else {
            List<WorkSession> list = new LinkedList<>();
            list.add(new WorkSession(runningSince, DateTime.now()));
            sessionsMap.put(getDayStart(runningSince), list);
        }
        runningSince = null;
        Log.d("zeiterfassung", "zeiterfassung stopped at " + DateTime.now().toString());
    }

    private DateTime getDayStart(DateTime dt) {
        return dt.withMillisOfDay(0);
    }

    public DateTime getRunningSince() {
        return runningSince;
    }
}
