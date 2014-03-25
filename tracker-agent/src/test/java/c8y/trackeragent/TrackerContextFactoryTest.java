package c8y.trackeragent;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformParameters;

public class TrackerContextFactoryTest {
    
    @Test
    public void shouldCreateTrackerContextBasingOnConfiguration() throws Exception {
        
        TrackerContext trackerContext = TrackerContextFactory.instance().createTrackerContext();
        
        assertThat(trackerContext.getPlatforms()).hasSize(2);        
        Platform platform = trackerContext.getPlatform("other");
        PlatformParameters platformParameters = (PlatformParameters) platform;
        assertThat(platformParameters.getUser().equals("otherUser"));
        assertThat(platformParameters.getPassword().equals("otherPassword"));
    }
    
    @Test
    public void shouldReadPortProperty() throws Exception {
        
        TrackerContext trackerContext = TrackerContextFactory.instance().createTrackerContext();
        
        assertThat(trackerContext.getProperty("port", null)).isEqualTo("9090");
        
    }
}
