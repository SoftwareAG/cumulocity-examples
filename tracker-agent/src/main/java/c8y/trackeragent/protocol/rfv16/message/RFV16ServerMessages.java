package c8y.trackeragent.protocol.rfv16.message;

import static c8y.trackeragent.protocol.rfv16.RFV16Constants.COMMAND_ARM_DISARM_ALARM;
import static c8y.trackeragent.protocol.rfv16.RFV16Constants.COMMAND_DISPLAY_DEVICE_SITUATION;
import static c8y.trackeragent.protocol.rfv16.RFV16Constants.COMMAND_POSITION_MONITORING;
import static c8y.trackeragent.protocol.rfv16.RFV16Constants.COMMAND_RESTART;
import static c8y.trackeragent.protocol.rfv16.RFV16Constants.COMMAND_SET_SOS_NUMBER;
import static c8y.trackeragent.protocol.rfv16.RFV16Constants.COMMAND_SINGLE_LOCATION;
import static c8y.trackeragent.protocol.rfv16.RFV16Constants.DEFAULT_MAKER;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.protocol.rfv16.RFV16Constants;
import c8y.trackeragent.utils.message.TrackerMessage;
import c8y.trackeragent.utils.message.TrackerMessageFactory;

@Component
public class RFV16ServerMessages extends TrackerMessageFactory<TrackerMessage> {
    
    public static final DateTimeFormatter HHMMSS = DateTimeFormat.forPattern("hhmmss");
    public static final DateTimeFormatter DDMMYY = DateTimeFormat.forPattern("ddMMyy");
    
    @Override
    public TrackerMessage msg() {
        return new TrackerMessage(TrackingProtocol.RFV16, RFV16Constants.REPORT_PREFIX);
    }

    
    /**
     * *XX,YYYYYYYYYY,D1,HHMMSS,interval,batch#
     *
     * Interval: The interval time of upload data to server, value range:1 ~ 65535 second.
     * Batch: The number of transferred batch, range from 1 to16, outside the range as 1.
     * When batch=1 as the real-time mode, when produce a record upload it, only support batch=1.
     */
    public TrackerMessage reportMonitoringCommand(String imei, String intervalInSeconds) {
        return msg()
                .appendField(DEFAULT_MAKER)
                .appendField(imei)
                .appendField(COMMAND_POSITION_MONITORING)
                .appendField(currTime())
                .appendField(intervalInSeconds)
                .appendField(1);                
    }
    
    /**
     * The device will wake up the GPS, and search the GPS for delayInSeconds, and then up load the location data to server.
     */
    public TrackerMessage singleLocationCommand(String imei, String delayInSeconds) {
	return msg()
		.appendField(DEFAULT_MAKER)
		.appendField(imei)
		.appendField(COMMAND_SINGLE_LOCATION)
		.appendField(currTime())
		.appendField(delayInSeconds);
    }
    
    public TrackerMessage restartCommand(String imei) {
    	return msg()
    		.appendField(DEFAULT_MAKER)
    		.appendField(imei)
    		.appendField(COMMAND_RESTART)
    		.appendField(currTime());
    }
    
    public TrackerMessage situationCommand(String imei) {
	return msg()
		.appendField(DEFAULT_MAKER)
		.appendField(imei)
		.appendField(COMMAND_DISPLAY_DEVICE_SITUATION)
		.appendField(currTime());
    }
    
    public TrackerMessage setSosNumberCommand(String imei, String phoneNumber) {
	return msg()
		.appendField(DEFAULT_MAKER)
		.appendField(imei)
		.appendField(COMMAND_SET_SOS_NUMBER)
		.appendField(currTime())
		.appendField(phoneNumber)
		.appendField("")
		.appendField("");
    }
    
    public TrackerMessage armAlarm(String imei, String flag) {
	return msg()
		.appendField(DEFAULT_MAKER)
		.appendField(imei)
		.appendField(COMMAND_ARM_DISARM_ALARM)
		.appendField(currTime())
		.appendField(0)
		.appendField(flag);                
    }

    protected String currTime() {
        return HHMMSS.print(new DateTime());
    }
    
    protected String currDate() {
        return DDMMYY.print(new DateTime());
    }
    
}
