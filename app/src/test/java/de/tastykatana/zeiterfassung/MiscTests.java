package de.tastykatana.zeiterfassung;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MiscTests {
    @Test
    public void getStartOfDay_isCorrect() throws Exception {
        assertEquals("might fail because of changed date", DateTime.now().withMillisOfDay(0).toString(), "2017-02-08T00:00:00.000+01:00");
    }
}