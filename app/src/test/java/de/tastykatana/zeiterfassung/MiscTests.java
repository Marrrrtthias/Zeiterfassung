package de.tastykatana.zeiterfassung;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MiscTests {
    @Test
    public void getStartOfDay_isCorrect() throws Exception {
        assertEquals("might fail because of changed date", DateTime.now().withMillisOfDay(0).toString(), "2017-02-14T00:00:00.000+01:00");
    }

    @Test
    public void getEndOfDay_isCorrect() throws Exception {
        assertEquals("might fail because of changed date", DateTime.now().withMillisOfDay(DateTimeConstants.MILLIS_PER_DAY-1).toString(), "2017-02-14T23:59:59.999+01:00");
    }

    @Test
    public void workday_isCorrect() throws Exception {
        DateTime start = DateTime.now();
        Thread.sleep(1000);
        DateTime end = DateTime.now();
        WorkSession session1 = new WorkSession(start, end);
        Workday workday = new Workday(session1);

        assertEquals(workday.getStart(), session1.getStart());
        assertEquals(workday.getEnd(), session1.getEnd());
        assertEquals(workday.getDuration(), session1.getDuration());

        start = DateTime.now();
        Thread.sleep(3000);
        end = DateTime.now();
        WorkSession session2 = new WorkSession(start, end);

        // session2 is on same day as workday, should be added to it
        assertTrue(workday.addSession(session2));

        WorkSession session3 = new WorkSession(DateTime.now().plusDays(2), DateTime.now().plusDays(3));
        // session 3 is not on the same day as workday; should not be added
        assertFalse(workday.addSession(session3));

        System.out.println("workday duration: " + workday.getDuration().getMillis());
        System.out.println("session1 duration: " + session1.getDuration().getMillis());
        System.out.println("session2 duration: " + session2.getDuration().getMillis());
        System.out.println("sum of both sessions: " + session1.getDuration().plus(session2.getDuration()).getMillis());

        assertEquals(workday.getDuration(), session1.getDuration().plus(session2.getDuration()));
        assertEquals(workday.getStart(), session1.getStart());
        assertEquals(workday.getEnd(), session2.getEnd());
    }
}