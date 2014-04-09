package c8y.lx.agent;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import com.cumulocity.model.authentication.CumulocityCredentials;

/**
 * This class reads credentials from a configuration file. This will be replaced
 * by a device bootstrap procedure in future.
 */
public class CredentialsManager {

    public static final String COMMON_PROPS_LOCATION = "./cfg/cumulocity.properties";
    public static final String DEVICE_PROPS_LOCATION = "./cfg/device.properties";

    public static CredentialsManager defaultCredentialsManager() {
        return new CredentialsManager(COMMON_PROPS_LOCATION, DEVICE_PROPS_LOCATION);
    }

    private final String host;
    private final String devicePropsFile;

    private final CumulocityCredentials deviceCredentials;
    private final CumulocityCredentials bootstrapCredentials;

    public CredentialsManager(String commonPropsFile, String devicePropsFile) {
        this.devicePropsFile = devicePropsFile;
        Properties commonProps = PropUtils.fromFile(commonPropsFile);
        Properties deviceProps = PropUtils.fromFile(devicePropsFile);
        this.deviceCredentials = initDeviceCredentials(deviceProps);
        this.bootstrapCredentials = initBootstrapCredentials(commonProps);
        this.host = commonProps.getProperty("host", "http://developer.cumulocity.com");
    }

    private static CumulocityCredentials initBootstrapCredentials(Properties commonProps) {
        return new CumulocityCredentials(
                commonProps.getProperty("bootstrap.tenant", "management"),
                commonProps.getProperty("bootstrap.user", "devicebootstrap"),
                commonProps.getProperty("bootstrap.password", "Fhdt1bb1f"),
                null);
    }

    private static CumulocityCredentials initDeviceCredentials(Properties deviceProps) {
        String user = deviceProps.getProperty("user");
        String password = deviceProps.getProperty("password");
        if(user == null || password == null) {
            return null;
        } else {
            return new CumulocityCredentials(deviceProps.getProperty("tenant", "demo"), user, password, null);
        }
    }

    public String getHost() {
        return host;
    }

    public CumulocityCredentials getDeviceCredentials() {
        return deviceCredentials;
    }

    public CumulocityCredentials getBootstrapCredentials() {
        return bootstrapCredentials;
    }

    public void saveDeviceCredentials(CumulocityCredentials cumulocityCredentials) {
        File file = new File(devicePropsFile);
        try {
            file.createNewFile();
        } catch (IOException ex) {
            throw new RuntimeException("Cant create file " + devicePropsFile, ex);
        }
        Properties props = new Properties();
        props.setProperty("user", cumulocityCredentials.getUsername());
        props.setProperty("password", cumulocityCredentials.getPassword());
        props.setProperty("tenant", cumulocityCredentials.getTenantId());
        PropUtils.toFile(props, devicePropsFile);
    }
}
