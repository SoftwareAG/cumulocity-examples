package c8y.trackeragent;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class TrackerContextFactoryTest {
    
    @Test
    public void shouldCreateTrackerContextBasingOnConfiguration() throws Exception {
        TrackerContext trackerContext = TrackerContextFactory.createTrackerContext();
        
        assertThat(trackerContext.getPlatforms()).hasSize(1);        
        TrackerPlatform platform = trackerContext.getPlatform("vaillant");
        assertThat(platform.getPlatformParameters().getUser().equals("admin"));
        assertThat(platform.getPlatformParameters().getPassword()).isNotEmpty();
    }
    
    @Test
    public void shouldReadPortProperty() throws Exception {
        TrackerContext trackerContext = TrackerContextFactory.createTrackerContext();
        
        assertThat(trackerContext.getLocalSocketPort()).isEqualTo(9090);
    }
}
