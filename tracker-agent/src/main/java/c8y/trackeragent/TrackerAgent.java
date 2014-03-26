package c8y.trackeragent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.exception.UnknownTenantException;
import c8y.trackeragent.utils.TrackerContext;

import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;

public class TrackerAgent {

    private static final Logger logger = LoggerFactory.getLogger(TrackerAgent.class);
    
    private final TrackerContext trackerContext;

    public TrackerAgent(TrackerContext trackerContext) {
        this.trackerContext = trackerContext;
    }
    
    public TrackerDevice getOrCreateTrackerDevice(String imei) throws SDKException {
        TrackerDevice device = ManagedObjectCache.instance().get(imei);
        if (device == null) {
            TrackerPlatform platform = trackerContext.getDevicePlatform(imei);
            ManagedObjectRepresentation agent = trackerContext.getOrCreateAgent(platform.getTenantId());
            device = new TrackerDevice(platform, agent.getId(), imei);
            ManagedObjectCache.instance().put(device);
        }
        return device;
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
        
    private DeviceControlApi getDeviceControlApi(String deviceImei) {
        return trackerContext.getDevicePlatform(deviceImei).getDeviceControlApi();
    }
}
