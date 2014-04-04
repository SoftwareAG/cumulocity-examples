package c8y.trackeragent.utils;

import static com.cumulocity.model.authentication.CumulocityCredentials.Builder.cumulocityCredentials;

import java.util.List;

import c8y.trackeragent.DeviceManagedObject;
import c8y.trackeragent.TrackerPlatform;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;

import com.cumulocity.model.Agent;
import com.cumulocity.model.ID;
import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.PlatformImpl;

public class TrackerContext {
    
    private static final TrackerContext instance = new TrackerContextFactory().createTrackerContext();
    
    private final TrackerPlatform bootstrapPlatform;
    private final DeviceCredentialsRepository deviceCredentialsRepository = DeviceCredentialsRepository.get();
    private final Object lock = new Object();
    private final String host;
    private int localSocketPort;
    
    TrackerContext(TrackerPlatform bootstrapPlatform, int localSocketPort, String host) {
        this.localSocketPort = localSocketPort;
        this.host = host;
        this.bootstrapPlatform = bootstrapPlatform;
    }
        
    public static TrackerContext get() {
        return instance;
    }
     
    public TrackerPlatform getBootstrapPlatform() {
        return bootstrapPlatform;
    }

    
    /**
     * TODO store trackerPlatforms in cache   
     */
    public TrackerPlatform getDevicePlatform(String imei) {
        DeviceCredentials deviceCredentials = deviceCredentialsRepository.getCredentials(imei);
        String tenantId = deviceCredentials.getTenantId();
        CumulocityCredentials credentials = cumulocityCredentials(
                deviceCredentials.getUser(), deviceCredentials.getPassword()).withTenantId(tenantId).build();
        TrackerPlatform trackerPlatform = new TrackerPlatform(new PlatformImpl(host, credentials), TrackerPlatform.PlatformType.REGULAR);
        setupAgent(trackerPlatform);
        return trackerPlatform;
    }

    public List<DeviceCredentials> getDeviceCredentials() {
        return deviceCredentialsRepository.getAllCredentials();
    }
    
    public int getLocalSocketPort() {
        return localSocketPort;
    }
    
    public void setLocalSocketPort(int localSocketPort) {
        this.localSocketPort = localSocketPort;
    }
    
    public boolean isDeviceRegistered(String imei) {
        return deviceCredentialsRepository.hasCredentials(imei);
    }
    
    private void setupAgent(TrackerPlatform platform) {
        synchronized (lock) {
            DeviceManagedObject deviceManagedObject = new DeviceManagedObject(platform);
            ManagedObjectRepresentation agentMo = new ManagedObjectRepresentation();
            agentMo.setType("c8y_TrackerAgent");
            agentMo.setName("Tracker agent");
            agentMo.set(new Agent());
            ID extId = DeviceManagedObject.getAgentExternalId();
            deviceManagedObject.createOrUpdate(agentMo, extId, null);
            platform.setAgent(agentMo);
        }
    }
    
    @Override
    public String toString() {
        return String.format("TrackerContext [bootstrapPlatform=%s, localSocketPort=%s]", bootstrapPlatform, localSocketPort);
    }
    
    
}
