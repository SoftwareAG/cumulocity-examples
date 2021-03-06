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
				.password(gatewayProperties.getBootstrapProperties().getPassword())
				.build();
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
		return PlatformBuilder.platform()
				.withBaseUrl(gatewayProperties.getBaseUrl())
				.withForceInitialHost(gatewayProperties.isForceInitialHost())
				.withCredentials(credentials)
				.build();
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

        int noOfConcurrentScheduledTasks = gatewayProperties.getThreadPoolSizeForScheduledTasks();
        ConnectionPoolConfig connectionPool = ConnectionPoolConfig.connectionPool()
        		.max(noOfConcurrentScheduledTasks)
        		.perHost(noOfConcurrentScheduledTasks)
        		.enabled(true)
        		.build();
        HttpClientConfig clientConfig = HttpClientConfig.httpConfig().pool(connectionPool).build();
        platform.setHttpClientConfig(clientConfig);
	}
}