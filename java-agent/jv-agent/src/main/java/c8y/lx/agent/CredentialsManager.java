package c8y.lx.agent;

import com.cumulocity.model.authentication.CumulocityBasicCredentials;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * This class reads credentials from a configuration file. This will be replaced
 * by a device bootstrap procedure in future.
 */
public class CredentialsManager {

    public static final String COMMON_PROPS_LOCATION = "./cfg/cumulocity.properties";
    public static final String DEVICE_PROPS_LOCATION = "./cfg/device.properties";

    private static final String DEFAULT_HOST = "http://developer.cumulocity.com";
    private static final String DEFAULT_BOOTSTRAP_TENANT = "management";
    private static final String DEFAULT_BOOTSTRAP_USER = "devicebootstrap";
    private static final String DEFAULT_BOOTSTRAP_PASSWORD = "Fhdt1bb1f";

    public static CredentialsManager defaultCredentialsManager() {
        return new CredentialsManager(COMMON_PROPS_LOCATION, DEVICE_PROPS_LOCATION);
    }

    private final String host;
    private final String devicePropsFile;

    private final CumulocityBasicCredentials deviceCredentials;
    private final CumulocityBasicCredentials bootstrapCredentials;

    public CredentialsManager(String commonPropsFile, String devicePropsFile) {
        this.devicePropsFile = devicePropsFile;
        Properties commonProps = PropUtils.fromFile(commonPropsFile);
        Properties deviceProps = PropUtils.fromFile(devicePropsFile);
        this.deviceCredentials = initDeviceCredentials(deviceProps);
        this.bootstrapCredentials = initBootstrapCredentials(commonProps);
        this.host = commonProps.getProperty("host", DEFAULT_HOST);
    }

    private static CumulocityBasicCredentials initBootstrapCredentials(Properties commonProps) {
        return CumulocityBasicCredentials.builder()
                .tenantId(commonProps.getProperty("bootstrap.tenant", DEFAULT_BOOTSTRAP_TENANT))
                .username(commonProps.getProperty("bootstrap.user", DEFAULT_BOOTSTRAP_USER))
                .password(commonProps.getProperty("bootstrap.password", DEFAULT_BOOTSTRAP_PASSWORD))
                .build();
    }

    private static CumulocityBasicCredentials initDeviceCredentials(Properties deviceProps) {
        String user = deviceProps.getProperty("user");
        String password = deviceProps.getProperty("password");
        if(user == null || password == null) {
            return null;
        } else {
            return CumulocityBasicCredentials.builder()
                    .tenantId(deviceProps.getProperty("tenant", "demo"))
                    .username(user)
                    .password(password)
                    .build();
        }
    }

    public String getHost() {
        return host;
    }

    public CumulocityBasicCredentials getDeviceCredentials() {
        return deviceCredentials;
    }

    public CumulocityBasicCredentials getBootstrapCredentials() {
        return bootstrapCredentials;
    }

    public void saveDeviceCredentials(CumulocityBasicCredentials cumulocityBasicCredentials) {
        File file = new File(devicePropsFile);
        try {
            file.createNewFile();
        } catch (IOException ex) {
            throw new RuntimeException("Cant create file " + devicePropsFile, ex);
        }
        Properties props = new Properties();
        props.setProperty("user", cumulocityBasicCredentials.getUsername());
        props.setProperty("password", cumulocityBasicCredentials.getPassword());
        props.setProperty("tenant", cumulocityBasicCredentials.getTenantId());
        PropUtils.toFile(props, devicePropsFile);
    }
}
