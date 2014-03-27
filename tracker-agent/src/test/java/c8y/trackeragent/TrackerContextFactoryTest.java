package c8y.trackeragent;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import c8y.trackeragent.utils.TrackerContext;

public class TrackerContextFactoryTest {
    
    @Test
    public void shouldCreateTrackerContextBasingOnConfiguration() throws Exception {
        TrackerContext trackerContext = TrackerContext.get();
        
        assertThat(trackerContext.getPlatforms()).hasSize(1);        
        TrackerPlatform platform = trackerContext.getPlatform("vaillant");
        assertThat(platform.getUser().equals("admin"));
        assertThat(platform.getPassword()).isNotEmpty();
        assertThat(trackerContext.getLocalSocketPort()).isEqualTo(9090);
    }
}
