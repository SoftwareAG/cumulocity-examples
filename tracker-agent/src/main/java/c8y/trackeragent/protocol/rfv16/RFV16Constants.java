package c8y.trackeragent.protocol.rfv16;




public class RFV16Constants {

    public static final String REPORT_PREFIX = "*";
    
    public static final String DEFAULT_MAKER = "HQ";
    
    public static final String DATE_EFFECTIVE_MARK = "A";
    public static final String DATE_EFFECTIVE_INVALID_MARK = "V";
    public static final String MESSAGE_TYPE_V1 = "V1";
    public static final String MESSAGE_TYPE_V4 = "V4";
    public static final String MESSAGE_TYPE_LINK = "LINK";
    public static final String MESSAGE_TYPE_MULTI_BASE_STATION_DATA = "NBR";
    
    public static final String CONNECTION_PARAM_CONTROL_COMMANDS_SENT = "CONTROL_COMMANDS_SENT";
    
    public static final String DEVICE_PARAM_OPERATION_IN_EXECUTION = "OPERATIONS_IN_EXECUTION";
    
    
    public static final String DEVICE_CONFIG_KEY_LOCATION_REPORT_TIME_INTERVAL = "locationReportTimeInterval";
    public static final Integer DEFAULT_LOCATION_REPORT_INTERVAL = 180;
    
    public static final String COMMAND_DISPLAY_DEVICE_SITUATION = "CK";
    public static final String COMMAND_POSITION_MONITORING = "D1";
    public static final String COMMAND_ARM_DISARM_ALARM = "SCF";
    public static final String COMMAND_RESTART = "R1";
    public static final String COMMAND_SINGLE_LOCATION = "LOC";
    public static final String COMMAND_SET_SOS_NUMBER = "S8";
    

}
