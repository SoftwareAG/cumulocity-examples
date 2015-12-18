package c8y.trackeragent.operations;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.logging.LoggingService;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.TrackerPlatform;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.utils.TrackerContext;

public class OperationDispatchers {

    private static Logger logger = LoggerFactory.getLogger(OperationDispatchers.class);

    private static final int THREAD_POOL_SIZE = 10;

    private final TrackerContext trackerContext;
    private final TrackerAgent trackerAgent;
    private final ScheduledExecutorService operationsExecutor;
    private final DeviceContextService contextService;
    private final LoggingService loggingService;

    public OperationDispatchers(TrackerContext trackerContext, TrackerAgent trackerAgent, DeviceContextService contextService, LoggingService loggingService) {
        this.trackerContext = trackerContext;
        this.trackerAgent = trackerAgent;
        this.loggingService = loggingService;
        this.contextService = contextService;
        this.operationsExecutor = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
    }

    public void startPollerFor(DeviceCredentials credentials) {
        TrackerDevice trackerDevice = trackerAgent.getOrCreateTrackerDevice(credentials.getImei());
        TrackerPlatform devicePlatform = trackerContext.getDevicePlatform(credentials.getImei());
        // Could be replace by device control notifications
        OperationDispatcher task = new OperationDispatcher(devicePlatform, trackerDevice, loggingService, contextService, credentials);
        task.startPolling(operationsExecutor);
        logger.info("Started operation polling for device {}.", credentials.getImei());
    }
}