/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, 
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

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
	public DeviceControlApi deviceControlApi() {
		return platformProvider.getPlatform().getDeviceControlApi();
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
	public EventApi eventApi() {
		return platformProvider.getPlatform().getEventApi();
	}

	@Bean
	@Scope(scopeName = SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public MeasurementApi measurementApi() {
		return platformProvider.getPlatform().getMeasurementApi();
	}
}