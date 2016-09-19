package c8y.trackeragent.protocol.queclink.device;

import c8y.Hardware;
import c8y.trackeragent.TrackerAgent;

public class GL200Device extends QueclinkDevice {

    private String revision = "gl200";
    
    @Override
    protected String configureType() {
        String type = model.toLowerCase() + "_" + revision;
        return type;
    }
    
    @Override
    protected Hardware configureHardware() {
        
        Hardware hardware = super.configureHardware();
        hardware.setRevision(revision);
        
        return hardware;
    }
    
}
