package c8y.trackeragent.protocol.queclink.device;

public class GL505Device extends QueclinkDevice {
    
    private String revision = "gl505";
    
    @Override
    public String configureType() {
        String type = model + "_" + revision;
        return type;
    }
}
