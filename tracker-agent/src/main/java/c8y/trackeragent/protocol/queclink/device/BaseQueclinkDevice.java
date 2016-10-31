package c8y.trackeragent.protocol.queclink.device;

public abstract class BaseQueclinkDevice {

    
    public abstract String configureMotionTrackingCommand(String password, boolean isActive, int intervalInSeconds, short serialNumber);
    public abstract String configureMotionTrackingCommand(String password, boolean isActive, short serialNumber);
    public abstract String configureTrackingCommand(String password, int intervalInSeconds, short serialNumber);
    
}
