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

    public TrackerPlatform getDevicePlatform(final String imei) {
        return platformProvider.getDevicePlatform(imei);
    }

    public List<DeviceCredentials> getDeviceCredentials() {
        return deviceCredentialsRepository.getAllDeviceCredentials();
    }
    
    public DeviceCredentials getDeviceCredentials(final String imei) {
        return deviceCredentialsRepository.getDeviceCredentials(imei);
    }
    
    public boolean isDeviceRegistered(String imei) {
        return deviceCredentialsRepository.hasDeviceCredentials(imei);
    }
    
    public TrackerConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public String toString() {
        return String.format("TrackerContext [configuration=%s]", configuration);
    }
    
}
