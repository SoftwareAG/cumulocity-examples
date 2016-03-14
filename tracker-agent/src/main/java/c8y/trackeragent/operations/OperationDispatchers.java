package c8y.trackeragent.operations;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.logging.LoggingService;

import c8y.trackeragent.TrackerPlatform;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import c8y.trackeragent.utils.TrackerPlatformProvider;

@Component
public class OperationDispatchers {

    private static Logger logger = LoggerFactory.getLogger(OperationDispatchers.class);

    private static final int THREAD_POOL_SIZE = 10;

    private final ScheduledExecutorService operationsExecutor;
    private final DeviceContextService contextService;
    private final LoggingService loggingService;
    private final TrackerPlatformProvider platformProvider;
    

    @Autowired
    public OperationDispatchers(TrackerPlatformProvider platformProvider, 
            DeviceContextService contextService, LoggingService loggingService, DeviceCredentialsRepository DeviceCredentialsRepository) {
        this.platformProvider = platformProvider;
        this.loggingService = loggingService;
        this.contextService = contextService;
        this.operationsExecutor = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
    }

    public void startPollerFor(DeviceCredentials tenantCredentials) {
        TrackerPlatform tenantPlatform = platformProvider.getTenantPlatform(tenantCredentials.getTenant());
        // Could be replace by device control notifications
        OperationDispatcher task = new OperationDispatcher(tenantPlatform, tenantCredentials, loggingService, contextService);
        task.startPolling(operationsExecutor);
        logger.info("Started operation polling for tenant {}.", tenantCredentials);
    }
}