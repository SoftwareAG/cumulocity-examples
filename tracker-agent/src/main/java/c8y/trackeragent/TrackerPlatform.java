package c8y.trackeragent;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.PlatformParameters;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.audit.AuditRecordApi;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.measurement.MeasurementApi;

public class TrackerPlatform implements Platform {
    
    private final Platform orig;

    public TrackerPlatform(PlatformImpl orig) {
        this.orig = orig;
    }

    public InventoryApi getInventoryApi() throws SDKException {
        return orig.getInventoryApi();
    }

    public IdentityApi getIdentityApi() throws SDKException {
        return orig.getIdentityApi();
    }

    public MeasurementApi getMeasurementApi() throws SDKException {
        return orig.getMeasurementApi();
    }

    public DeviceControlApi getDeviceControlApi() throws SDKException {
        return orig.getDeviceControlApi();
    }

    public AlarmApi getAlarmApi() throws SDKException {
        return orig.getAlarmApi();
    }

    public EventApi getEventApi() throws SDKException {
        return orig.getEventApi();
    }

    public AuditRecordApi getAuditRecordApi() throws SDKException {
        return orig.getAuditRecordApi();
    }

    public String getTenantId() {
        return getPlatformParameters().getTenantId();
    }
    
    public String getHost() {
        return getPlatformParameters().getHost();
    }
    
    @Deprecated
    public PlatformParameters getPlatformParameters() {
        return (PlatformParameters) orig;
    }
    
}
