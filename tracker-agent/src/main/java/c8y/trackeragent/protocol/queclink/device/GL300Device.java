package c8y.trackeragent.protocol.queclink.device;

public class GL300Device extends QueclinkDevice{

    private String revision = "gl300";
    
    @Override
    public String configureType() {
        String type = model + "_" + revision;
        return type;
    }
    
}
