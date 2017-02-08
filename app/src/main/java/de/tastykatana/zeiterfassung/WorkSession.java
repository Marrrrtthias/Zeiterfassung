package de.tastykatana.zeiterfassung;

import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * Created by matthias on 08.02.17.
 */

public class WorkSession {
    private DateTime start;
    private DateTime end;

    public WorkSession(DateTime end, DateTime start) {
        this.end = end;
        this.start = start;
    }

    public Duration getDuration() {
        return new Duration(start, end);
    }

    public DateTime getStart() {
        return start;
    }

    public DateTime getEnd() {
        return end;
    }

}
