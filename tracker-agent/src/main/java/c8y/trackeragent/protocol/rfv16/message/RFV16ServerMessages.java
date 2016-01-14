package c8y.trackeragent.protocol.rfv16.message;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import c8y.trackeragent.protocol.rfv16.RFV16Constants;
import c8y.trackeragent.utils.message.TrackerMessage;
import c8y.trackeragent.utils.message.TrackerMessageFactory;

public class RFV16ServerMessages extends TrackerMessageFactory {
    
    private static final DateTimeFormatter HHMMSS = DateTimeFormat.forPattern("hhmmss");
    private static final DateTimeFormatter DDMMYY = DateTimeFormat.forPattern("ddMMyy");
    
    public RFV16ServerMessages() {
        super(RFV16Constants.FIELD_SEP, "" + RFV16Constants.REPORT_SEP, RFV16Constants.REPORT_PREFIX);
    }
    
    /**
     * *XX,YYYYYYYYYY,D1,HHMMSS,interval,batch#
     *
     * Interval: The interval time of upload data to server, value range:1 ~ 65535 second.
     * Batch: The number of transferred batch, range from 1 to16, outside the range as 1.
     * When batch=1 as the real-time mode, when produce a record upload it, only support batch=1.
     */
    public TrackerMessage positioningMonitoringCommand(String maker, String imei, int intervalInSeconds) {
        return msg()
                .appendField(maker)
                .appendField(imei)
                .appendField("D1")
                .appendField(currTime())
                .appendField(intervalInSeconds)
                .appendField(1);                
    }

    protected String currTime() {
        return HHMMSS.print(new DateTime());
    }
    
    protected String currDate() {
        return DDMMYY.print(new DateTime());
    }
    

}
