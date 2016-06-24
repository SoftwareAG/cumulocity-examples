package c8y.trackeragent.devicebootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.utils.TrackerPlatformProvider;

@Component
public class DeviceBootstrapProcessor {

    protected static Logger logger = LoggerFactory.getLogger(DeviceBootstrapProcessor.class);

    private final DeviceCredentialsApi deviceCredentialsApi;
    private final DeviceCredentialsRepository credentialsRepository;
    private final TrackerPlatformProvider platformProvider;
    private final TenantBinder tenantBinder;

    @Autowired
    public DeviceBootstrapProcessor(TrackerAgent trackerAgent, TrackerPlatformProvider trackerPlatformProvider, 
    		DeviceCredentialsRepository deviceCredentialsRepository, TenantBinder tenantBinder) {
		this.credentialsRepository = deviceCredentialsRepository;
		this.tenantBinder = tenantBinder;
        this.platformProvider = trackerPlatformProvider;
        this.deviceCredentialsApi = platformProvider.getBootstrapPlatform().getDeviceCredentialsApi();
    }
    
    public DeviceCredentials tryAccessDeviceCredentials(String imei) {    
        logger.info("Start bootstrapping: {}", imei);
        DeviceCredentialsRepresentation credentialsRepresentation = pollCredentials(imei);
        if (credentialsRepresentation == null) {
        	return null;
        } else {
        	return onNewDeviceCredentials(credentialsRepresentation);            
        }
    }
    
    public DeviceCredentials tryAccessAgentCredentials(String tenant) {    
    	logger.info("Start bootstrapping agent for tenant: {}", tenant);
    	String newDeviceRequestId = "tracker-agent-" + tenant;
    	DeviceCredentialsRepresentation credentialsRepresentation = pollCredentials(newDeviceRequestId);
    	if (credentialsRepresentation == null) {
    		return null;
    	} else {
    		return onNewAgentCredentials(credentialsRepresentation);    		
    	}
    }

	private DeviceCredentials onNewAgentCredentials(DeviceCredentialsRepresentation credentialsRep) {
		DeviceCredentials credentials = DeviceCredentials.forAgent(credentialsRep.getTenantId(), credentialsRep.getUsername(), credentialsRep.getPassword());
		credentialsRepository.saveAgentCredentials(credentials);
		//platformProvider.initTenantPlatform(credentials.getTenant());
		tenantBinder.bind(credentials.getTenant());
		logger.info("Agent for tenant {} bootstraped. Following devices start working: {}",
				credentials.getTenant(), credentialsRepository.getAllDeviceCredentials(credentials.getTenant()));
		return credentials;
	}

	private DeviceCredentials onNewDeviceCredentials(DeviceCredentialsRepresentation credentialsRep) {
		boolean hasAgentCredentials = credentialsRepository.hasAgentCredentials(credentialsRep.getTenantId());
		DeviceCredentials credentials = DeviceCredentials.forDevice(credentialsRep.getId(), credentialsRep.getTenantId()); 
		logger.info("Credentials for imei {} accessed: {}.", credentials.getImei(), credentials);
		credentialsRepository.saveDeviceCredentials(credentials);
		if (!hasAgentCredentials) {
			tryAccessAgentCredentials(credentials.getTenant());
		}
		return credentials;
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
