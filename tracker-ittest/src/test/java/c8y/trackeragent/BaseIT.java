package c8y.trackeragent;

import static com.cumulocity.model.authentication.CumulocityCredentials.Builder.cumulocityCredentials;

import org.junit.Before;

import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.PlatformParameters;

public abstract class BaseIT {
    
    protected Platform platform;
    protected PlatformParameters platformParameters;
    protected String host;
    protected String user;
    protected String password;
    private String tenant;
    
    @Before
    public void baseSetUp() {
        host = "http://localhost:8181";
        user = "admin";
        password = "klanpi";
        tenant = "vaillant";
        platform = createTrackerPlatform();
        platformParameters = (PlatformParameters) platform;
    }

    protected Platform createTrackerPlatform() {
        CumulocityCredentials credentials = cumulocityCredentials(user, password).withTenantId(tenant).build();
        return new PlatformImpl(host, credentials);
    }

}
