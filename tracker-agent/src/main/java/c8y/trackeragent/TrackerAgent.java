package c8y.trackeragent;

import c8y.trackeragent.event.TrackerAgentEventListener;
import c8y.trackeragent.exception.UnknownTenantException;
import c8y.trackeragent.utils.TrackerContext;

import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.google.common.eventbus.EventBus;

public class TrackerAgent {
    
    /**
     * @deprecated
     * TODO remove and replace with direct invocations 
     */
    private final EventBus eventBus;
    private final TrackerContext context;

    public TrackerAgent(TrackerContext trackerContext) {
        this.context = trackerContext;
        this.eventBus = new EventBus("tracker-agent");
    }

    public TrackerDevice getOrCreateTrackerDevice(String imei) throws SDKException {
        TrackerDevice device = ManagedObjectCache.instance().get(imei);
        if (device == null) {
            TrackerPlatform platform = context.getDevicePlatform(imei);
            device = new TrackerDevice(platform, platform.getAgentId(), imei);
            ManagedObjectCache.instance().put(device);
        }
        return device;
    }

    public void finish(String deviceImei, OperationRepresentation operation) throws UnknownTenantException {
        operation.setStatus(OperationStatus.SUCCESSFUL.toString());
        context.getDevicePlatform(deviceImei).getDeviceControlApi().update(operation);
    }

    public void fail(String deviceImei, OperationRepresentation operation, String text, SDKException ex) {
        operation.setStatus(OperationStatus.FAILED.toString());
        operation.setFailureReason(text + " " + ex.getMessage());
        context.getDevicePlatform(deviceImei).getDeviceControlApi().update(operation);
    }
    
    public void registerEventListener(TrackerAgentEventListener... eventListeners) {
        for (Object eventListener : eventListeners) {
            eventBus.register(eventListener);
        }
    }
    
    public TrackerContext getContext() {
        return context;
    }

    public void sendEvent(Object event) {
        eventBus.post(event);
    }
}
