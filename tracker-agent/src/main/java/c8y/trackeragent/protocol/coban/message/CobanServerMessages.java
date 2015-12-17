package c8y.trackeragent.protocol.coban.message;

import c8y.trackeragent.protocol.coban.CobanConstants;
import c8y.trackeragent.utils.message.TrackerMessage;
import c8y.trackeragent.utils.message.TrackerMessageFactory;

public class CobanServerMessages extends TrackerMessageFactory {
    
    private static final String IMEI_PREFIX = "imei:";

    public CobanServerMessages() {
        super(CobanConstants.FIELD_SEP, "" + CobanConstants.REPORT_SEP);
    }
    
    public TrackerMessage load() {
        return msg().appendField("LOAD");
    }
    
    public TrackerMessage on() {
        return msg().appendField("ON");
    }
    
    public TrackerMessage timeIntervalLocationRequest(String imei, String interval) {
        return msg().appendField("**").appendField(imeiMsg(imei)).appendField("C").appendField(interval);
    }
    
    public static String imeiMsg(String imei) {
        return IMEI_PREFIX + imei;
    }
    
    public static String extractImeiValue(String imeiPart) {
        return imeiPart.replaceFirst(IMEI_PREFIX, "");
    }


    

}
