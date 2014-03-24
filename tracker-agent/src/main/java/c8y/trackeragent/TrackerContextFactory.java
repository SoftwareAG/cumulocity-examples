package c8y.trackeragent;

import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformImpl;

public class TrackerContextFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(TrackerContextFactory.class);
    
    public static final String PROPS = "/cumulocity.properties";
    public static final String TENANT_ACCESS_REGEXP = "(.*)\\.(host|user|password)";
    private static final Pattern tenantAccessPattern = Pattern.compile(TENANT_ACCESS_REGEXP);

    private final Properties props = new Properties();
    private final List<Platform> platforms = new ArrayList<>();
    private final Map<String, TenantAccess> tenantAccesses = new HashMap<>();
    
    public static TrackerContextFactory instance() {
        return new TrackerContextFactory();
    }
    
    public TrackerContext createTrackerContext() throws IOException {
        loadConfiguration();
        for (Object configEntry : props.keySet()) {
            tryReadEntryAsTenantAccessInfo(String.valueOf(configEntry));
        }
        for (Entry<String, TenantAccess> tenantAccessEntry : tenantAccesses.entrySet()) {
            TenantAccess tenantAccess = tenantAccessEntry.getValue();
            if(tenantAccess.isValid()) {
                Platform platform = new PlatformImpl(tenantAccess.getHost(), tenantAccess.getCredentials());
                platforms.add(platform);
            } else {
                logger.error(format("Missing access information for tenant %s; expected 'host', 'user' and 'password'", 
                        tenantAccessEntry.getKey()), tenantAccess);                
            }
        }
        TrackerContext trackerContext = new TrackerContext(platforms, props);
        return trackerContext;
        
    }

    private void loadConfiguration() throws IOException {
        try (InputStream is = TrackerContext.class.getResourceAsStream(PROPS); 
                InputStreamReader ir = new InputStreamReader(is)) {
            props.load(ir);
        }
    }
    
    private void tryReadEntryAsTenantAccessInfo(String configEntry) {
        Matcher matcher = tenantAccessPattern.matcher(configEntry);
        if(matcher.matches()) {
            String tenantId = matcher.group(1);
            TenantAccess tenantAccess = getOrCreateTenantAccess(tenantId);
            tenantAccess.set(matcher.group(2), props.getProperty(configEntry));
        }
    }

    private TenantAccess getOrCreateTenantAccess(String tenantId) {
        TenantAccess tenantAccess = tenantAccesses.get(tenantId);
        if(tenantAccess == null) {
            tenantAccess = new TenantAccess();
            tenantAccesses.put(tenantId, tenantAccess);
        }
        return tenantAccess;
    }
    
    static class TenantAccess {
            
        private final Map<String, String> vals = new HashMap<>();
        
        String getHost() {
            return vals.get("host");
        }
        void set(String paramName, String paramValue) {
            vals.put(paramName, paramValue);
        }
        String getUser() {
            return vals.get("user");
        }
        String getPassword() {
            return vals.get("password");
        }
        boolean isValid() {
            return vals.keySet().size() == 3;
        }
        CumulocityCredentials getCredentials() {
            return new CumulocityCredentials(getUser(), getPassword());
        }
        @Override
        public String toString() {
            return String.format("TenantAccess [vals=%s]", vals);
        }
        
    }
    
    public static void main(String[] args) {
        TrackerContextFactory.instance().tryReadEntryAsTenantAccessInfo("vendme.password");
    }
}
