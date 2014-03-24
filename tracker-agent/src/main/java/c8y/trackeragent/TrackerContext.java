package c8y.trackeragent;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cumulocity.sdk.client.Platform;

public class TrackerContext {
    
    public static final String PROPS = "/cumulocity.properties";
    public static final String TENANT_ACCESS_REGEXP = "(.*)\\.(host|user|password)";
    private static final Pattern tenantAccessPattern = Pattern.compile(TENANT_ACCESS_REGEXP);
    
    private final Properties props;
    private final Collection<Platform> platforms;

    public TrackerContext(Collection<Platform> platforms, Properties props) {
        this.platforms = platforms;
        this.props = props;
    }

    public static TrackerContext create() throws IOException {
        Properties props = new Properties();
        Collection<Platform> platforms = new ArrayList<>();
        try (InputStream is = TrackerContext.class.getResourceAsStream(PROPS); InputStreamReader ir = new InputStreamReader(is)) {
            props.load(ir);
        }
        Map<String, TenantAccess> tenantAccesses = new HashMap<>();
        for (Object configEntry : props.keySet()) {
            tryReadEntryAsTenantAccessInfo(tenantAccesses, String.valueOf(configEntry));
        }
        TrackerContext trackerContext = new TrackerContext(platforms, props);
        return trackerContext;
        
    }
    
    static void tryReadEntryAsTenantAccessInfo(Map<String, TenantAccess> tenantAccesses, String configEntry) {
        Matcher matcher = tenantAccessPattern.matcher(configEntry);
        if(matcher.matches()) {
            
        }
    }

    public Collection<Platform> getPlatforms() {
        return platforms;
    }
    
    public String getProperty(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
        
    }
    
    static class TenantAccess {
        String host;
        String user;
        String password;
        
        String getHost() {
            return host;
        }
        void setHost(String host) {
            this.host = host;
        }
        String getUser() {
            return user;
        }
        void setUser(String user) {
            this.user = user;
        }
        String getPassword() {
            return password;
        }
        void setPassword(String password) {
            this.password = password;
        }
    }
    
    public static void main(String[] args) {
        HashMap<String, TenantAccess> tenantAccesses = new HashMap<String, TenantAccess>();
        TrackerContext.tryReadEntryAsTenantAccessInfo(tenantAccesses, "vendme.password");
    }

}
