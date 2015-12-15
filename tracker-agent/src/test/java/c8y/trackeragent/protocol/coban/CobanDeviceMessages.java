package c8y.trackeragent.protocol.coban;

import static c8y.trackeragent.utils.SignedLocation.altitude;
import static c8y.trackeragent.utils.SignedLocation.latitude;
import static c8y.trackeragent.utils.SignedLocation.longitude;
import static java.lang.String.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.Position;
import c8y.trackeragent.protocol.coban.parser.AlarmType;
import c8y.trackeragent.utils.SignedLocation;
import c8y.trackeragent.utils.message.TrackerMessage;
import c8y.trackeragent.utils.message.TrackerMessageFactory;

public class CobanDeviceMessages extends TrackerMessageFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(CobanDeviceMessages.class);
    
    private static final String LOGON = "##,imei:%s,A;";
    private static final String HEARTBEAT = "%s;";
    private static final String POSITION_UPDATE = "imei:%s,tracker,0809231929,,F,055403.000,A,%s,%s,%s,%s,%s,,;";
    private static final String ALARM = "imei:%s,%s,0809231929,,F,055403.000,A,,,,,,,;";
    
    public CobanDeviceMessages() {
        super(CobanConstants.FIELD_SEP, "" + CobanConstants.REPORT_SEP);
    }

    public TrackerMessage logon(final String imei) {
        String msg = formatMessage(LOGON, imei);
        return msg().fromText(msg);
    }
    
    public TrackerMessage heartbeat(String imei) {
        String msg = formatMessage(HEARTBEAT, imei);
        return msg().fromText(msg);
    }
    
    public TrackerMessage positionUpdate(String imei, Position position) {
        SignedLocation lat = latitude().withValue(position.getLat());
        SignedLocation lng = longitude().withValue(position.getLng());
        SignedLocation alt = altitude().withValue(position.getAlt());
        String msg = formatMessage(POSITION_UPDATE, imei, lat.getAbsValue(), lat.getSymbol(), lng.getAbsValue(), lng.getSymbol(), alt.getAbsValue());
        return msg().fromText(msg);
    }
    
    public TrackerMessage alarm(String imei, AlarmType type) {
        String msg = formatMessage(ALARM, imei, type.asCobanType());
        return msg().fromText(msg);
    }

    private static String formatMessage(String format, Object... params) {
        String msg = format(format, params);
        logger.info("Message prepared:{}", msg);
        return msg;
    }

}
