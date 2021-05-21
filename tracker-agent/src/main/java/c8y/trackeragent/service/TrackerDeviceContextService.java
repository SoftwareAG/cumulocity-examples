package c8y.trackeragent.service;

import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.UserCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.device.TrackerDeviceProvider;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import c8y.trackeragent.protocol.TrackingProtocol;

@Component
public class TrackerDeviceContextService {

	protected static Logger logger = LoggerFactory.getLogger(TrackerDeviceContextService.class);

//	private final ContextService<DeviceCredentials> contextService;
	private final ContextService<UserCredentials> contextService;
    private final DeviceCredentialsRepository credentialsRepository;
    private final TrackerDeviceProvider trackerDeviceFactory;
    
    @Autowired
	public TrackerDeviceContextService(ContextService<UserCredentials> contextService, DeviceCredentialsRepository credentialsRepository, TrackerDeviceProvider trackerDeviceFactory) {
		this.contextService = contextService;
		this.credentialsRepository = credentialsRepository;
		this.trackerDeviceFactory = trackerDeviceFactory;
	}
    
	public void executeWithContext(String tenant, Runnable runnable) {
		executeWithContext(credentialsRepository.getAgentCredentials(tenant), runnable);
	}

    public void executeWithContext(String tenant, String imei, Runnable runnable) {
		DeviceCredentials cred = credentialsRepository.getAgentCredentials(tenant);
		TrackerDevice device = trackerDeviceFactory.getOrCreate(tenant, imei);
		DeviceCredentials credWithDevice =  DeviceCredentials.forAgent(cred.getTenant(), cred.getUsername(), cred.getPassword(), device.getGId());
		executeWithContext(credWithDevice, runnable);
	}
    
    public void executeWithContext(String tenant, String imei, TrackingProtocol trackingProtocol, Runnable runnable) {
        DeviceCredentials cred = credentialsRepository.getAgentCredentials(tenant);
        TrackerDevice device = trackerDeviceFactory.getOrCreate(tenant, imei);
        if (trackingProtocol != null) {
            device.setTrackingProtocolInfo(trackingProtocol);
        }
        DeviceCredentials credWithDevice =  DeviceCredentials.forAgent(cred.getTenant(), cred.getUsername(), cred.getPassword(), device.getGId());
        executeWithContext(credWithDevice, runnable);
    }
	
	private void executeWithContext(DeviceCredentials credentials, Runnable r) {
    	contextService.runWithinContext(credentials, r);
	}
}
