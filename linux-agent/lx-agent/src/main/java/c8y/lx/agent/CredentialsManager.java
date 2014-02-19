package c8y.lx.agent;

import java.util.Properties;

import com.cumulocity.model.authentication.CumulocityCredentials;

/**
 * This class reads credentials from a configuration file. This will be replaced
 * by a device bootstrap procedure in future.
 */
public class CredentialsManager {

    public static final String DEFAULT_PROPS_LOCATION = "/etc/cumulocity.properties";

    public static CredentialsManager defaultCredentialsManager() {
        return new CredentialsManager(DEFAULT_PROPS_LOCATION);
    }

    private final String host;

    private final CumulocityCredentials credentials;

    public CredentialsManager(String propsLocation) {
        this(PropUtils.fromFile(propsLocation));
    }

    public CredentialsManager(Properties props) {
        host = props.getProperty("host", "http://developer.cumulocity.com");
        credentials = new CumulocityCredentials(
                props.getProperty("tenant", "demo"),
                props.getProperty("user"),
                props.getProperty("password"),
                null);
    }

    public String getHost() {
        return host;
    }

    public CumulocityCredentials getCredentials() {
        return credentials;
    }
}
