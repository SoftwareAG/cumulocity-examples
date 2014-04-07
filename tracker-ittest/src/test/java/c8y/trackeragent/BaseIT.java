package c8y.trackeragent;

import static com.cumulocity.model.authentication.CumulocityCredentials.Builder.cumulocityCredentials;

import org.junit.Before;

import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.sdk.client.PlatformImpl;

public abstract class BaseIT {
    
    protected TrackerPlatform platform;
    protected String host = "http://localhost:8181";
    protected String user = "admin";
    protected String password = "klanpi";
    protected String tenant = "vaillant";
    
    @Before
    public void baseSetUp() {
        platform = createTrackerPlatform();
    }

    protected TrackerPlatform createTrackerPlatform() {
        CumulocityCredentials credentials = cumulocityCredentials(user, password).withTenantId(tenant).build();
        PlatformImpl platform = new PlatformImpl(host, credentials);
        return new TrackerPlatform(platform);
    }

}
