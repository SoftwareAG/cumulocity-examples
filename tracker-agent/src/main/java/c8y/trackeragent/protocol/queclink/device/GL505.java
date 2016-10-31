package c8y.trackeragent.protocol.queclink.device;

public class GL505 extends BaseQueclinkDevice {

    public static final String motionTemplate = "AT+GTGBC=%s,,,,,,,,,,,,,,,%d,,,,,,,,%04x$";
    public static final String motionWithReportIntervalTemplate = "AT+GTGBC=%s,,,,,,,,,,,,,%d,,%d,,,,,,,,%04x$";
    
    public static final String nonMovementIntervalTemplate = "AT+GTNMD=%s,%s,,,,%d,,,,%04x$";
    
    /**
     * Bitmask for non-movement report interval:
     * Report message when it detects non movement: 2
     * Report message when it detects movement: 4 
     * Change the fix interval and send interval of FRI to <rest fix interval> and <rest send interval> when it detects non-movement: 8
     */
    public static final String BITMASK_MODENOMOTION = "E"; // 2 + 4 + 8
    
    @Override
    public String configureMotionTrackingCommand(String password, boolean isActive, short serialNumber) {
        return String.format(motionTemplate,
                password,
                isActive ? 1 : 0, serialNumber);  
    }
    
    @Override
    public String configureMotionTrackingCommand(String password, boolean isActive, int intervalInSeconds, short serialNumber) {
        
        int intervalInMins = intervalInSeconds / 60;
        return String.format(motionWithReportIntervalTemplate,
                password,
                intervalInMins,
                isActive ? 1 : 0, serialNumber); 
    }

    @Override
    public String configureTrackingCommand(String password, int intervalInSeconds, short serialNumber) {
        
        int intervalInMins = intervalInSeconds / 60;
        return String.format(nonMovementIntervalTemplate, 
                password, BITMASK_MODENOMOTION, 
                intervalInMins, serialNumber);
    }
    
}
