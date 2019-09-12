package com.cumulocity.agent.snmp.platform.config;

import com.cumulocity.agent.snmp.bootstrap.model.CredentialsAvailableEvent;
import com.cumulocity.agent.snmp.bootstrap.model.DeviceCredentials;
import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.platform.model.PlatformConnectionReadyEvent;
import com.cumulocity.common.utils.ObjectUtils;
import com.cumulocity.model.authentication.CumulocityBasicCredentials;
import com.cumulocity.sdk.client.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PlatformProvider implements InitializingBean {

	private Platform platform;

	private Platform bootstrapPlatform;

	private final GatewayProperties properties;

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

	public boolean isCredentialsAvailable() {
		return !ObjectUtils.isNull(platform);
	}

	@EventListener
	public void onCredentialsAvailable(CredentialsAvailableEvent credentialsAvailableEvent) {
		if (!Objects.isNull(platform)) {
			return;
		}

		log.info("Device credentials available, setting up platform connection");

		DeviceCredentials deviceCredentials = credentialsAvailableEvent.getDeviceCredentials();
		CumulocityBasicCredentials credentials = createCredentials(deviceCredentials);
		platform = createPlatform(credentials);
		configurePlatform((PlatformImpl) platform);

		eventPublisher.publishEvent(new PlatformConnectionReadyEvent(deviceCredentials.getUsername()));
	}

	@Override
	public void afterPropertiesSet() {
		CumulocityBasicCredentials credentials = createCredentials();
		bootstrapPlatform = createPlatform(credentials);
	}

	private CumulocityBasicCredentials createCredentials() {
		GatewayProperties.DeviceBootstrapProperties prop = properties.getBootstrapProperties();
		return createCredentials(prop.getTenantId(), prop.getUsername(), prop.getPassword());
	}

	private CumulocityBasicCredentials createCredentials(DeviceCredentials credentials) {
		return createCredentials(credentials.getTenantId(), credentials.getUsername(), credentials.getPassword());
	}

	private CumulocityBasicCredentials createCredentials(String tenantId, String username, String password) {
		return CumulocityBasicCredentials.builder().tenantId(tenantId).username(username).password(password).build();
	}

	private Platform createPlatform(CumulocityBasicCredentials credentials) {
		return PlatformBuilder.platform().withBaseUrl(properties.getBaseUrl())
				.withForceInitialHost(properties.isForceInitialHost()).withCredentials(credentials).build();
	}

	private void configurePlatform(PlatformImpl platform) {
		platform.setHttpClientConfig(HttpClientConfig.httpConfig()
				.pool(ConnectionPoolConfig.connectionPool().enabled(true).max(properties.getPlatformConnectionPoolMax())
						.perHost(properties.getPlatformConnectionPoolPerHost()).build())
				.build());
	}
}