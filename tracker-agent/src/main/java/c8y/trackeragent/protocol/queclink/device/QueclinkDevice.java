package c8y.trackeragent.protocol.queclink.device;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.device.ManagedObjectCache;
import c8y.trackeragent.device.TrackerDevice;

public class QueclinkDevice {
   
    private TrackerAgent trackerAgent;
    private TrackerDevice trackerDevice;
    private ManagedObjectRepresentation representation;
    protected final String model = "queclink";

    public void setTrackerAgent(TrackerAgent trackerAgent) {
        this.trackerAgent = trackerAgent;
    }
    
    public QueclinkDevice create(String imei) {
        
        if (ManagedObjectCache.instance().get(imei) == null) { //initial setting

            trackerAgent.getOrCreateTrackerDevice(imei);
            //update managed object representation
            representation = trackerDevice.getManagedObject();
            representation.setType(this.configureType());
            //update device (inventory)
            trackerDevice.updateIfExists(representation, trackerDevice.getAgentId());
            
        } else {
            trackerDevice = trackerAgent.getOrCreateTrackerDevice(imei);
        }
        
        return this;
    }
    
    public String configureType() {
        return model;
    } 
    
}
