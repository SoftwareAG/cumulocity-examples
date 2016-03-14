package c8y.trackeragent.context;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.repository.InventoryRepository;

import c8y.trackeragent.TrackerPlatform;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import c8y.trackeragent.utils.TrackerConfiguration;
import c8y.trackeragent.utils.TrackerPlatformProvider;

@Component
public class TrackerContext {
    
    private final DeviceCredentialsRepository deviceCredentialsRepository;
    private final TrackerPlatformProvider platformProvider;
    private final TrackerConfiguration configuration;
    
    @Autowired
    public TrackerContext(TrackerConfiguration configuration, 
            DeviceContextService contextSerivce,
            InventoryRepository inventoryRepository,
            DeviceCredentialsRepository deviceCredentialsRepository,
            @Value("${C8Y.agent.user}") String agentUser,
            @Value("${C8Y.agent.password}") String agentPassword) {
        this.configuration = configuration;
        this.deviceCredentialsRepository = deviceCredentialsRepository;
        this.platformProvider = new TrackerPlatformProvider(configuration, deviceCredentialsRepository, contextSerivce, inventoryRepository, agentUser, agentPassword);
    }

    public TrackerPlatform getBootstrapPlatform() {
        return platformProvider.getBootstrapPlatform();
    }

    public TrackerPlatform getTenantPlatform(final String tenant) {
        return platformProvider.getTenantPlatform(tenant);
    }

    public List<DeviceCredentials> getAllTenantCredentials() {
    	return deviceCredentialsRepository.getAllAgentCredentials();
    }
    
    public DeviceCredentials getTenantCredentials(String imei) {
    	TrackerPlatform devicePlatform = getDevicePlatform(imei);
    	String tenantId = devicePlatform.getTenantId();
    	return deviceCredentialsRepository.getAgentCredentials(tenantId);
    }
    
    public boolean isDeviceRegistered(String imei) {
        return deviceCredentialsRepository.hasDeviceCredentials(imei);
    }
    
    public TrackerConfiguration getConfiguration() {
        return configuration;
    }
    
    public TrackerPlatform getDevicePlatform(String imei) {
    	DeviceCredentials deviceCredentials = deviceCredentialsRepository.getDeviceCredentials(imei);
    	String tenant = deviceCredentials.getTenant();
    	return getTenantPlatform(tenant);
    }

    @Override
    public String toString() {
        return String.format("TrackerContext [configuration=%s]", configuration);
    }

    
}
