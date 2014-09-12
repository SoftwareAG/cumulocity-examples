package c8y.trackeragent.utils;

import java.util.List;

import c8y.trackeragent.TrackerPlatform;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;

public class TrackerContext {
    
    private final DeviceCredentialsRepository deviceCredentialsRepository;
    private final TrackerPlatformProvider platformProvider;
    private final TrackerConfiguration configuration;
    
        
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
