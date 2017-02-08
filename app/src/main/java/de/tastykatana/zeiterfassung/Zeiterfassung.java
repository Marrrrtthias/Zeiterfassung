package de.tastykatana.zeiterfassung;

import org.joda.time.DateTime;

/**
 * Created by matthias on 08.02.17.
 */

public class Zeiterfassung {
    private DateTime runningSince;    // when was the zeiterfassung last started? null if not running

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
    }

    /**
     * stops the Zeiterfassung and archives the WorkSession, does nothing if not running
     */
    public void stop() {
        // TODO archive Session
    }

    public DateTime getRunningSince() {
        return runningSince;
    }
}
