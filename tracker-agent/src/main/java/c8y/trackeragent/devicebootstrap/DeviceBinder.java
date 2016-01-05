package c8y.trackeragent.devicebootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.event.TrackerAgentEventListener;
import c8y.trackeragent.event.TrackerAgentEvents.NewDeviceRegisteredEvent;
import c8y.trackeragent.logger.TracelogAppenders;
import c8y.trackeragent.operations.OperationDispatchers;

import com.cumulocity.agent.server.context.DeviceContext;
import com.cumulocity.agent.server.context.DeviceContextService;
import com.google.common.eventbus.Subscribe;

public class DeviceBinder implements TrackerAgentEventListener {
    
    private static Logger logger = LoggerFactory.getLogger(DeviceBinder.class);

    private final OperationDispatchers operationDispatchers;
    private final TracelogAppenders tracelogAppenders;
    private final DeviceCredentialsRepository deviceCredentialsRepository;
    private final DeviceContextService contextService;

    //@formatter:off
    public DeviceBinder(
            OperationDispatchers operationDispatchers, 
            TracelogAppenders tracelogAppenders, 
            DeviceCredentialsRepository deviceCredentialsRepository,
            DeviceContextService contextService) {
        this.operationDispatchers = operationDispatchers;
        this.tracelogAppenders = tracelogAppenders;
        this.deviceCredentialsRepository = deviceCredentialsRepository;
        this.contextService = contextService;
        //@formatter:on
    }
    
    @Subscribe
    public void listen(NewDeviceRegisteredEvent event) {  
        DeviceCredentials credentials = event.getDeviceCredentials();
        deviceCredentialsRepository.saveCredentials(credentials);
        bind(credentials);
        
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
