package c8y.trackeragent.utils;

import static com.cumulocity.model.authentication.CumulocityCredentials.Builder.cumulocityCredentials;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.DeviceManagedObject;
import c8y.trackeragent.TrackerPlatform;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import c8y.trackeragent.exception.UnknownTenantException;

import com.cumulocity.model.Agent;
import com.cumulocity.model.ID;
import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.SDKException;

public class TrackerContext {
    
    private static final Logger logger = LoggerFactory.getLogger(TrackerContext.class);
    private static final TrackerContext instance = new TrackerContextFactory().createTrackerContext();
    
    private final Map<String, ManagedObjectRepresentation> tenantIdToAgent = new HashMap<String, ManagedObjectRepresentation>();    
    private final Map<String, TrackerPlatform> tenantIdToPlatform = new HashMap<String, TrackerPlatform>();
    private final TrackerPlatform bootstrapPlatform;
    private final DeviceCredentialsRepository deviceCredentialsRepository = DeviceCredentialsRepository.instance();
    private int localSocketPort;
    
    TrackerContext(Collection<TrackerPlatform> platforms, int localSocketPort) {
        this.localSocketPort = localSocketPort;
        TrackerPlatform bootstrapPlatformTmp = null;
        for (TrackerPlatform platform : platforms) {
            if(platform.isBootstrap()) {
                bootstrapPlatformTmp = platform;
            } else {
                this.tenantIdToPlatform.put(platform.getTenantId(), platform);
            }
        }        
        this.bootstrapPlatform = bootstrapPlatformTmp;
    }
        
    public static TrackerContext get() {
        return instance;
    }
 
    public Collection<TrackerPlatform> getRegularPlatforms() {
        return tenantIdToPlatform.values();
    }
    
    public TrackerPlatform getBootstrapPlatform() {
        return bootstrapPlatform;
    }

    public TrackerPlatform getPlatform(String tenantId) {
        TrackerPlatform trackerPlatform = tenantIdToPlatform.get(tenantId);
        if(trackerPlatform == null) {
            throw UnknownTenantException.forTenantId(tenantId);
        }
        return trackerPlatform;
    }
    
    public TrackerPlatform getDevicePlatform(String imei) {
        DeviceCredentials deviceCredentials = deviceCredentialsRepository.getCredentials(imei);
        String tenantId = deviceCredentials.getTenantId();
        CumulocityCredentials credentials = cumulocityCredentials(
                deviceCredentials.getUser(), deviceCredentials.getPassword()).withTenantId(tenantId).build();
        String host = getPlatform(tenantId).getHost();
        return new TrackerPlatform(new PlatformImpl(host, credentials), TrackerPlatform.PlatformType.REGULAR);
    }
    
    public int getLocalSocketPort() {
        return localSocketPort;
    }
    
    public void setLocalSocketPort(int localSocketPort) {
        this.localSocketPort = localSocketPort;
    }
    
    public ManagedObjectRepresentation getOrCreateAgent(String tenantId) {
        ManagedObjectRepresentation agent = tenantIdToAgent.get(tenantId);
        if(agent == null) {
            agent = createOrUpdateAgent(tenantId);
            logger.info("Agent for tenantId {} specified {}.", tenantId, agent.getId());
            tenantIdToAgent.put(tenantId, agent);
        }
        return agent;
    }
    
    public boolean isDeviceRegistered(String imei) {
        return deviceCredentialsRepository.hasCredentials(imei);
    }
    
    private ManagedObjectRepresentation createOrUpdateAgent(String tenantId) throws SDKException {
        TrackerPlatform platform = getPlatform(tenantId);
        DeviceManagedObject deviceManagedObject = new DeviceManagedObject(platform);
        ManagedObjectRepresentation agentMo = new ManagedObjectRepresentation();
        agentMo.setType("c8y_TrackerAgent");
        agentMo.setName("Tracker agent");
        agentMo.set(new Agent());
        ID extId = DeviceManagedObject.getAgentExternalId();
        deviceManagedObject.createOrUpdate(agentMo, extId, null);
        return agentMo;
    }

    @Override
    public String toString() {
        return String.format("TrackerContext [bootstrapPlatform=%s, localSocketPort=%s]", bootstrapPlatform, localSocketPort);
    }
    
    
}
