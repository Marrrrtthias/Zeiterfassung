package de.tastykatana.zeiterfassung;

import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * Created by matthias on 2/16/17.
 */

public class PeriodFormatters {
    public static final PeriodFormatter HOURS_AND_Minutes = new PeriodFormatterBuilder()
            .printZeroAlways()
            .appendHours()
            .appendSeparator(":")
            .appendMinutes()
            .toFormatter();
}
