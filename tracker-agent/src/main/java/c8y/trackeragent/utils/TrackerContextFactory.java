package c8y.trackeragent.utils;

import static c8y.trackeragent.utils.ConfigUtils.getConfigFilePath;
import static com.cumulocity.model.authentication.CumulocityCredentials.Builder.cumulocityCredentials;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import c8y.trackeragent.TrackerPlatform;
import c8y.trackeragent.TrackerPlatform.PlatformType;
import c8y.trackeragent.utils.GroupPropertyAccessor.Group;

import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.SDKException;

class TrackerContextFactory {
    
    public static final String PLATFORM_HOST_PROP = "platformHost";    
    public static final String LOCAL_SOCKET_PORT_PROP = "localPort";
    public static final String DEFAULT_LOCAL_SOCKET_PORT = "9090";

    public static final String SOURCE_FILE = "common.properties";

    TrackerContext createTrackerContext() throws SDKException {
        Path configFilePath = getConfigFilePath(SOURCE_FILE);
        GroupPropertyAccessor propertyAccessor = new GroupPropertyAccessor(configFilePath, asList("user", "password", "type"));
        propertyAccessor.refresh();
        
        Properties props = propertyAccessor.getSource();
        String host = props.getProperty(PLATFORM_HOST_PROP);                
        if(host == null) {
            throw new RuntimeException("Missing property " + PLATFORM_HOST_PROP);
        }        
        int port = parseInt(props.getProperty(LOCAL_SOCKET_PORT_PROP, DEFAULT_LOCAL_SOCKET_PORT));
        
        List<Group> groups = propertyAccessor.getGroups();
        Map<String, TrackerPlatform> platforms = asPlatforms(groups, host);
        return new TrackerContext(platforms.values(), port);

    }

    private Map<String, TrackerPlatform> asPlatforms(List<Group> groups, String host) {
        Map<String, TrackerPlatform> result = new HashMap<>();
        for (Group group : groups) {
            if (group.isFullyInitialized()) {
                result.put(group.getGroupName(), asPlatform(group, host));
            }
        }
        return result;
    }

    private TrackerPlatform asPlatform(Group group, String host) {
        PlatformType platformType = TrackerPlatform.PlatformType.valueOf(group.get("type").toUpperCase());
        CumulocityCredentials credentials = cumulocityCredentials(
                group.get("user"), group.get("password")).withTenantId(group.getGroupName()).build();
        return new TrackerPlatform(new PlatformImpl(host, credentials), platformType);
    }
}
