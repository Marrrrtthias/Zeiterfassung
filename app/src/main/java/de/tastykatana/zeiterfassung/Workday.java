package de.tastykatana.zeiterfassung;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.Collection;

/**
 * Created by matthias on 2/14/17.
 */

public class Workday {
    private DateTime start, end;
    private Duration duration;

    public Workday(WorkSession session) {
        start = session.getStart();
        end = session.getEnd();
        duration = new Duration(start, end);
    }

    /**
     * adds a WorkSession to the Workday (adds duration of the session to Workdays duration and
     * updates start and end if necessary)
     * session won't be added if it's start is on another day than the start of the Workday
     * 
     * @param session Worksession to be added to the Workday
     * @return whether the session was added
     */
    public boolean addSession(WorkSession session) {
        if (!getDayStart(start).equals(getDayStart(session.getStart()))) {
            // session doesn't belong to this Workday => don't addSession it and return false
            return false;
        }
        
        // update start and end of Workday if applicable
        if (session.getStart().isBefore(start)) {
            start = session.getStart();
        }
        if (session.getEnd().isAfter(end)) {
            end = session.getEnd();
        }
        
        // addSession duration of session and return true
        duration = duration.plus(session.getDuration());
        return true;
    }
    
    private DateTime getDayStart(DateTime dt) {
        return dt.withMillisOfDay(0);
    }

    public Duration getDuration() {
        return duration;
    }
}
