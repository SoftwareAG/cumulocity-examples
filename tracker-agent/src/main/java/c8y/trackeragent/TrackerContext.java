package c8y.trackeragent;

import static java.lang.Integer.parseInt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class TrackerContext {
    
    public static final String INTERNAL_SOCKET_PORT_PROP = "port";
    public static final String DEFAULT_INTERNAL_SOCKET_PORT = "9090";
    
    private final Properties props;
    private final Map<String, TrackerPlatform> tenantIdToPlatform;
    private final List<TrackerPlatform> platforms;

    public TrackerContext(Map<String, TrackerPlatform> platforms, Properties props) {
        this.tenantIdToPlatform = platforms;
        this.platforms = new ArrayList<>(platforms.values());
        this.props = props;
    }
 
    public List<TrackerPlatform> getPlatforms() {
        return platforms;
    }
    
    public TrackerPlatform getPlatform(String tenantId) {
        return tenantIdToPlatform.get(tenantId);
    }
    
    public int getInternalSocketPort() {
        return parseInt(props.getProperty(INTERNAL_SOCKET_PORT_PROP, DEFAULT_INTERNAL_SOCKET_PORT));
    }
    
}
