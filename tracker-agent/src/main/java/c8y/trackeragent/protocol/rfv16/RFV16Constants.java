package c8y.trackeragent.protocol.rfv16;


public class RFV16Constants {

    public static final String REPORT_PREFIX = "*";
    public static final String FIELD_SEP = ",";
    public static final char REPORT_SEP = '#';
    
    public static final String DATE_EFFECTIVE_MARK = "A";
    public static final String DATE_EFFECTIVE_INVALID = "V";
    public static final String MESSAGE_TYPE_V1 = "V1";
    public static final String MESSAGE_TYPE_V4 = "V4";
    public static final String MESSAGE_TYPE_LINK = "LINK";
    
    public static final String DEVICE_CONFIG_FRAGMENT = "coban_config";
    public static final String DEVICE_CONFIG_KEY_LOCATION_REPORT_TIME_INTERVAL = "locationReportTimeInterval";
    public static final Integer DEFAULT_LOCATION_REPORT_INTERVAL = 180;
    
    public static final String COMMAND_DISPLAY_DEVICE_SITUATION = "CK";
    public static final String COMMAND_POSITION_MONITORING = "D1";
    public static final String COMMAND_TURN_ON_ALL_ALARMS = "SCF";
    
    public static final String CONNECTION_PARAM_CONTROL_COMMANDS_SENT = "CONTROL_COMMANDS_SENT";

}
