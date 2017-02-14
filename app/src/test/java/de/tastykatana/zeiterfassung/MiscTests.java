package de.tastykatana.zeiterfassung;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
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
    public void addSession_isCorrect() throws Exception {
        DateTime start = DateTime.now();
        Thread.sleep(1000);
        DateTime end = DateTime.now();
        WorkSession session1 = new WorkSession(start, end);
        Workday workday = new Workday(session1);

        start = DateTime.now();
        Thread.sleep(3000);
        end = DateTime.now();
        WorkSession session2 = new WorkSession(start, end);

        workday.addSession(session2);

        System.out.println("workday duration: " + workday.getDuration().getMillis());
        System.out.println("session1 duration: " + session1.getDuration().getMillis());
        System.out.println("session2 duration: " + session2.getDuration().getMillis());
        System.out.println("sum of both sessions: " + session1.getDuration().plus(session2.getDuration()).getMillis());

        assertTrue(workday.getDuration().equals(session1.getDuration().plus(session2.getDuration())));
    }
}