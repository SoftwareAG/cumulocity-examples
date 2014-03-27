package c8y.trackeragent.utils;

import static com.cumulocity.model.authentication.CumulocityCredentials.Builder.cumulocityCredentials;
import static java.lang.Integer.parseInt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.DeviceManagedObject;
import c8y.trackeragent.TrackerPlatform;
import c8y.trackeragent.exception.UnknownTenantException;
import c8y.trackeragent.repository.DeviceCredentials;
import c8y.trackeragent.repository.DeviceCredentialsRepository;

import com.cumulocity.model.Agent;
import com.cumulocity.model.ID;
import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.SDKException;

public class TrackerContext {
    
    private static final Logger logger = LoggerFactory.getLogger(TrackerContext.class);
    
    public static final String LOCAL_SOCKET_PORT_PROP = "port";
    public static final String DEFAULT_LOCAL_SOCKET_PORT = "9090";
    
    private final Map<String, ManagedObjectRepresentation> tenantToAgent = new HashMap<>();    
    private final Map<String, TrackerPlatform> tenantIdToPlatform;
    private final List<TrackerPlatform> platforms = new ArrayList<>();
    private TrackerPlatform bootstrapPlatform;
    private final DeviceCredentialsRepository deviceCredentialsRepository;
    private int localSocketPort;
    
    private static TrackerContext instance = new TrackerContextFactory().createTrackerContext();
    
    TrackerContext(Map<String, TrackerPlatform> allPlatforms, Properties props) {
        for (TrackerPlatform platform : allPlatforms.values()) {
            if(platform.isBootstrap()) {
                bootstrapPlatform = platform;
            } else {
                this.platforms.add(platform);
            }
        }        
        this.tenantIdToPlatform = allPlatforms;
        this.deviceCredentialsRepository = DeviceCredentialsRepository.instance();
        this.localSocketPort = parseInt(props.getProperty(LOCAL_SOCKET_PORT_PROP, DEFAULT_LOCAL_SOCKET_PORT));
    }
        
    public static TrackerContext get() {
        return instance;
    }
 
    public List<TrackerPlatform> getRegularPlatforms() {
        return platforms;
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
        ManagedObjectRepresentation agent = tenantToAgent.get(tenantId);
        if(agent == null) {
            agent = createOrUpdateAgent(tenantId);
            logger.info("Agent for tenantId {} specified {}.", tenantId, agent.getId());
            tenantToAgent.put(tenantId, agent);
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
        ID extId = new ID("c8y_TrackerAgent");
        extId.setType("c8y_ServerSideAgent");
        deviceManagedObject.createOrUpdate(agentMo, extId, null);
        return agentMo;
    }
}
