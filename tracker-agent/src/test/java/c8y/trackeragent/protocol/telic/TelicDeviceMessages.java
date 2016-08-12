package c8y.trackeragent.protocol.telic;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.cumulocity.model.DateTimeConverter;

import c8y.Position;
import c8y.trackeragent.utils.message.TrackerMessage;
import c8y.trackeragent.utils.message.TrackerMessageFactory;

public class TelicDeviceMessages extends TrackerMessageFactory<TrackerMessage> {
    
    private static final String LOG_TIMESTAMP_STR = "020216141710";
    private static final String GPS_TIMESTAMPS_STR = "020216141711";
    public static final DateTime LOG_TIMESTAMP = TelicConstants.TIMESTAMP_FORMATTER.parseDateTime(LOG_TIMESTAMP_STR);
    public static final DateTime GPS_TIMESTAMP = TelicConstants.TIMESTAMP_FORMATTER.parseDateTime(GPS_TIMESTAMPS_STR);
    public static final String LOG_TIMESTAMP_C8Y_STR = DateTimeConverter.date2String(LOG_TIMESTAMP);
    public static final String GPS_TIMESTAMP_C8Y_STR = DateTimeConverter.date2String(GPS_TIMESTAMP);
    
    @Override
    public TrackerMessage msg() {
        return new TelicMessage(TelicConstants.FIELD_SEP, "" + TelicConstants.REPORT_SEP);
    }
    
    public TrackerMessage positionUpdate(String imei, Position position, String eventCode) {
        // @formatter:off
        TrackerMessage msg = msg()
                .appendField("0721" + imei + eventCode)
                .appendField(LOG_TIMESTAMP_STR) //Log Timestamp
                .appendField("0")
                .appendField(GPS_TIMESTAMPS_STR) //GPS Timestamp
                .appendField(asTelicStringCoord(position.getLng()))
                .appendField(asTelicStringCoord(position.getLat()))
                .appendField("3") //Fix type
                .appendField("4") //Speed
                .appendField("67")  //Course
                .appendField("4")   //Satellites for calculation 
                .appendField("")    //HDOP 
                .appendField("")    //VDOP
                .appendField(asTelicStringCoord(position.getAlt())) //Altitude
                .appendField("11032") //mileage
                .appendField("")
                .appendField("0100") //digital input status
                .appendField("238") //digital output status
                .appendField("211")   //analog input 1
                .appendField("0")   //analog input 2
                .appendField("0")  //analog input 3
                .appendField("0109");   // Device Id
        // @formatter:on

        if (position.getAccuracy() != null) { // Extended Data mode
            msg.appendField("1294"); // Motion Sensor stationary time
            msg.appendField(position.getAccuracy().intValue()); // GPS Accuracy Estimation
        }

        return msg;
    }
    
    public TrackerMessage positionUpdate(String imei, Position position) {
        return positionUpdate(imei, position, "99");
    }
    
    private static String asTelicStringCoord(BigDecimal coord) {
        return coord.toString().replaceAll("\\.", "");
    }
    
    

}
