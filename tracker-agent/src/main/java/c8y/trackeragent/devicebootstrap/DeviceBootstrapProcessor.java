package c8y.trackeragent.devicebootstrap;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.TrackerPlatform;
import c8y.trackeragent.repository.DeviceCredentials;
import c8y.trackeragent.repository.DeviceCredentialsRepository;
import c8y.trackeragent.utils.TrackerContext;

import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;

public class DeviceBootstrapProcessor {

    private static final int POOL_SIZE = 2;
    public static final int POLL_CREDENTIALS_TIMEOUT = 60;
    public static final int POLL_CREDENTIALS_INTERVAL = 5;

    protected static Logger logger = LoggerFactory.getLogger(DeviceBootstrapProcessor.class);

    private static DeviceBootstrapProcessor instance = new DeviceBootstrapProcessor();

    private final ExecutorService threadPoolExecutor;
    private final TrackerPlatform platform;
    private Collection<String> duringBootstrap = new HashSet<>();

    private DeviceBootstrapProcessor() {
        threadPoolExecutor = Executors.newFixedThreadPool(POOL_SIZE);
        platform = TrackerContext.get().getPlatforms().get(0);
    }

    public static DeviceBootstrapProcessor get() {
        return instance;
    }

    public synchronized void startBootstaping(String imei) {
        if (duringBootstrap.contains(imei)) {
            return;
        }
        duringBootstrap.add(imei);
        try {
            DeviceBootstrapTask deviceBootstrapTask = new DeviceBootstrapTask(platform.getDeviceCredentialsApi(), imei);
            threadPoolExecutor.execute(deviceBootstrapTask);
        } finally {
            duringBootstrap.remove(imei);
        }
    }

    static class DeviceBootstrapTask implements Runnable {

        private final DeviceCredentialsApi deviceCredentialsApi;
        private final String imei;

        public DeviceBootstrapTask(DeviceCredentialsApi deviceCredentialsApi, String imei) {
            this.deviceCredentialsApi = deviceCredentialsApi;
            this.imei = imei;
        }

        @Override
        public void run() {
            try {
                deviceCredentialsApi.hello(imei);
            } catch (SDKException ex) {
                logger.warn("Hello from device for imei {} failed: {}.", imei, ex.getMessage());
                return;
            }
            DeviceCredentialsRepresentation credentialsRepresentation = deviceCredentialsApi.pollCredentials(imei, POLL_CREDENTIALS_INTERVAL, POLL_CREDENTIALS_TIMEOUT);
            if (credentialsRepresentation != null) {
                DeviceCredentials credentials = asCredentials(credentialsRepresentation);
                logger.warn("Credentials for imei {} accessed: {}.", imei, credentials);
                DeviceCredentialsRepository.instance().saveCredentials(imei, credentials);
            }
        }

        private static DeviceCredentials asCredentials(DeviceCredentialsRepresentation credentials) {
            DeviceCredentials deviceCredentials = new DeviceCredentials();
            deviceCredentials.setPassword(credentials.getPassword());
            deviceCredentials.setUser(credentials.getUsername());
            deviceCredentials.setTenantId(credentials.getTenantId());
            return deviceCredentials;
        }

    }
}
