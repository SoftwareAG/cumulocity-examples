package c8y.trackeragent;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Test;

import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformParameters;

public class TrackerContextFactoryTest {
    
    @Test
    public void shouldCreateTrackerContextBasingOnConfiguration() throws Exception {
        
        TrackerContext trackerContext = TrackerContextFactory.instance().createTrackerContext();
        
        assertThat(trackerContext.getPlatforms()).hasSize(2);        
        boolean hasOtherHost = false;
        for (Platform platform : trackerContext.getPlatforms()) {
            PlatformParameters platformParameters = (PlatformParameters) platform;
            if(platformParameters.getHost().equals("http://other.com/")) {
                hasOtherHost = true;
                assertThat(platformParameters.getUser().equals("otherUser"));
                assertThat(platformParameters.getPassword().equals("otherPassword"));
            }
        }        
        assertThat(hasOtherHost).isTrue();
    }
    
    @Test
    public void shouldReadPortProperty() throws Exception {
        
        TrackerContext trackerContext = TrackerContextFactory.instance().createTrackerContext();
        
        assertThat(trackerContext.getProperty("port", null)).isEqualTo("9090");
        
    }
}
