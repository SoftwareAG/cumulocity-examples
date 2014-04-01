package c8y.trackeragent;

import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.PlatformParameters;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.audit.AuditRecordApi;
import com.cumulocity.sdk.client.cep.CepApi;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.measurement.MeasurementApi;

public class TrackerPlatform implements Platform {
    
    private final Platform orig;
    private final PlatformType platformType;

    public TrackerPlatform(PlatformImpl orig, PlatformType platformType) {
        this.orig = orig;
        this.platformType = platformType;
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

    public CepApi getCepApi() throws SDKException {
        return orig.getCepApi();
    }

    public DeviceCredentialsApi getDeviceCredentialsApi() throws SDKException {
        return orig.getDeviceCredentialsApi();
    }

    public String getTenantId() {
        return getPlatformParameters().getTenantId();
    }
    
    public String getHost() {
        return getPlatformParameters().getHost();
    }
    
    public String getUser() {
        return getPlatformParameters().getUser();
    }
    
    public String getPassword() {
        return getPlatformParameters().getPassword();
    }
    
    public PlatformParameters getPlatformParameters() {
        return (PlatformParameters) orig;
    }
    
    public PlatformType getPlatformType() {
        return platformType;
    }
    
    public boolean isRegular() {
        return PlatformType.REGULAR.equals(platformType);
    }
    
    public boolean isBootstrap() {
        return PlatformType.BOOTSTRAP.equals(platformType);
    }
    
    public static enum PlatformType {
        REGULAR, 
        BOOTSTRAP;
    }

    @Override
    public String toString() {
        return String.format("TrackerPlatform [orig=%s, platformType=%s, getTenantId()=%s, getHost()=%s, getUser()=%s]", orig, platformType, getTenantId(), getHost(), getUser());
    }
    
    


    
}
