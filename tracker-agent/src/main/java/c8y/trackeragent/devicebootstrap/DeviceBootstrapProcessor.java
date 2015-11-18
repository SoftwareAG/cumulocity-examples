package c8y.trackeragent.devicebootstrap;

import static com.cumulocity.sdk.client.polling.PollingStrategy.DEFAULT_POLL_INTERVALS;
import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.event.TrackerAgentEventListener;
import c8y.trackeragent.event.TrackerAgentEvents;

import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;
import com.cumulocity.sdk.client.polling.PollingStrategy;
import com.google.common.eventbus.Subscribe;

public class DeviceBootstrapProcessor implements TrackerAgentEventListener {

    private static final int POOL_SIZE = 2;

    protected static Logger logger = LoggerFactory.getLogger(DeviceBootstrapProcessor.class);

    private final ExecutorService threadPoolExecutor;
    private final Collection<String> duringBootstrap = new HashSet<String>();
    private final Object lock = new Object();
    private final TrackerAgent trackerAgent;
    private final DeviceCredentialsApi deviceCredentialsApi;

    public DeviceBootstrapProcessor(TrackerAgent trackerAgent) {
        this.trackerAgent = trackerAgent;
        this.threadPoolExecutor = Executors.newFixedThreadPool(POOL_SIZE);
        this.deviceCredentialsApi = trackerAgent.getContext().getBootstrapPlatform().getDeviceCredentialsApi();
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
            duringBootstrap.add(imei);
            DeviceBootstrapTask deviceBootstrapTask = new DeviceBootstrapTask(imei);
            threadPoolExecutor.execute(deviceBootstrapTask);
        }
    }

    private class DeviceBootstrapTask implements Runnable {

        private final String imei;

        public DeviceBootstrapTask(String imei) {
            this.imei = imei;
        }

        @Override
        public void run() {
            try {
                doRun();
            } finally {
                duringBootstrap.remove(imei);
            }
        }

        private void doRun() {
            PollingStrategy strategy = new PollingStrategy(TimeUnit.SECONDS, asList(DEFAULT_POLL_INTERVALS));
            DeviceCredentialsRepresentation credentialsRepresentation = deviceCredentialsApi.pollCredentials(imei, strategy);
            logger.info("Send credentials representation {}.", credentialsRepresentation);
            if (credentialsRepresentation == null) {
                logger.info("No credentials accessed for imei {}.", imei);
            } else {
                DeviceCredentials credentials = asCredentials(credentialsRepresentation);
                logger.warn("Credentials for imei {} accessed: {}.", imei, credentials);
                trackerAgent.sendEvent(new TrackerAgentEvents.NewDeviceRegisteredEvent(credentials));
            }
        }

        private DeviceCredentials asCredentials(DeviceCredentialsRepresentation credentials) {
            //@formatter:off
            return new DeviceCredentials()
                .setPassword(credentials.getPassword())
                .setUser(credentials.getUsername())
                .setTenantId(credentials.getTenantId())
                .setImei(credentials.getId());
            //@formatter:on
        }

    }
}
