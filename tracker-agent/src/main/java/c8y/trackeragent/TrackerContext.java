package c8y.trackeragent;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import com.cumulocity.sdk.client.Platform;

public class TrackerContext {
    
    private final Properties props;
    private final Map<String, Platform> platforms;

    public TrackerContext(Map<String, Platform> platforms, Properties props) {
        this.platforms = platforms;
        this.props = props;
    }
 
    public Collection<Platform> getPlatforms() {
        return platforms.values();
    }
    
    public Platform getPlatform(String tenantId) {
        return platforms.get(tenantId);
    }
    
    public String getProperty(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
    
}
