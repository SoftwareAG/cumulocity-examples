package com.cumulocity.agent.snmp.platform.service;

import com.cumulocity.agent.snmp.bootstrap.model.CredentialsAvailableEvent;
import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.platform.model.PlatformConnectionReadyEvent;
import com.cumulocity.model.authentication.CumulocityBasicCredentials;
import com.cumulocity.rest.representation.BaseResourceRepresentation;
import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.sdk.client.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.Duration;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PlatformProvider implements InitializingBean {

	private final GatewayProperties gatewayProperties;

	private final GatewayProperties.SnmpProperties snmpProperties;

    private final TaskScheduler taskScheduler;

    private final ApplicationEventPublisher eventPublisher;

    private Platform bootstrapPlatform;

	private Platform platform;

    private volatile boolean isPlatformAvailable = false;

    public Platform getPlatform() {
		if (platform == null) {
			throw new IllegalStateException("Credentials are not available yet.");
		}

		return platform;
	}

	public Platform getBootstrapPlatform() {
		return bootstrapPlatform;
	}

	@Override
	public void afterPropertiesSet() {
		CumulocityBasicCredentials credentials = CumulocityBasicCredentials.builder()
				.tenantId(gatewayProperties.getBootstrapProperties().getTenantId())
				.username(gatewayProperties.getBootstrapProperties().getUsername())
				.password(gatewayProperties.getBootstrapProperties().getPassword()).build();
		bootstrapPlatform = createPlatform(credentials);
	}

    @EventListener(CredentialsAvailableEvent.class)
	void onCredentialsAvailable(CredentialsAvailableEvent credentialsAvailableEvent) {
		if (!Objects.isNull(platform)) {
			return;
		}

		log.info("Device credentials available, setting up a connection to the platform.");

		DeviceCredentialsRepresentation deviceCredentials = credentialsAvailableEvent.getDeviceCredentials();
		CumulocityBasicCredentials credentials = CumulocityBasicCredentials.builder()
				.tenantId(deviceCredentials.getTenantId())
				.username(deviceCredentials.getUsername())
				.password(deviceCredentials.getPassword())
				.build();
		platform = createPlatform(credentials);

		configurePlatform((PlatformImpl) platform);

		eventPublisher.publishEvent(new PlatformConnectionReadyEvent(deviceCredentials.getUsername()));

        startPlatformAvailabilityMonitor();
	}

    public boolean isPlatformAvailable() {
        return isPlatformAvailable;
    }

    public void markPlatfromAsUnavailable() {
        isPlatformAvailable = false;
    }

    private void startPlatformAvailabilityMonitor() {
        taskScheduler.scheduleWithFixedDelay(() -> {
            if (!isPlatformAvailable) {
                if(checkPlatformAvailability()) {
                    isPlatformAvailable = true;
                }
                else {
                    log.info("Platform is unavailable. Waiting for {} seconds before retry.", gatewayProperties.getBootstrapFixedDelay()/1000);
                }
            }
        }, Duration.ofMillis(gatewayProperties.getBootstrapFixedDelay()));
    }

    private boolean checkPlatformAvailability() {
        try {
            return (this.platform != null && (this.platform.getMeasurementApi() != null));
        } catch(Throwable t) {
            log.debug("Platform is unavailable or the device credentials are incorrect.", t);
        }

        return false;
    }

    private Platform createPlatform(CumulocityBasicCredentials credentials) {
		return PlatformBuilder.platform().withBaseUrl(gatewayProperties.getBaseUrl())
				.withForceInitialHost(gatewayProperties.isForceInitialHost()).withCredentials(credentials).build();
	}

	private void configurePlatform(PlatformImpl platform) {
	    // A custom ResponseMapper is registered to handle the posting of the JSON string based resources
        // to the Platform as is without needing to parse and convert them into BaseResourceRepresentation
        ((PlatformParameters) this.platform).setResponseMapper(
                new ResponseMapper() {
                    @Override
                    public CharSequence write(Object o) {
                        // If the BaseResourceRepresentation object passed is one of the classes defined in
                        // one of this agent packages, then just call toJSON() method to serialize.
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

        platform.setHttpClientConfig(HttpClientConfig.httpConfig()
                .pool(ConnectionPoolConfig.connectionPool().enabled(true).max(snmpProperties.getTrapListenerPort()).perHost(snmpProperties.getTrapListenerPort()).build())
                .build());
	}
}