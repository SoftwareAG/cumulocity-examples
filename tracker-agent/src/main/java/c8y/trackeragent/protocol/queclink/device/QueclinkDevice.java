package c8y.trackeragent.protocol.queclink.device;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

import c8y.Hardware;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.device.ManagedObjectCache;
import c8y.trackeragent.device.TrackerDevice;

public class QueclinkDevice {
   
    protected final String model = "Queclink";
    
    private TrackerAgent trackerAgent;
    private TrackerDevice trackerDevice;
    private ManagedObjectRepresentation representation;
    private String serialNumber; 
    
    public void setTrackerAgent(TrackerAgent trackerAgent) {
        this.trackerAgent = trackerAgent;
    }
    
    public QueclinkDevice create(String imei) {
        
        if (ManagedObjectCache.instance().get(imei) == null) { //initial setting

            this.serialNumber = imei;
            trackerAgent.getOrCreateTrackerDevice(imei);
            
            //update managed object representation
            representation = trackerDevice.getManagedObject();
            setMoRepresentationType(representation);
            setMoRepresentationHardware(representation);
            
            //update device (inventory)
            trackerDevice.updateIfExists(representation, trackerDevice.getAgentId());
            
        } else {
            trackerDevice = trackerAgent.getOrCreateTrackerDevice(imei);
        }
        
        return this;
    }
    
    private void setMoRepresentationType(ManagedObjectRepresentation representation) {
        representation.setType(configureType());
    }
    
    private void setMoRepresentationHardware(ManagedObjectRepresentation representation) {
        Hardware queclink_hardware = configureHardware();
        representation.set(queclink_hardware);
    }
    
    protected String configureType() {
        // Type is configured from derived classes
        return null;
    } 
    
    protected Hardware configureHardware() {
        // Hardware revision is configured from derived classes
        Hardware hardware = new Hardware();
        hardware.setSerialNumber(serialNumber);
        hardware.setModel(model);
        return hardware;
    }
    
}
