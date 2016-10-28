package c8y.trackeragent.protocol.queclink.device;

public class GL500 {
    
    public String motionTemplate = "AT+GTGBC=%s,,,,,,,,,,,,,,,%d,,,,,,,%04x$";
    public String motionTemplateWithReportInterval = "AT+GTGBC=%s,,,,,,,,,,,,,%s,,%d,,,,,,,%04x$";
    
    public String configureMotionCommand(String password, boolean isActive, short serialNumber) {
        return String.format(motionTemplate,
                password,
                isActive ? 1 : 0, serialNumber);  
    }
    
    public String configureMotionCommand(String password, boolean isActive, String interval, short serialNumber) {
        return String.format(motionTemplateWithReportInterval,
                password,
                interval,
                isActive ? 1 : 0, serialNumber); 
    }
}
