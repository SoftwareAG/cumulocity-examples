package c8y.trackeragent.protocol.queclink.device;

import c8y.trackeragent.TrackerAgent;

public class GL200Device extends QueclinkDevice {

    private String revision = "gl200";
    
    @Override
    public String configureType() {
        String type = model + "_" + revision;
        return type;
    }
}
