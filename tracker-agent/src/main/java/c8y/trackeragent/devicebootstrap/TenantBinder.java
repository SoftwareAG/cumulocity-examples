package c8y.trackeragent.devicebootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.agent.server.context.DeviceContext;
import com.cumulocity.agent.server.context.DeviceContextService;
import com.google.common.eventbus.Subscribe;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerPlatform;
import c8y.trackeragent.event.TrackerAgentEventListener;
import c8y.trackeragent.event.TrackerAgentEvents.NewTenantRegisteredEvent;
import c8y.trackeragent.operations.OperationDispatcher;
import c8y.trackeragent.operations.OperationDispatchers;

@Component
public class TenantBinder implements TrackerAgentEventListener {
    
    private static Logger logger = LoggerFactory.getLogger(TenantBinder.class);

    private final TrackerAgent agent;
    private final OperationDispatchers operationDispatchers;
    private final DeviceCredentialsRepository deviceCredentialsRepository;
    private final DeviceContextService contextService;
    private final DeviceBootstrapProcessor deviceBootstrapProcessor;

    @Autowired
    public TenantBinder(
            TrackerAgent agent,
            OperationDispatchers operationDispatchers, 
            DeviceContextService contextService, 
            DeviceBootstrapProcessor deviceBootstrapProcessor, 
            DeviceCredentialsRepository deviceCredentialsRepository) {
        this.agent = agent;
        this.operationDispatchers = operationDispatchers;
        this.deviceBootstrapProcessor = deviceBootstrapProcessor;
        this.deviceCredentialsRepository = deviceCredentialsRepository;
        this.contextService = contextService;
    }
    
    @Subscribe
    public void listen(NewTenantRegisteredEvent event) {  
        bind(event.getTenant());
    }
    
    public void init() {
        agent.registerEventListener(deviceBootstrapProcessor, this);
        for (DeviceCredentials deviceCredentials : agent.getContext().getAllTenantCredentials()) {
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
        contextService.runWithinContext(new DeviceContext(credentials), new Runnable() {
            
            @Override
            public void run() {
                logger.info("Bind new tenant " + tenant);
                operationDispatchers.startPollerFor(credentials);
                logger.info("Tenant " + tenant + " bound successfully.");
            }
        });
    }
    

}
