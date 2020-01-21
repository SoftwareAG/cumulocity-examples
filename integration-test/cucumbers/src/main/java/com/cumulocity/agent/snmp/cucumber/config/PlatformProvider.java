package com.cumulocity.agent.snmp.cucumber.config;

import com.cumulocity.agent.snmp.cucumber.config.GatewayIntegrationTestProperties.TenantProperties;
import com.cumulocity.model.authentication.CumulocityBasicCredentials;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformBuilder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class PlatformProvider {

	@Autowired
	private TenantProperties mgmtTenantProperties;

	@Autowired
	private GatewayIntegrationTestProperties gatewayProperties;

	@Getter
	private Platform mgmtPlatform;

	@Getter
	private Platform testPlatform;

	@PostConstruct
	public Platform switchToManagement() {
		CumulocityBasicCredentials credentials = CumulocityBasicCredentials.builder()
				.tenantId(mgmtTenantProperties.getTenantId()).username(mgmtTenantProperties.getUsername())
				.password(mgmtTenantProperties.getPassword()).build();
		mgmtPlatform = createPlatform(credentials);
		return mgmtPlatform;
	}

	public Platform createIntegrationTestPlatform(CumulocityBasicCredentials testTenantCredentials) {
		testPlatform = createPlatform(testTenantCredentials);
		return testPlatform;
	}

	public Platform createPlatform(CumulocityBasicCredentials credentials) {
		return PlatformBuilder.platform().withBaseUrl(gatewayProperties.getBaseUrl())
				.withForceInitialHost(gatewayProperties.isForceInitialHost()).withCredentials(credentials).build();
	}
}
