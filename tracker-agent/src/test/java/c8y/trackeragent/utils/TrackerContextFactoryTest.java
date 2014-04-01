package c8y.trackeragent.utils;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import c8y.trackeragent.TrackerPlatform;
import c8y.trackeragent.utils.TrackerContext;

public class TrackerContextFactoryTest {
    
    @Test
    public void shouldCreateTrackerContextBasingOnConfiguration() throws Exception {
        TrackerContext trackerContext = TrackerContext.get();
        
        assertThat(trackerContext.getRegularPlatforms()).hasSize(1);        
        TrackerPlatform platform = trackerContext.getPlatform("vaillant");
        assertThat(platform.getUser().equals("admin"));
        assertThat(platform.getPlatformType()).isEqualTo(TrackerPlatform.PlatformType.REGULAR);
        assertThat(platform.getPassword()).isNotEmpty();
        assertThat(trackerContext.getLocalSocketPort()).isEqualTo(9090);
        
        platform = trackerContext.getBootstrapPlatform();
        assertThat(platform.getPlatformType()).isEqualTo(TrackerPlatform.PlatformType.BOOTSTRAP);
    }
}
