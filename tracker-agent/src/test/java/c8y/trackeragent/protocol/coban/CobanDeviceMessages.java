package c8y.trackeragent.protocol.coban;

import static c8y.trackeragent.utils.SignedLocation.altitude;
import static c8y.trackeragent.utils.SignedLocation.latitude;
import static c8y.trackeragent.utils.SignedLocation.longitude;
import static java.lang.String.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.Position;
import c8y.trackeragent.utils.DeviceMessage;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.SignedLocation;

public class CobanDeviceMessages {
    
    private static final Logger logger = LoggerFactory.getLogger(CobanDeviceMessages.class);
    
    private static final String LOGON = "##,imei:%s,A;";
    private static final String HEARTBEAT = "%s;";
    private static final String POSITION_UPDATE = "imei:%s,001,0809231929,,F,055403.000,A,%s,%s,%s,%s,%s,,;";
    
    public static DeviceMessage logon(final String imei) {
        String msg = formatMessage(LOGON, imei);
        return deviceMessage(msg);
    }
    
    public static DeviceMessage heartbeat(String imei) {
        String msg = formatMessage(HEARTBEAT, imei);
        return deviceMessage(msg);
    }
    
    public static DeviceMessage positionUpdate(String imei, Position position) {
        SignedLocation lat = latitude().withValue(position.getLat());
        SignedLocation lng = longitude().withValue(position.getLng());
        SignedLocation alt = altitude().withValue(position.getAlt());
        String format = POSITION_UPDATE;
        String msg = formatMessage(format, imei, lat.getAbsValue(), lat.getSymbol(), lng.getAbsValue(), lng.getSymbol(), alt.getAbsValue());
        return deviceMessage(msg);
    }

    private static String formatMessage(String format, Object... params) {
        String msg = format(format, params);
        logger.info("Message prepared:{}", msg);
        return msg;
    }
    
    private static DeviceMessage deviceMessage(String text) {
        return new DeviceMessage(CobanConstants.FIELD_SEP, "" + CobanConstants.REPORT_SEP, text);
    }
    
    public static void main(String[] args) throws Exception {
        String imei = "12345";
        DeviceMessage logon = CobanDeviceMessages.logon(imei);
        DeviceMessage msg = CobanDeviceMessages.positionUpdate(imei, Positions.SAMPLE_2);
        System.out.println(logon.append(msg));
    }

}
