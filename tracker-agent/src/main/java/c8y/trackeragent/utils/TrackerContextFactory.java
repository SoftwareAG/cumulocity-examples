package c8y.trackeragent.utils;

import static com.cumulocity.model.authentication.CumulocityCredentials.Builder.cumulocityCredentials;
import static java.lang.Integer.parseInt;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.TrackerPlatform;
import c8y.trackeragent.TrackerPlatform.PlatformType;

import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.SDKException;

class TrackerContextFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(TrackerContextFactory.class);
    
    public static final String PLATFORM_HOST_PROP = "platformHost";    
    public static final String LOCAL_SOCKET_PORT_PROP = "localPort";
    public static final String DEFAULT_LOCAL_SOCKET_PORT = "9090";
    private static final String BOOTSTRAP_USER_PROP = "bootstrap.user";
    private static final String BOOTSTRAP_PASSWORD_PROP = "bootstrap.password";
    
    public static final String SOURCE_FILE = "common.properties";

    private final Properties props;
    
    public TrackerContextFactory() {
        String sourceFilePath = ConfigUtils.get().getConfigFilePath(SOURCE_FILE);
        props = ConfigUtils.get().getProperties(sourceFilePath);
    }

    TrackerContext createTrackerContext() throws SDKException {                       
        String host = getProperty(PLATFORM_HOST_PROP);                
        int port = parseInt(getProperty(LOCAL_SOCKET_PORT_PROP, DEFAULT_LOCAL_SOCKET_PORT));        
        TrackerPlatform bootstrapPlatform = createBootstrapPlatform(host);
        TrackerContext trackerContext = new TrackerContext(bootstrapPlatform, port, host);
        logger.info("Context created: {}.", trackerContext);
        return trackerContext;
    }

    private TrackerPlatform createBootstrapPlatform(String host) {
        String bootstrapUser = getProperty(BOOTSTRAP_USER_PROP);
        String bootstrapPassword = getProperty(BOOTSTRAP_PASSWORD_PROP);
        String tenantId = "management";
        CumulocityCredentials credentials = cumulocityCredentials(bootstrapUser, bootstrapPassword)
                .withTenantId(tenantId).build();
        return new TrackerPlatform(new PlatformImpl(host, credentials), PlatformType.BOOTSTRAP);
    }
    
    private String getProperty(String key) {
        String value = getProperty(key, null);
        if (value == null) {
            throw new RuntimeException("Missing property \'" + key + "\' in file " + SOURCE_FILE);
        }
        return value;
    }
    
    private String getProperty(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
}