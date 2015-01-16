package c8y.trackeragent.devicebootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.event.TrackerAgentEventListener;
import c8y.trackeragent.event.TrackerAgentEvents.NewDeviceRegisteredEvent;
import c8y.trackeragent.logger.TracelogAppenders;
import c8y.trackeragent.operations.OperationDispatchers;

import com.google.common.eventbus.Subscribe;

public class DeviceBinder implements TrackerAgentEventListener {
    
    private static Logger logger = LoggerFactory.getLogger(DeviceBinder.class);

    private final OperationDispatchers operationDispatchers;
    private final TracelogAppenders tracelogAppenders;
    private final DeviceCredentialsRepository deviceCredentialsRepository;

    //@formatter:off
    public DeviceBinder(
            OperationDispatchers operationDispatchers, 
            TracelogAppenders tracelogAppenders, 
            DeviceCredentialsRepository deviceCredentialsRepository) {
        this.operationDispatchers = operationDispatchers;
        this.tracelogAppenders = tracelogAppenders;
        this.deviceCredentialsRepository = deviceCredentialsRepository;
        //@formatter:on
    }
    
    @Subscribe
    public void listen(NewDeviceRegisteredEvent event) {  
        DeviceCredentials credentials = event.getDeviceCredentials();
        logger.info("Bind new device " + credentials.getImei());
        deviceCredentialsRepository.saveCredentials(credentials);
        bind(credentials.getImei());
        logger.info("Device " + credentials.getImei() + " bound successfully.");
    }
    
    public void bind(String imei) {
        operationDispatchers.startPollerFor(imei);
        tracelogAppenders.start(imei);
    }

}
