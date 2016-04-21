package c8y.trackeragent.protocol.mt90g;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class MT90GConstants {
    
    public static final String FIELD_SEP = ",";
    public static final char REPORT_SEP = '\n';
    
    public static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormat.forPattern("yyMMddHHmmss");
    
    public static final String DIRECTION = "direction";

}
