package c8y.trackeragent.protocol.queclink.device;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.measurement.MeasurementApi;

import c8y.Hardware;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.UpdateIntervalProvider;
import c8y.trackeragent.configuration.TrackerConfiguration;
import c8y.trackeragent.device.ManagedObjectCache;
import c8y.trackeragent.device.TrackerDevice;

public class QueclinkDevice {
  

    private Logger logger = LoggerFactory.getLogger(QueclinkDevice.class);
            
    protected final String model = "Queclink";
    
    private TrackerAgent trackerAgent;
    private TrackerDevice trackerDevice;
    private ManagedObjectRepresentation representation;
    private String serialNumber; 
    private String type;
    private String revision;
    
    public void setTrackerAgent(TrackerAgent trackerAgent) {
        this.trackerAgent = trackerAgent;
    }
    
    public void setType(String type) {
       this.type = type;
    }
    
    public void setRevision(String revision) {
        this.revision = revision;
    }
    
    public TrackerDevice getOrUpdateTrackerDevice (String imei) {
        
        this.serialNumber = imei;
        trackerDevice = trackerAgent.getOrCreateTrackerDevice(imei);
        representation = trackerDevice.getManagedObject();
        
        logger.info("representation type {}, configured type {}" , representation.getType(), configureType());
        if (representation.getType() == null || !representation.getType().contains(configureType()) 
                //|| (queclinkDevice.getManagedObjectHardware(imei) == null && 
                //queclinkDevice.getManagedObjectHardware(imei).getRevision() != revision) 
                )
        { 
            
            // initial setting
            
            // update managed object representation
            setMoRepresentationType(representation);
            setMoRepresentationHardware(representation);
            representation.setLastUpdatedDateTime(null);
            
            // update device (inventory)
            logger.info("Agent id: {}", trackerDevice.getAgentId());
            trackerDevice.updateMoOfDevice(representation, trackerDevice.getGId());
            
            logger.info("Device MO updated: {}", representation);

        }
        
        logger.info("Current MO is: {}", trackerAgent.getOrCreateTrackerDevice(imei).getManagedObject());
        
        return trackerDevice;
    }
    
    private void setMoRepresentationType(ManagedObjectRepresentation representation) {
        representation.setType(configureType());
    }
    
    private void setMoRepresentationHardware(ManagedObjectRepresentation representation) {
        Hardware queclink_hardware = configureHardware();
        representation.set(queclink_hardware);
    }
    
    protected String configureType() {
        return model.toLowerCase() + "_" + type;
    } 
    
    protected Hardware configureHardware() {
        Hardware hardware = new Hardware();
        hardware.setSerialNumber(serialNumber);
        hardware.setModel(model);
        hardware.setRevision(revision);
        return hardware;
    }
    
}
