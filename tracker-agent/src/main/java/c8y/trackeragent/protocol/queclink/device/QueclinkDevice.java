package c8y.trackeragent.protocol.queclink.device;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

import c8y.Hardware;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.ManagedObjectCache;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.queclink.QueclinkConstants;

public class QueclinkDevice {
    
    protected final String model = "Queclink";
    private GL200 gl200 = new GL200();
    private GL300 gl300 = new GL300();
    private GL500 gl500 = new GL500();
    private GL505 gl505 = new GL505();
    
    public GL200 getGL200() {
        return gl200;
    }
    public GL300 getGL300() {
        return gl300;
    }
    public GL500 getGL500() {
        return gl500;
    }
    public GL505 getGL505() {
        return gl505;
    }
    
    public BaseQueclinkDevice getDeviceByType(String type) {
        if (type.toLowerCase().equals("queclink_gl200")) {
            return getGL200();
        } else if (type.toLowerCase().equals("queclink_gl300")) {
            return getGL300();
        } else if (type.toLowerCase().equals("queclink_gl500")) {
            return getGL500();
        } else if (type.toLowerCase().equals("queclink_gl505")) {
            return getGL505();
        } 
        return null;
    }
    private Logger logger = LoggerFactory.getLogger(QueclinkDevice.class);
            
    

    public String convertDeviceTypeToQueclinkType(String deviceType) {
       return "queclink_" + QueclinkConstants.queclinkProperties.get(deviceType)[0];
    }
    
    public TrackerDevice getOrUpdateTrackerDevice (TrackerAgent trackerAgent, String protocolVersion, String imei) {
        
        String type = new String();
        String revision = new String();
        String password = new String();
        
        if (getDeviceProtocolType(protocolVersion) != null) {
            type = getDeviceProtocolType(protocolVersion);
            revision = getRevision(protocolVersion);
            password = getDevicePassword(protocolVersion);
        }
        
        TrackerDevice trackerDevice = trackerAgent.getOrCreateTrackerDevice(imei);
        ManagedObjectRepresentation representation = trackerDevice.getManagedObject();
        
        logger.debug("representation type {}, configured type {}" , representation.getType(), configureType(type));
        
        if (representation.getType() == null || 
                !representation.getType().equals(configureType(type)) ||
                representation.get(Hardware.class) == null || 
                !representation.get(Hardware.class).getRevision().equals(revision))
        { 
            
            // update managed object representation
            setMoRepresentationType(representation, type);
            setMoRepresentationHardware(representation, imei, type, revision);
            
            representation.setLastUpdatedDateTime(null);
            
            // update device (inventory)
            trackerDevice.updateMoOfDevice(representation, trackerDevice.getGId());
            
            logger.debug("Device MO updated: {}", representation);

        }
        
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

    public ManagedObjectRepresentation getManagedObjectFromGId(GId id) {
         TrackerDevice trackerDevice = ManagedObjectCache.instance().get(id);
         ManagedObjectRepresentation mo = trackerDevice.getManagedObject();
        return mo;    
    }
    
    private void setMoRepresentationType(ManagedObjectRepresentation representation, String type) {
        representation.setType(configureType(type));
    }
    
    private void setMoRepresentationHardware(ManagedObjectRepresentation representation, String serialNumber, String deviceType, String revision) {
        Hardware queclink_hardware = configureHardware(serialNumber, deviceType, revision);
        representation.set(queclink_hardware);
    }
    
    protected String configureType(String type) {
        return model.toLowerCase() + "_" + type;
    } 
    
    protected Hardware configureHardware(String serialNumber, String deviceType, String revision) {
        Hardware hardware = new Hardware();
        hardware.setSerialNumber(serialNumber);
        String model = this.model.toUpperCase() + " " + deviceType.toUpperCase();
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
