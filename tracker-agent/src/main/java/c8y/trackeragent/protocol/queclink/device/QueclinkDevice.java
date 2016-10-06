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
    
    public void setTrackerAgent(TrackerAgent trackerAgent) {
        this.trackerAgent = trackerAgent;
    }
    
    public String convertDeviceTypeToQueclinkType(String deviceType) {
       return "queclink_" + QueclinkConstants.queclinkProperties.get(deviceType)[0];
    }
    
    public TrackerDevice getOrUpdateTrackerDevice (String protocolVersion, String imei) {
        
        String type = new String();
        String revision = new String();
        String password = new String();
        
        if (getDeviceProtocolType(protocolVersion) != null) {
            type = getDeviceProtocolType(protocolVersion);
            revision = getRevision(protocolVersion);
            password = getDevicePassword(protocolVersion);
        }
        
        trackerDevice = trackerAgent.getOrCreateTrackerDevice(imei);
        ManagedObjectRepresentation representation = trackerDevice.getManagedObject();
        
        logger.info("representation type {}, configured type {}" , representation.getType(), configureType(type));
        
        if (representation.getType() == null || 
                !representation.getType().equals(configureType(type)) ||
                representation.get(Hardware.class) == null || 
                !representation.get(Hardware.class).getRevision().equals(revision))
        { 
            
            // update managed object representation
            setMoRepresentationType(representation, type);
            setMoRepresentationHardware(representation, imei, revision);
            
            if(representation.get(password) == null) {
                setMoRepresentationPassword(representation, password);
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
    
    public ManagedObjectRepresentation getManagedObjectFromGId(GId id) {

        return trackerDevice.getManagedObject(id);    
    }

    private void setMoRepresentationType(ManagedObjectRepresentation representation, String type) {
        representation.setType(configureType(type));
    }
    
    private void setMoRepresentationHardware(ManagedObjectRepresentation representation, String serialNumber, String revision) {
        Hardware queclink_hardware = configureHardware(serialNumber, revision);
        representation.set(queclink_hardware);
    }
    
    private void setMoRepresentationPassword(ManagedObjectRepresentation representation, String password) {
        representation.setProperty("password", password);
    }
    
    protected String configureType(String type) {
        return model.toLowerCase() + "_" + type;
    } 
    
    protected Hardware configureHardware(String serialNumber, String revision) {
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
