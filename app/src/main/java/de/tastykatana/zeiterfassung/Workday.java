package de.tastykatana.zeiterfassung;

import org.joda.time.DateTime;
import org.joda.time.Duration;

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

    private Workday(DateTime start, DateTime end, Duration duration) {
        this.start = start;
        this.end = end;
        this.duration = duration;
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

    public DateTime getStart() {
        return start;
    }

    public DateTime getEnd() {
        return end;
    }

    public String toFormattedString() {
        return start.toString("dd.MM.yyyy   E") + "\t" + start.toString("HH:mm") + "\t" + end.toString("HH:mm") + "\t" + duration.getStandardHours() + ":" + duration.getStandardMinutes();
    }

    public int getWeekday() {
        return start.getDayOfWeek();
    }

    public String toFormattedString(boolean correctTimes) {
        if (correctTimes) {
            // only return date and weekday if this is a sunday
            if (start.getDayOfWeek() == 7) {
                return start.toString("dd.MM.yyyy   E");
            }

            Workday tunedWorkday;
            // workday has illegal start time
            if (start.getHourOfDay() < 6 || start.getHourOfDay() > 23 || (start.getHourOfDay()==23 && start.getMinuteOfHour()>0)) {
                tunedWorkday = new Workday(start.withHourOfDay(6), start.withHourOfDay(6).plus(duration), duration);
                return tunedWorkday.toFormattedString();
            }
            // workday has illegal end time
            if (end.getHourOfDay() < 6 || end.getHourOfDay() > 23 || (end.getHourOfDay()==23 && end.getMinuteOfHour()>0)) {
                tunedWorkday = new Workday(end.withHourOfDay(22).minus(duration), end.withHourOfDay(22), duration);
                return tunedWorkday.toFormattedString();
            }
        }

        return toFormattedString();
    }
}
