package com.cumulocity.agent.snmp.platform.service;

import com.cumulocity.sdk.client.RestOperations;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PlatformApiProvider {

	private final PlatformProvider platformProvider;

	@Bean
	public IdentityApi identityApi() {
		return platformProvider.getPlatform().getIdentityApi();
	}

	@Bean
	public InventoryApi inventoryApi() {
		return platformProvider.getPlatform().getInventoryApi();
	}

	@Bean
	public DeviceControlApi deviceControlApi() {
		return platformProvider.getPlatform().getDeviceControlApi();
	}

	@Bean
	public RestOperations restOperations() {
		return platformProvider.getPlatform().rest();
	}

	@Bean
	public AlarmApi alarmApi() {
		return platformProvider.getPlatform().getAlarmApi();
	}

	@Bean
	public EventApi eventApi() {
		return platformProvider.getPlatform().getEventApi();
	}

	@Bean
	public MeasurementApi measurementApi() {
		return platformProvider.getPlatform().getMeasurementApi();
	}
}