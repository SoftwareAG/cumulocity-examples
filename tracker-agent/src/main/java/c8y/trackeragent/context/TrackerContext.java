package c8y.trackeragent.context;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public TrackerContext(TrackerConfiguration configuration) {
        this.configuration = configuration;
        this.deviceCredentialsRepository = DeviceCredentialsRepository.get();
        this.platformProvider = new TrackerPlatformProvider(configuration, deviceCredentialsRepository);
    }

    public TrackerPlatform getBootstrapPlatform() {
        return platformProvider.getBootstrapPlatform();
    }

    public TrackerPlatform getDevicePlatform(final String imei) {
        return platformProvider.getDevicePlatform(imei);
    }

    public List<DeviceCredentials> getDeviceCredentials() {
        return deviceCredentialsRepository.getAllCredentials();
    }
    
    public DeviceCredentials getDeviceCredentials(final String imei) {
        return deviceCredentialsRepository.getCredentials(imei);
    }
    
    public boolean isDeviceRegistered(String imei) {
        return deviceCredentialsRepository.hasCredentials(imei);
    }
    
    public TrackerConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public String toString() {
        return String.format("TrackerContext [configuration=%s]", configuration);
    }
    
}
