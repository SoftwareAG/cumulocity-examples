package c8y.trackeragent.operations;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.TrackerPlatform;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.logging.LoggingService;

@Component
public class OperationDispatchers {

    private static Logger logger = LoggerFactory.getLogger(OperationDispatchers.class);

    private static final int THREAD_POOL_SIZE = 10;

    private final TrackerAgent trackerAgent;
    private final ScheduledExecutorService operationsExecutor;
    private final DeviceContextService contextService;
    private final LoggingService loggingService;

    @Autowired
    public OperationDispatchers(TrackerAgent trackerAgent, 
            DeviceContextService contextService, LoggingService loggingService) {
        this.trackerAgent = trackerAgent;
        this.loggingService = loggingService;
        this.contextService = contextService;
        this.operationsExecutor = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
    }

    public void startPollerFor(DeviceCredentials credentials) {
        TrackerDevice trackerDevice = trackerAgent.getOrCreateTrackerDevice(credentials.getImei());
        TrackerPlatform devicePlatform = trackerAgent.getContext().getDevicePlatform(credentials.getImei());
        // Could be replace by device control notifications
        OperationDispatcher task = new OperationDispatcher(devicePlatform, trackerDevice, loggingService, contextService, credentials);
        task.startPolling(operationsExecutor);
        logger.info("Started operation polling for device {}.", credentials.getImei());
    }
}