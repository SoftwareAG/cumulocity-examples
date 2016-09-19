package c8y.trackeragent.protocol.queclink.device;

import c8y.Hardware;

public class GL505Device extends QueclinkDevice {
    
    private String revision = "gl505";
    
    @Override
    public String configureType() {
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
