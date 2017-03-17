package c8y.trackeragent.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class QueclinkReports {
    public static DateTime convertEntryToDateTime(String reportDate) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");
        DateTime dateTime = formatter.parseDateTime(reportDate);
        return dateTime;
    }
}
