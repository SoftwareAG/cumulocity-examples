package c8y.trackeragent.devicebootstrap;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.event.TrackerAgentEventListener;
import c8y.trackeragent.event.TrackerAgentEvents;
import c8y.trackeragent.utils.TrackerContext;

import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;
import com.google.common.eventbus.Subscribe;
import com.sun.jersey.api.client.ClientResponse.Status;

public class DeviceBootstrapProcessor implements TrackerAgentEventListener {

    private static final int POOL_SIZE = 2;
    public static final int POLL_CREDENTIALS_TIMEOUT = 60;
    public static final int POLL_CREDENTIALS_INTERVAL = 5;

    protected static Logger logger = LoggerFactory.getLogger(DeviceBootstrapProcessor.class);

    private final ExecutorService threadPoolExecutor;
    private Collection<String> duringBootstrap = new HashSet<String>();
    private Object lock = new Object();
    private TrackerAgent trackerAgent;

    public DeviceBootstrapProcessor(TrackerAgent trackerAgent) {
        this.trackerAgent = trackerAgent;
        threadPoolExecutor = Executors.newFixedThreadPool(POOL_SIZE);
    }
    
    @Subscribe
    public void listen(TrackerAgentEvents.NewDeviceEvent event) {
        startBootstraping(event.getImei());
    }

    public void startBootstraping(String imei) {
        synchronized (lock) {
            if (duringBootstrap.contains(imei)) {
                return;
            }
            DeviceCredentialsApi deviceCredentialsApi = TrackerContext.get().getBootstrapPlatform().getDeviceCredentialsApi();
            duringBootstrap.add(imei);
            try {
                DeviceBootstrapTask deviceBootstrapTask = new DeviceBootstrapTask(trackerAgent, deviceCredentialsApi, imei);
                threadPoolExecutor.execute(deviceBootstrapTask);
            } finally {
                duringBootstrap.remove(imei);
            }        
        }
    }

    static class DeviceBootstrapTask implements Runnable {

        private final DeviceCredentialsApi deviceCredentialsApi;
        private final String imei;
        private final TrackerAgent trackerAgent;

        public DeviceBootstrapTask(TrackerAgent trackerAgent, DeviceCredentialsApi deviceCredentialsApi, String imei) {
            this.trackerAgent = trackerAgent;
            this.deviceCredentialsApi = deviceCredentialsApi;
            this.imei = imei;
        }

        @Override
        public void run() {
            try {
                deviceCredentialsApi.hello(imei);
            } catch (SDKException ex) {
                boolean notFound = Status.NOT_FOUND.getStatusCode() == ex.getHttpStatus();
                logger.warn("Hello from device for imei {} failed: {}.", imei, notFound ? "" : ex.getMessage());
                return;
            }
            logger.info("Successfully sent hello from imei {}.", imei);
            DeviceCredentialsRepresentation credentialsRepresentation = deviceCredentialsApi.pollCredentials(imei, POLL_CREDENTIALS_INTERVAL, POLL_CREDENTIALS_TIMEOUT);
            if (credentialsRepresentation == null) {
                logger.info("No credentials accessed for imei {}.", imei);                
            } else {
                DeviceCredentials credentials = asCredentials(credentialsRepresentation);
                logger.warn("Credentials for imei {} accessed: {}.", imei, credentials);
                trackerAgent.sendEvent(new TrackerAgentEvents.NewDeviceRegisteredEvent(credentials));
            }
        }

        private static DeviceCredentials asCredentials(DeviceCredentialsRepresentation credentials) {
            DeviceCredentials deviceCredentials = new DeviceCredentials();
            deviceCredentials.setPassword(credentials.getPassword());
            deviceCredentials.setUser(credentials.getUsername());
            deviceCredentials.setTenantId(credentials.getTenantId());
            deviceCredentials.setImei(credentials.getId());
            return deviceCredentials;
        }

    }
}
