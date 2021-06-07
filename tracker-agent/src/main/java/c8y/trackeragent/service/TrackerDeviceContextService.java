package c8y.trackeragent.service;

import c8y.trackeragent.tracker.MicroserviceCredentialsFactory;
import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.device.TrackerDeviceProvider;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import c8y.trackeragent.protocol.TrackingProtocol;

@Component
public class TrackerDeviceContextService {

	protected static Logger logger = LoggerFactory.getLogger(TrackerDeviceContextService.class);

	private final ContextService<MicroserviceCredentials> contextService;
	private final MicroserviceCredentialsFactory microserviceCredentialsFactory;
    private final DeviceCredentialsRepository credentialsRepository;
    private final TrackerDeviceProvider trackerDeviceFactory;
    
    @Autowired
	public TrackerDeviceContextService(
			ContextService<MicroserviceCredentials> contextService,
			MicroserviceCredentialsFactory microserviceCredentialsFactory,
			DeviceCredentialsRepository credentialsRepository,
			TrackerDeviceProvider trackerDeviceFactory) {
		this.contextService = contextService;
		this.microserviceCredentialsFactory = microserviceCredentialsFactory;
		this.credentialsRepository = credentialsRepository;
		this.trackerDeviceFactory = trackerDeviceFactory;
	}
    
	public void executeWithContext(String tenant, Runnable runnable) {
		executeWithContext(microserviceCredentialsFactory.getForTenant(tenant), runnable);
	}

    public void executeWithContext(String tenant, String imei, TrackingProtocol trackingProtocol, Runnable runnable) {
        TrackerDevice device = trackerDeviceFactory.getOrCreate(tenant, imei);
        if (trackingProtocol != null) {
            device.setTrackingProtocolInfo(trackingProtocol);
        }
        executeWithContext(microserviceCredentialsFactory.getForTenant(tenant), runnable);
    }
	
	private void executeWithContext(MicroserviceCredentials credentials, Runnable r) {
    	contextService.runWithinContext(credentials, r);
	}
}
