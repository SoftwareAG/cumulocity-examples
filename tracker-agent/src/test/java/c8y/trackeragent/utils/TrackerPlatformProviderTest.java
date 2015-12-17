package c8y.trackeragent.utils;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import c8y.trackeragent.TrackerPlatform;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import c8y.trackeragent.utils.ConfigUtils;
import c8y.trackeragent.utils.TrackerConfiguration;
import c8y.trackeragent.utils.TrackerPlatformProvider;

public class TrackerPlatformProviderTest {
    
    @Test
    public void shouldCreateTrackerContextBasingOnConfiguration() throws Exception {
        TrackerConfiguration config = ConfigUtils.get().loadCommonConfiguration();
        DeviceCredentialsRepository deviceCredentialsRepository = DeviceCredentialsRepository.get();
        TrackerPlatformProvider bean = new TrackerPlatformProvider(config, deviceCredentialsRepository);
        
        TrackerPlatform platform = bean.getBootstrapPlatform();        
        assertThat(platform.getTenantId()).isEqualTo("management");
        assertThat(platform.getUser()).isEqualTo("devicebootstrap");
        assertThat(platform.getPassword()).isEqualTo("Fhdt1bb1f");
    }
}
