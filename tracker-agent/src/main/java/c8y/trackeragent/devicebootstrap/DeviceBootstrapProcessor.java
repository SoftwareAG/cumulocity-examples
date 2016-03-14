package c8y.trackeragent.devicebootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.event.TrackerAgentEventListener;
import c8y.trackeragent.event.TrackerAgentEvents;

import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;
import com.google.common.eventbus.Subscribe;

@Component
public class DeviceBootstrapProcessor implements TrackerAgentEventListener {

    protected static Logger logger = LoggerFactory.getLogger(DeviceBootstrapProcessor.class);

    private final TrackerAgent trackerAgent;
    private final DeviceCredentialsApi deviceCredentialsApi;

    @Autowired
    public DeviceBootstrapProcessor(TrackerAgent trackerAgent) {
        this.trackerAgent = trackerAgent;
        this.deviceCredentialsApi = trackerAgent.getContext().getBootstrapPlatform().getDeviceCredentialsApi();
    }

    @Subscribe
    public void listen(TrackerAgentEvents.NewDeviceEvent event) {
        startBootstraping(event.getImei());
    }

    public void startBootstraping(String imei) {    
        logger.info("Start bootstrapping: {}", imei);
        DeviceCredentialsRepresentation credentialsRepresentation = pollCredentials(imei);
        if (credentialsRepresentation == null) {
            logger.info("No credentials accessed for imei {}.", imei);
        } else {
            DeviceCredentials credentials = asCredentials(credentialsRepresentation);
            logger.info("Credentials for imei {} accessed: {}.", imei, credentials);
            trackerAgent.sendEvent(new TrackerAgentEvents.NewDeviceRegisteredEvent(credentials));
        }
    }
    
    private DeviceCredentialsRepresentation pollCredentials(final String imei) {
        try {
            return deviceCredentialsApi.pollCredentials(imei);
        } catch (SDKException e) {
            if (e.getHttpStatus() == HttpStatus.NOT_FOUND.value()) {
                logger.debug("Crentials not yet available for device: " + imei);
            } else {
                logger.error("Failed to retrieve credentials from cumulocity.", e);
            }
        }
        return null;
    }
    
    private DeviceCredentials asCredentials(DeviceCredentialsRepresentation credentials) {
    	return DeviceCredentials.forDevice(credentials.getId(), credentials.getTenantId());
    }
}
