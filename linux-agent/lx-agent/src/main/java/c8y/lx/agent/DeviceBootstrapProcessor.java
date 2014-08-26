package c8y.lx.agent;

import static com.cumulocity.model.authentication.CumulocityCredentials.Builder.cumulocityCredentials;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;

public class DeviceBootstrapProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceBootstrapProcessor.class);
    
    public static final int POLL_CREDENTIALS_TIMEOUT = 24 * 3600;
    public static final int POLL_CREDENTIALS_INTERVAL = 5;

    private final CredentialsManager credentialsManager;

    public DeviceBootstrapProcessor(CredentialsManager credentialsManager) {
        this.credentialsManager = credentialsManager;
    }

    public CumulocityCredentials process(String serialNumber) {
        DeviceCredentialsApi deviceCredentialsApi = getDeviceCredentialsApi();
        DeviceCredentialsRepresentation credentialsRepresentation = deviceCredentialsApi.pollCredentials(serialNumber, POLL_CREDENTIALS_INTERVAL, POLL_CREDENTIALS_TIMEOUT);
        if (credentialsRepresentation == null) {
            logger.info("No credentials accessed for serialNumber {}.", serialNumber);
            return null;
        }
        CumulocityCredentials cumulocityCredentials = asCumulocityCredentials(credentialsRepresentation);
        credentialsManager.saveDeviceCredentials(cumulocityCredentials);
        return cumulocityCredentials;
    }

    private CumulocityCredentials asCumulocityCredentials(DeviceCredentialsRepresentation credentialsRepresentation) {
        //formatter:off
        return cumulocityCredentials(
                credentialsRepresentation.getUsername(), 
                credentialsRepresentation.getPassword())
                .withTenantId(credentialsRepresentation.getTenantId()).build();
        //formatter:on
    }

    private DeviceCredentialsApi getDeviceCredentialsApi() {
        return new PlatformImpl(credentialsManager.getHost(), credentialsManager.getBootstrapCredentials()).getDeviceCredentialsApi();
    }

}
