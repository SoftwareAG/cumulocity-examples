package c8y.lx.agent;

import java.io.IOException;
import java.util.Properties;

/**
 * This class reads credentials from a configuration file. This will be replaced
 * by a device bootstrap procedure in future.
 */
public class CredentialManager {
	public static final String PROPS = "/etc/cumulocity.properties";

	public CredentialManager() throws IOException {
		Properties props = new Properties();
		PropUtils.fromFile(PROPS, props);

		String host = props.getProperty("host",
				"http://developer.cumulocity.com");
		String tenant = props.getProperty("tenant", "demo");
		String user = props.getProperty("user");
		String password = props.getProperty("password");
		String key = props.getProperty("key");

		credentials = new Credentials(host, tenant, user, password, key);
	}

	public Credentials getCredentials() {
		return credentials;
	}

	private Credentials credentials;
}
