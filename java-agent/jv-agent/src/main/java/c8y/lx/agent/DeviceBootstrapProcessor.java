package c8y.lx.agent;

import com.cumulocity.model.authentication.CumulocityBasicCredentials;
import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceBootstrapProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DeviceBootstrapProcessor.class);

    public static final int POLL_CREDENTIALS_TIMEOUT = 24 * 3600;
    public static final int POLL_CREDENTIALS_INTERVAL = 5;

    private final CredentialsManager credentialsManager;

    public DeviceBootstrapProcessor(CredentialsManager credentialsManager) {
        this.credentialsManager = credentialsManager;
    }

    public CumulocityBasicCredentials process(String serialNumber) {
        DeviceCredentialsApi deviceCredentialsApi = getDeviceCredentialsApi();
        DeviceCredentialsRepresentation credentialsRepresentation = deviceCredentialsApi.pollCredentials(serialNumber, POLL_CREDENTIALS_INTERVAL, POLL_CREDENTIALS_TIMEOUT);
        if (credentialsRepresentation == null) {
            logger.info("No credentials accessed for serialNumber {}.", serialNumber);
            return null;
        }
        CumulocityBasicCredentials cumulocityCredentials = CumulocityBasicCredentials.builder()
                .tenantId(credentialsRepresentation.getTenantId())
                .username(credentialsRepresentation.getUsername())
                .password(credentialsRepresentation.getPassword())
                .build();
        credentialsManager.saveDeviceCredentials(cumulocityCredentials);
        return cumulocityCredentials;
    }

    private DeviceCredentialsApi getDeviceCredentialsApi() {
        return new PlatformImpl(credentialsManager.getHost(), credentialsManager.getBootstrapCredentials()).getDeviceCredentialsApi();
    }

}
