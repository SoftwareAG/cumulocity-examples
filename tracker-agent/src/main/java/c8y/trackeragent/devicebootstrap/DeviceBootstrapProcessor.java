package c8y.trackeragent.devicebootstrap;

import static c8y.trackeragent.devicebootstrap.DeviceBootstrapStatus.BOOTSTRAPED;
import static c8y.trackeragent.devicebootstrap.DeviceBootstrapStatus.WAITING_FOR_AGENT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.event.TrackerAgentEventListener;
import c8y.trackeragent.event.TrackerAgentEvents;
import c8y.trackeragent.utils.TrackerPlatformProvider;

import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;
import com.google.common.eventbus.Subscribe;

@Component
public class DeviceBootstrapProcessor implements TrackerAgentEventListener {

    protected static Logger logger = LoggerFactory.getLogger(DeviceBootstrapProcessor.class);

    private final TrackerAgent trackerAgent;
    private final DeviceCredentialsApi deviceCredentialsApi;
    private final DeviceCredentialsRepository deviceCredentialsRepository;
    private final TrackerPlatformProvider trackerPlatformProvider;

    @Autowired
    public DeviceBootstrapProcessor(TrackerAgent trackerAgent, TrackerPlatformProvider trackerPlatformProvider, 
    		DeviceCredentialsRepository deviceCredentialsRepository) {
        this.trackerAgent = trackerAgent;
		this.deviceCredentialsRepository = deviceCredentialsRepository;
		//TODO make it by spring
        this.deviceCredentialsApi = trackerAgent.getContext().getBootstrapPlatform().getDeviceCredentialsApi();
        this.trackerPlatformProvider = trackerPlatformProvider;
    }

    @Subscribe
    public void listen(TrackerAgentEvents.NewDeviceEvent event) {
        startDeviceBootstraping(event.getImei());
    }
    
    @Subscribe
    public void listen(TrackerAgentEvents.NewTenantEvent event) {
    	startAgentBootstraping(event.getTenant());
    }

    public void startDeviceBootstraping(String imei) {    
        logger.info("Start bootstrapping: {}", imei);
        DeviceCredentialsRepresentation credentialsRepresentation = pollCredentials(imei);
        if (credentialsRepresentation == null) {
            logger.info("No credentials accessed for imei {}.", imei);
            return;
        } 
        onNewDeviceCredentials(credentialsRepresentation);
    }
    
    public void startAgentBootstraping(String tenant) {    
    	logger.info("Start bootstrapping agent for tenant: {}", tenant);
    	String newDeviceRequestId = "tracker-agent-" + tenant;
    	DeviceCredentialsRepresentation credentialsRepresentation = pollCredentials(newDeviceRequestId);
    	if (credentialsRepresentation == null) {
    		logger.info("No credentials accessed for tenant agent {}.", tenant);
    		return;
    	} 
    	onNewAgentCredentials(credentialsRepresentation);
    }

	private void onNewAgentCredentials(DeviceCredentialsRepresentation credentialsRep) {
		DeviceCredentials agentCredentials = DeviceCredentials.forAgent(credentialsRep.getTenantId(), credentialsRep.getUsername(), credentialsRep.getPassword());
		deviceCredentialsRepository.saveAgentCredentials(agentCredentials);
		trackerPlatformProvider.getTenantPlatform(agentCredentials.getTenant());
		trackerAgent.sendEvent(new TrackerAgentEvents.NewTenantRegisteredEvent(agentCredentials.getTenant()));
	}

	private void onNewDeviceCredentials(DeviceCredentialsRepresentation credentialsRep) {
		boolean hasAgentCredentials = deviceCredentialsRepository.hasAgentCredentials(credentialsRep.getTenantId());
		DeviceBootstrapStatus bootstrapStatus = hasAgentCredentials ? BOOTSTRAPED : WAITING_FOR_AGENT;
		DeviceCredentials credentials = DeviceCredentials.forDevice(credentialsRep.getId(), credentialsRep.getTenantId(), bootstrapStatus); 
		logger.info("Credentials for imei {} accessed: {}.", credentials.getImei(), credentials);
		deviceCredentialsRepository.saveDeviceCredentials(credentials);
		trackerAgent.sendEvent(new TrackerAgentEvents.NewDeviceRegisteredEvent(credentials));
		if (!hasAgentCredentials) {
			trackerAgent.sendEvent(new TrackerAgentEvents.NewTenantEvent(credentials.getTenant()));
		}
	}
    
    private DeviceCredentialsRepresentation pollCredentials(final String newDeviceRequestId) {
        try {
            return deviceCredentialsApi.pollCredentials(newDeviceRequestId);
        } catch (SDKException e) {
            if (e.getHttpStatus() == HttpStatus.NOT_FOUND.value()) {
                logger.debug("Credentials not yet available for device: " + newDeviceRequestId);
            } else {
                logger.error("Failed to retrieve credentials from cumulocity.", e);
            }
        }
        return null;
    }
}
