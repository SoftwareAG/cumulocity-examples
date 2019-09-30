package com.cumulocity.agent.snmp.platform.service;

import com.cumulocity.sdk.client.RestOperations;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.sdk.client.inventory.BinariesApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PlatformApiProvider {

	private final PlatformProvider platformProvider;

	@Bean
	@Scope(scopeName = SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public IdentityApi identityApi() {
		return platformProvider.getPlatform().getIdentityApi();
	}

	@Bean
	@Scope(scopeName = SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public InventoryApi inventoryApi() {
		return platformProvider.getPlatform().getInventoryApi();
	}

	@Bean
	@Scope(scopeName = SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public RestOperations restOperations() {
		return platformProvider.getPlatform().rest();
	}

	@Bean
	@Scope(scopeName = SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public AlarmApi alarmApi() {
		return platformProvider.getPlatform().getAlarmApi();
	}

	@Bean
	@Scope(scopeName = SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public BinariesApi binariesApi() {
		return platformProvider.getPlatform().getBinariesApi();
	}

	@Bean
	@Scope(scopeName = SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public EventApi eventApi() {
		return platformProvider.getPlatform().getEventApi();
	}

	@Bean
	@Scope(scopeName = SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public DeviceControlApi deviceControlApi() {
		return platformProvider.getPlatform().getDeviceControlApi();
	}

	@Bean
	@Scope(scopeName = SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
	@Primary
	public MeasurementApi measurementApi() {
		return platformProvider.getPlatform().getMeasurementApi();
	}

	@Bean
	@Scope(scopeName = SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public RestOperations rest() {
		return platformProvider.getPlatform().rest();
	}
}