package c8y.trackeragent.utils;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import c8y.trackeragent.TrackerPlatform;
import c8y.trackeragent.utils.TrackerContext;

public class TrackerContextFactoryTest {
    
    @Test
    public void shouldCreateTrackerContextBasingOnConfiguration() throws Exception {
        TrackerContext trackerContext = TrackerContext.get();        
        
        TrackerPlatform platform = trackerContext.getBootstrapPlatform();        
        assertThat(platform.getPlatformType()).isEqualTo(TrackerPlatform.PlatformType.BOOTSTRAP);
        assertThat(platform.getTenantId()).isEqualTo("management");
        assertThat(platform.getUser().equals("devicebootstrap"));
        assertThat(platform.getPassword().equals("secret123"));
    }
}
