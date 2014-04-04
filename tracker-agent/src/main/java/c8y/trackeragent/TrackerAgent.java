package c8y.trackeragent;

import c8y.trackeragent.event.TrackerAgentEventListener;
import c8y.trackeragent.exception.UnknownTenantException;
import c8y.trackeragent.utils.TrackerContext;

import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.google.common.eventbus.EventBus;

public class TrackerAgent {
    
    private final EventBus eventBus;

    public TrackerAgent() {
        this.eventBus = new EventBus("tracker-agent");
    }

    public TrackerDevice getOrCreateTrackerDevice(String imei) throws SDKException {
        TrackerDevice device = ManagedObjectCache.instance().get(imei);
        if (device == null) {
            TrackerPlatform platform = TrackerContext.get().getDevicePlatform(imei);
            device = new TrackerDevice(platform, platform.getAgentId(), imei);
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
    
    public void registerEventListener(TrackerAgentEventListener... eventListeners) {
        for (Object eventListener : eventListeners) {
            eventBus.register(eventListener);
        }
    }
    
    public void sendEvent(Object event) {
        eventBus.post(event);
    }
        
    private DeviceControlApi getDeviceControlApi(String deviceImei) {
        return TrackerContext.get().getDevicePlatform(deviceImei).getDeviceControlApi();
    }
}
