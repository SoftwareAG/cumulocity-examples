package com.cumulocity.agent.snmp.platform.service;

import com.cumulocity.agent.snmp.configuration.SnmpAgentGatewayProperties;
import com.cumulocity.agent.snmp.service.DeviceCredentialsStoreService;
import com.cumulocity.model.authentication.CumulocityBasicCredentials;
import com.cumulocity.rest.representation.BaseResourceRepresentation;
import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformBuilder;
import com.cumulocity.sdk.client.PlatformParameters;
import com.cumulocity.sdk.client.ResponseMapper;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A service to provide access to Platform objects and its exposed API classes.
 * This service also initializes the Platform object when the device credentials are available,
 * monitors and maintains the Platform status (available/unavailable).
 */

@Service
@Slf4j
public class SnmpAgentGatewayService {

    private ExecutorService executorServiceForMonitoringPlatformAvailability = Executors.newSingleThreadExecutor();

    private final Object RESOURCE_TO_NOTIFY_WHEN_PLATFORM_IS_AVAILABLE = new Object();
    private final Object RESOURCE_TO_NOTIFY_WHEN_PLATFORM_IS_UNAVAILABLE = new Object();

    @Autowired
    private SnmpAgentGatewayProperties snmpAgentGatewayProperties;

    @Autowired
    private DeviceCredentialsStoreService deviceCredentialsStoreService;

    private Platform platformWithDeviceCredentials;

    private MeasurementApi measurementApi;
    private AlarmApi alarmApi;
    private EventApi eventApi;

    private volatile boolean isPlatformAvailable = false;

    @PostConstruct
    private void startPlatformAvailabilityMonitor() {
        executorServiceForMonitoringPlatformAvailability.execute(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    if (!isPlatformAvailable()) {
                        boolean isPlatformAvailable = initializePlatformWithDeviceCredentials();
                        if(isPlatformAvailable) {
                            markPlatfromAsAvailable();
                        }
                        else {
                            // Sleep before retry
                            Thread.sleep(10000);
                        }
                    } else {
                        synchronized (RESOURCE_TO_NOTIFY_WHEN_PLATFORM_IS_UNAVAILABLE) {
                            RESOURCE_TO_NOTIFY_WHEN_PLATFORM_IS_UNAVAILABLE.wait();
                        }
                    }
                } catch (InterruptedException ie) {
                    // Ignore this exception and continue to execute the while loop
                    // which will be anyway terminated by !Thread.currentThread().isInterrupted() check
                    log.debug("Thread monitoring the platform availability is interrupted.", ie);
                }
            }
        });
    }

    @PreDestroy
    private void stopPlatformAvailabilityMonitor() {
        executorServiceForMonitoringPlatformAvailability.shutdownNow(); // Force shutdown the monitor thread
    }

    public Platform getPlatformWithDeviceCredentials() {
        if(this.platformWithDeviceCredentials == null) {
            throw new IllegalStateException("Platform not initialized with device credentials.");
        }

        return this.platformWithDeviceCredentials;
    }

    public MeasurementApi getMeasurementApi() {
        if(this.measurementApi == null) {
            throw new IllegalStateException("Platform not initialized with device credentials.");
        }

        return this.measurementApi;
    }

    public AlarmApi getAlarmApi() {
        if(this.alarmApi == null) {
            throw new IllegalStateException("Platform not initialized with device credentials.");
        }

        return this.alarmApi;
    }

    public EventApi getEventApi() {
        if(this.eventApi == null) {
            throw new IllegalStateException("Platform not initialized with device credentials.");
        }

        return this.eventApi;
    }

    public boolean isPlatformAvailable() {
        return isPlatformAvailable;
    }

    public void markPlatfromAsUnavailable() {
        isPlatformAvailable = false;

        synchronized (RESOURCE_TO_NOTIFY_WHEN_PLATFORM_IS_UNAVAILABLE) {
            RESOURCE_TO_NOTIFY_WHEN_PLATFORM_IS_UNAVAILABLE.notifyAll();
        }
    }

    public void markPlatfromAsAvailable() {
        isPlatformAvailable = true;

        synchronized (RESOURCE_TO_NOTIFY_WHEN_PLATFORM_IS_AVAILABLE) {
            RESOURCE_TO_NOTIFY_WHEN_PLATFORM_IS_AVAILABLE.notifyAll();
        }
    }

    public void waitForPlatformToBeAvailable() throws InterruptedException {
        synchronized (RESOURCE_TO_NOTIFY_WHEN_PLATFORM_IS_AVAILABLE) {
            RESOURCE_TO_NOTIFY_WHEN_PLATFORM_IS_AVAILABLE.wait();
        }
    }

    @Bean(name = "platformWithBootstrapCredentials")
    public Platform accessPlatformWithBootstrapCredentials() {
        CumulocityBasicCredentials credentials = CumulocityBasicCredentials.builder()
                .tenantId(snmpAgentGatewayProperties.getTenant())
                .username(snmpAgentGatewayProperties.getUser())
                .password(snmpAgentGatewayProperties.getPassword())
                .build();

        return PlatformBuilder.platform()
                .withBaseUrl(snmpAgentGatewayProperties.getUrl())
                .withForceInitialHost(snmpAgentGatewayProperties.isForceInitialHost())
                .withCredentials(credentials)
                .build();
    }

    private boolean initializePlatformWithDeviceCredentials() {
        DeviceCredentialsRepresentation deviceCredentials = deviceCredentialsStoreService.fetch();
        if(deviceCredentials == null) {
            log.error("Agent initialization failed as device credentials are not initialized yet.");
            clearPlatformObjects();
            return false;
        }

        try {
            CumulocityBasicCredentials credentials = CumulocityBasicCredentials.builder()
                    .tenantId(deviceCredentials.getTenantId())
                    .username(deviceCredentials.getUsername())
                    .password(deviceCredentials.getPassword())
                    .build();
            this.platformWithDeviceCredentials = PlatformBuilder.platform()
                    .withBaseUrl(snmpAgentGatewayProperties.getUrl())
                    .withForceInitialHost(snmpAgentGatewayProperties.isForceInitialHost())
                    .withCredentials(credentials)
                    .build();

            ((PlatformParameters) this.platformWithDeviceCredentials).setResponseMapper(
                    new ResponseMapper() {
                        @Override
                        public CharSequence write(Object o) {
                            if (o instanceof BaseResourceRepresentation && o.getClass().getName().startsWith("com.cumulocity.agent.snmp")) {
                                return ((BaseResourceRepresentation) o).toJSON();
                            }

                            return null;
                        }

                        @Override
                        public <T> T read(InputStream inputStream, Class<T> aClass) {
                            return null;
                        }
                    }
            );

            this.measurementApi = this.platformWithDeviceCredentials.getMeasurementApi();
            this.alarmApi = this.platformWithDeviceCredentials.getAlarmApi();
            this.eventApi = this.platformWithDeviceCredentials.getEventApi();
        } catch(Throwable t) {
            log.error("Agent initialization failed as the Platform is unavailable or the device credentials are incorrect.", t);
            clearPlatformObjects();
            return false;
        }

        return true;
    }

    private void clearPlatformObjects() {
        this.platformWithDeviceCredentials = null;
        this.measurementApi = null;
        this.alarmApi = null;
        this.eventApi = null;
    }
}
