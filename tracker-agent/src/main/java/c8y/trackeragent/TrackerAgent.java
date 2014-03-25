package c8y.trackeragent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.model.Agent;
import com.cumulocity.model.ID;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;

public class TrackerAgent {

    private static final Logger logger = LoggerFactory.getLogger(TrackerAgent.class);
    
    private final TrackerContext trackerContext;
    private final Map<String, ManagedObjectRepresentation> tenantToAgent = new HashMap<>();
    private final Map<String, String> imeiToTenantId = new HashMap<>();

    public TrackerAgent(TrackerContext trackerContext) {
        this.trackerContext = trackerContext;
    }
    
    public TrackerDevice getOrCreateTrackerDevice(String imei) throws SDKException {
        TrackerDevice device = ManagedObjectCache.instance().get(imei);
        if (device == null) {
            String tenantId = getTenantId(imei);
            if(tenantId == null) {
                throw new UnknownTenantException(imei);
            }
            device = getOrCreateTrackerDevice(tenantId, imei);
            ManagedObjectCache.instance().put(device);
        }
        return device;
    }

    protected TrackerDevice getOrCreateTrackerDevice(String tenantId, String imei) {
        TrackerPlatform platform = trackerContext.getPlatform(tenantId);
        ManagedObjectRepresentation agent = getOrCreateAgent(tenantId);
        return new TrackerDevice(platform, agent.getId(), imei);
    }

    public void finish(String deviceImei, OperationRepresentation operation) throws UnknownTenantException {
        operation.setStatus(OperationStatus.SUCCESSFUL.toString());
        getDeviceControlApi(deviceImei).update(operation);
    }

    public void fail(String deviceImei, OperationRepresentation operation, String text, SDKException ex) {
        operation.setStatus(OperationStatus.FAILED.toString());
        operation.setFailureReason(text + " " + ex.getMessage());
        getDeviceControlApi(deviceImei).update(operation);
    }
    
    public ManagedObjectRepresentation getOrCreateAgent(String tenantId) {
        ManagedObjectRepresentation agent = tenantToAgent.get(tenantId);
        if(agent == null) {
            agent = createOrUpdateAgent(tenantId);
            logger.info("Agent for tenantId {} specified {}.", tenantId, agent.getId());
            tenantToAgent.put(tenantId, agent);
        }
        return agent;
    }
    
    private String getTenantId(String imei) {
        String tenantId = imeiToTenantId.get(imei);
        if(tenantId != null) {
            return tenantId;
        }
        tenantId = discoverTenantId(imei, tenantId);
        if(tenantId != null) {
            imeiToTenantId.put(imei, tenantId);
            logger.info("Imei {} associated to tenant {}.", imei, tenantId);
        }
        return tenantId;
    }

    private String discoverTenantId(String imei, String tenantId) {
        Collection<TrackerPlatform> platforms = trackerContext.getPlatforms();
        for (TrackerPlatform platform : platforms) {
            DeviceManagedObject deviceManagedObject = new DeviceManagedObject(platform);
            if(deviceManagedObject.existsDevice(imei)) {
                return platform.getTenantId();
            }            
        }
        return null;
    }

    private ManagedObjectRepresentation createOrUpdateAgent(String tenantId) throws SDKException {
        DeviceManagedObject deviceManagedObject = new DeviceManagedObject(trackerContext.getPlatform(tenantId));
        ManagedObjectRepresentation agentMo = new ManagedObjectRepresentation();
        agentMo.setType("c8y_TrackerAgent");
        agentMo.setName("Tracker agent");
        agentMo.set(new Agent());
        ID extId = new ID("c8y_TrackerAgent");
        extId.setType("c8y_ServerSideAgent");
        deviceManagedObject.createOrUpdate(agentMo, extId, null);
        return agentMo;
    }
    
    private DeviceControlApi getDeviceControlApi(String deviceImei) {
        String tenantId = getTenantId(deviceImei);
        if(tenantId == null) {
            throw new UnknownTenantException(deviceImei);
        }
        DeviceControlApi deviceControlApi = trackerContext.getPlatform(tenantId).getDeviceControlApi();
        return deviceControlApi;
    }


}
