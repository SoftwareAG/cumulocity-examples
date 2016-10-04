package c8y.trackeragent.protocol.queclink.device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

import c8y.Hardware;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.queclink.QueclinkConstants;

public class QueclinkDevice {

    private Logger logger = LoggerFactory.getLogger(QueclinkDevice.class);
            
    protected final String model = "Queclink";
    
    private TrackerAgent trackerAgent;
    private TrackerDevice trackerDevice;
    private ManagedObjectRepresentation representation;
    private String serialNumber; 
    private String type;
    private String revision;
    private String password;
    
    public void setTrackerAgent(TrackerAgent trackerAgent) {
        this.trackerAgent = trackerAgent;
    }
    
    public void setType(String type) {
       this.type = type;
    }
    
    public void setProtocolVersion(String protocolVersion) {
        //set type
        if (getDeviceProtocolType(protocolVersion) != null) {
            this.type = getDeviceProtocolType(protocolVersion);
        } else {
            this.type = getDeviceId(protocolVersion);
        }
        
        //set revision
        this.revision = getRevision(protocolVersion);
        
        //set password
        this.password = getDevicePassword(protocolVersion);
        
    }
    
    public void setRevision(String revision) {
        this.revision = revision;
    }
    
    public TrackerDevice getOrUpdateTrackerDevice (String imei) {
        
        this.serialNumber = imei;
        trackerDevice = trackerAgent.getOrCreateTrackerDevice(imei);
        representation = trackerDevice.getManagedObject();
        
        logger.info("representation type {}, configured type {}" , representation.getType(), configureType());
        if (representation.getType() == null || 
                !representation.getType().equals(configureType()) ||
                representation.get(Hardware.class) == null || 
                !representation.get(Hardware.class).getRevision().equals(revision))
        { 
            
            // update managed object representation
            setMoRepresentationType(representation);
            setMoRepresentationHardware(representation);
            
            if(representation.get(password) == null) {
                setMoRepresentationPassword(representation);
            }
            
            representation.setLastUpdatedDateTime(null);
            
            // update device (inventory)
            logger.info("Agent id: {}", trackerDevice.getAgentId());
            trackerDevice.updateMoOfDevice(representation, trackerDevice.getGId());
            
            logger.info("Device MO updated: {}", representation);

        }
        
        //logger.info("Current MO is: {}", trackerAgent.getOrCreateTrackerDevice(imei).getManagedObject());
        
        return trackerDevice;
    }
    
    public String fetchProtocolFromType(String type) {
        String protocolName = type.substring(9);
        return protocolName;
    }
    
    public String getDevicePassword(String protocolVersion) {
        String key = getDeviceId(protocolVersion);
        if(QueclinkConstants.queclinkProperties.get(key) == null) {
            return "";
        }
        return QueclinkConstants.queclinkProperties.get(key)[0];
    }

    
    public String getDevicePasswordFromGId(GId id) {
        
        ManagedObjectRepresentation mo = trackerDevice.getManagedObject(id);
        return (String) mo.get("password");    
    }
    
    private void setMoRepresentationType(ManagedObjectRepresentation representation) {
        representation.setType(configureType());
    }
    
    private void setMoRepresentationHardware(ManagedObjectRepresentation representation) {
        Hardware queclink_hardware = configureHardware();
        representation.set(queclink_hardware);
    }
    
    private void setMoRepresentationPassword(ManagedObjectRepresentation representation) {
        representation.setProperty("password", password);
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
    
    private String getDeviceId(String protocolVersion) {
        return protocolVersion.substring(0, 2);
    }
    
    private String getDeviceProtocolType(String protocolVersion) {
        String key = getDeviceId(protocolVersion);
        if(QueclinkConstants.queclinkProperties.get(key) == null) {
            return null;
        }
        return QueclinkConstants.queclinkProperties.get(key)[0];
    }
    
    private String getRevision(String protocolVersion) {
        return protocolVersion.substring(2, 4) + "." + protocolVersion.substring(4, 6);
    }
    
}
