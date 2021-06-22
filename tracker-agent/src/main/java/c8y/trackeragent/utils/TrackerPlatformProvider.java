/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.utils;

import c8y.trackeragent.TrackerPlatform;
import c8y.trackeragent.configuration.TrackerConfiguration;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import c8y.trackeragent.exception.SDKExceptions;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.model.authentication.CumulocityBasicCredentials;
import com.cumulocity.sdk.client.ClientConfiguration;
import com.cumulocity.sdk.client.PlatformImpl;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.Callable;

@Component
public class TrackerPlatformProvider {

	private static final Logger logger = LoggerFactory.getLogger(TrackerPlatformProvider.class);

    private final DeviceCredentialsRepository deviceCredentialsRepository;
    private final Cache<PlatformKey, TrackerPlatform> cache;
    private final TrackerConfiguration config;
    private final MicroserviceSubscriptionsService microserviceSubscriptionsService;

    @Autowired
    public TrackerPlatformProvider(TrackerConfiguration config,
                                   DeviceCredentialsRepository deviceCredentialsRepository,
                                   MicroserviceSubscriptionsService microserviceSubscriptionsService) {
        this.config = config;
        this.deviceCredentialsRepository = deviceCredentialsRepository;
        this.microserviceSubscriptionsService = microserviceSubscriptionsService;
        this.cache = CacheBuilder.newBuilder().build();
    }

    public void initTenantPlatform(final String tenantId) {
    	getTenantPlatform(tenantId);
    }

    public TrackerPlatform getTenantPlatform(final String tenantId) {
    	return getPlatform(PlatformKey.forTenant(tenantId));
    }

    public TrackerPlatform getBootstrapPlatform() {
        return getPlatform(PlatformKey.forBootstrap());
    }

    private TrackerPlatform getPlatform(final PlatformKey key) {
        try {
            return cache.get(key, new Callable<TrackerPlatform>() {

                @Override
                public TrackerPlatform call() throws Exception {
                    return createAndCachePlatform(key);
                }

            });
        } catch (Exception e) {
            throw SDKExceptions.narrow(e, "Can't access device platform for " + key);
        }
    }

    private TrackerPlatform createAndCachePlatform(PlatformKey key) {
        if (key.isBootstrap()) {
            return createBootstrapPlatform();
        } else {
            return createTenantPlatform(key.getTenant());
        }
    }

    private TrackerPlatform createBootstrapPlatform() {
        CumulocityBasicCredentials credentials = CumulocityBasicCredentials.builder()
                .tenantId(config.getBootstrapTenant())
                .username(config.getBootstrapUser())
                .password(config.getBootstrapPassword())
                .build();
        PlatformImpl paltform = c8yPlatform(credentials);
        return new TrackerPlatform(paltform);
    }

    private TrackerPlatform createTenantPlatform(String tenant) {
        Optional<MicroserviceCredentials> credentialsOptional = microserviceSubscriptionsService.getCredentials(tenant);
        if (credentialsOptional.isEmpty()) {
            throw new RuntimeException("Not found credentials for tenant: " + tenant); //check for better exception
        }
        CumulocityBasicCredentials credentials = CumulocityBasicCredentials.builder()
                .tenantId(tenant)
                .username(credentialsOptional.get().getUsername())
                .password(credentialsOptional.get().getPassword())
                .build();
        PlatformImpl platform = c8yPlatform(credentials);
        return new TrackerPlatform(platform);
    }

    private PlatformImpl c8yPlatform(CumulocityBasicCredentials credentials) {
        PlatformImpl platform = new PlatformImpl(config.getPlatformHost(), credentials, new ClientConfiguration(null, false));
        platform.setForceInitialHost(config.getForceInitialHost());
        return platform;
    }

    private static class PlatformKey {

        private final String tenant;

        public static PlatformKey forBootstrap() {
        	return new PlatformKey(null);
        }

        public static PlatformKey forTenant(String tenant) {
        	return new PlatformKey(tenant);
        }

        private PlatformKey(String tenant) {
            this.tenant = tenant;
        }

        String getTenant() {
            return tenant;
        }

        boolean isBootstrap() {
            return tenant == null;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((tenant == null) ? 0 : tenant.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            PlatformKey other = (PlatformKey) obj;
            if (tenant == null) {
                if (other.tenant != null)
                    return false;
            } else if (!tenant.equals(other.tenant))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return isBootstrap() ? "bootstrap" : "tenant: " + tenant;
        }
    }
}