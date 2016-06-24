package c8y.trackeragent.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.agent.server.context.DeviceContext;
import com.cumulocity.agent.server.context.DeviceContextService;

import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.device.TrackerDeviceProvider;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;

@Component
public class TrackerDeviceContextService {
	
	protected static Logger logger = LoggerFactory.getLogger(TrackerDeviceContextService.class);
	
    private final DeviceContextService contextService;
    private final DeviceCredentialsRepository credentialsRepository;
    private final TrackerDeviceProvider trackerDeviceFactory;
    
    @Autowired
	public TrackerDeviceContextService(DeviceContextService contextService, DeviceCredentialsRepository credentialsRepository, TrackerDeviceProvider trackerDeviceFactory) {
		this.contextService = contextService;
		this.credentialsRepository = credentialsRepository;
		this.trackerDeviceFactory = trackerDeviceFactory;
	}
	
	public void enterContext(String tenant) {
		enterContext(credentialsRepository.getAgentCredentials(tenant));
	}
	
	public void enterContext(String tenant, String imei) {
		DeviceCredentials cred = credentialsRepository.getAgentCredentials(tenant);
		TrackerDevice device = trackerDeviceFactory.getOrCreate(tenant, imei);
		DeviceCredentials credWithDevice =  DeviceCredentials.forAgent(cred.getTenant(), cred.getUsername(), cred.getPassword(), device.getGId());
		enterContext(credWithDevice);
	}
	
	private void enterContext(DeviceCredentials credentials) {
		contextService.enterContext(new DeviceContext(credentials));
	}
	
	public void leaveContext() {
		contextService.leaveContext();
	}

}
