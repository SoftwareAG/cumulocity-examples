package c8y.trackeragent.devicebootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.event.TrackerAgentEventListener;
import c8y.trackeragent.event.TrackerAgentEvents.NewDeviceRegisteredEvent;
import c8y.trackeragent.logger.TracelogAppenders;
import c8y.trackeragent.operations.OperationDispatchers;

import com.cumulocity.agent.server.context.DeviceContext;
import com.cumulocity.agent.server.context.DeviceContextService;
import com.google.common.eventbus.Subscribe;

@Component
public class DeviceBinder implements TrackerAgentEventListener {
    
    private static Logger logger = LoggerFactory.getLogger(DeviceBinder.class);

    private final TrackerAgent agent;
    private final OperationDispatchers operationDispatchers;
    private final TracelogAppenders tracelogAppenders;
    private final DeviceCredentialsRepository deviceCredentialsRepository;
    private final DeviceContextService contextService;
    private final DeviceBootstrapProcessor deviceBootstrapProcessor;

    @Autowired
    public DeviceBinder(
            TrackerAgent agent,
            OperationDispatchers operationDispatchers, 
            TracelogAppenders tracelogAppenders, 
            DeviceContextService contextService, 
            DeviceBootstrapProcessor deviceBootstrapProcessor) {
        this.agent = agent;
        this.operationDispatchers = operationDispatchers;
        this.tracelogAppenders = tracelogAppenders;
        this.deviceBootstrapProcessor = deviceBootstrapProcessor;
        this.deviceCredentialsRepository = DeviceCredentialsRepository.get();
        this.contextService = contextService;
    }
    
    @Subscribe
    public void listen(NewDeviceRegisteredEvent event) {  
        DeviceCredentials credentials = event.getDeviceCredentials();
        deviceCredentialsRepository.saveCredentials(credentials);
        bind(credentials);
    }
    
    public void init() {
        agent.registerEventListener(deviceBootstrapProcessor, this);
        for (DeviceCredentials deviceCredentials : agent.getContext().getDeviceCredentials()) {
            try {
                logger.debug("bind IMEI {}", deviceCredentials.getImei());
                bind(deviceCredentials);
            } catch (Exception e) {
                logger.error("Failed to initialize device: " + deviceCredentials.getImei());
            }
        }
    }
    
    public void bind(final DeviceCredentials credentials) {
        contextService.runWithinContext(new DeviceContext(credentials), new Runnable() {
            
            @Override
            public void run() {
                logger.info("Bind new device " + credentials.getImei());
                operationDispatchers.startPollerFor(credentials);
                tracelogAppenders.start(credentials.getImei());
                logger.info("Device " + credentials.getImei() + " bound successfully.");
            }
        });
    }

}
