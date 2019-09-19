package com.cumulocity.agent.snmp.platform.config;

import com.cumulocity.agent.snmp.bootstrap.model.CredentialsAvailableEvent;
import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.platform.model.PlatformConnectionReadyEvent;
import com.cumulocity.common.utils.ObjectUtils;
import com.cumulocity.model.authentication.CumulocityBasicCredentials;
import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.sdk.client.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PlatformProvider implements InitializingBean {

	private final GatewayProperties gatewayProperties;

	private Platform bootstrapPlatform;

	private Platform platform;

	private final ApplicationEventPublisher eventPublisher;

	public Platform getPlatform() {
		if (!isCredentialsAvailable()) {
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

	public boolean isCredentialsAvailable() {
		return !ObjectUtils.isNull(platform);
	}

	@EventListener
	public void onCredentialsAvailable(CredentialsAvailableEvent credentialsAvailableEvent) {
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
	}

	private Platform createPlatform(CumulocityBasicCredentials credentials) {
		return PlatformBuilder.platform().withBaseUrl(gatewayProperties.getBaseUrl())
				.withForceInitialHost(gatewayProperties.isForceInitialHost()).withCredentials(credentials).build();
	}

	private void configurePlatform(PlatformImpl platform) {
		platform.setHttpClientConfig(HttpClientConfig.httpConfig()
				.pool(ConnectionPoolConfig.connectionPool().enabled(true).max(gatewayProperties.getPlatformConnectionPoolMax())
						.perHost(gatewayProperties.getPlatformConnectionPoolPerHost()).build())
				.build());
	}
}