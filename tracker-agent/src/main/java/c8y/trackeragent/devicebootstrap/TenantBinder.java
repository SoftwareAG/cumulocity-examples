package c8y.trackeragent.devicebootstrap;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.logging.LoggingService;

import c8y.trackeragent.TrackerPlatform;
import c8y.trackeragent.operations.OperationDispatcher;
import c8y.trackeragent.utils.TrackerPlatformProvider;

@Component
public class TenantBinder {
    
    private static Logger logger = LoggerFactory.getLogger(TenantBinder.class);

    private final DeviceCredentialsRepository deviceCredentialsRepository;
    private final DeviceContextService contextService;
    private final TrackerPlatformProvider platformProvider;
    private final LoggingService loggingService;
	private final ScheduledExecutorService operationsExecutor;
    
    private static final int OPERATIONS_THREAD_POOL_SIZE = 10;

    @Autowired
    public TenantBinder(
            DeviceContextService contextService, 
            DeviceCredentialsRepository deviceCredentialsRepository, 
            TrackerPlatformProvider platformProvider, 
            LoggingService loggingService) {
        this.deviceCredentialsRepository = deviceCredentialsRepository;
        this.contextService = contextService;
		this.platformProvider = platformProvider;
		this.loggingService = loggingService;
		this.operationsExecutor = Executors.newScheduledThreadPool(OPERATIONS_THREAD_POOL_SIZE);
    }
    
    public void init() {
        for (DeviceCredentials deviceCredentials : deviceCredentialsRepository.getAllAgentCredentials()) {
            try {
                logger.debug("bind IMEI {}", deviceCredentials.getTenant());
                bind(deviceCredentials.getTenant());
            } catch (Exception e) {
                logger.error("Failed to initialize device: " + deviceCredentials.getImei());
            }
        }
    }
    
    public void bind(final String tenant) {
    	final DeviceCredentials credentials = deviceCredentialsRepository.getAgentCredentials(tenant);
    	logger.info("Bind new tenant " + tenant);
    	startPollerFor(credentials);
    	logger.info("Tenant " + tenant + " bound successfully.");
    }
    
    public void startPollerFor(DeviceCredentials tenantCredentials) {
        TrackerPlatform tenantPlatform = platformProvider.getTenantPlatform(tenantCredentials.getTenant());
        // Could be replace by device control notifications
        OperationDispatcher task = new OperationDispatcher(tenantPlatform, tenantCredentials, loggingService, contextService);
        task.startPolling(operationsExecutor);
        logger.info("Started operation polling for tenant {}.", tenantCredentials);
    }

}
