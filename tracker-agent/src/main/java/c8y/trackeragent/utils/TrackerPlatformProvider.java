package c8y.trackeragent.utils;

import static com.cumulocity.model.authentication.CumulocityCredentials.Builder.cumulocityCredentials;

import java.util.concurrent.Callable;

import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.ClientConfiguration;
import com.cumulocity.sdk.client.PlatformImpl;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import c8y.trackeragent.DeviceManagedObject;
import c8y.trackeragent.TrackerPlatform;
import c8y.trackeragent.configuration.TrackerConfiguration;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import c8y.trackeragent.exception.SDKExceptions;

public class TrackerPlatformProvider {

    private final DeviceCredentialsRepository deviceCredentialsRepository;
    private final Cache<PlatformKey, TrackerPlatform> cache;
    private final TrackerConfiguration config;
    private final Object lock = new Object();
    private final DeviceContextService contextService;
    private final InventoryRepository inventoryRepository;

    public TrackerPlatformProvider(TrackerConfiguration config, DeviceCredentialsRepository deviceCredentialsRepository,
            DeviceContextService contextService, InventoryRepository inventoryRepository) {
        this.config = config;
        this.deviceCredentialsRepository = deviceCredentialsRepository;
        this.cache = CacheBuilder.newBuilder().build();
        this.contextService = contextService;
        this.inventoryRepository = inventoryRepository;
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
                    return createPlatform(key);
                }

            });
        } catch (Exception e) {
            throw SDKExceptions.narrow(e, "Can't access device platform for " + key);
        }
    }

    private TrackerPlatform createPlatform(PlatformKey key) {
        if (key.isBootstrap()) {
            return createBootstrapPlatform();
        } else {
            return createTenantPlatform(key.getTenant());
        }
    }

    private TrackerPlatform createBootstrapPlatform() {
        CumulocityCredentials credentials = cumulocityCredentials(config.getBootstrapUser(), config.getBootstrapPassword()).withTenantId(config.getBootstrapTenant()).build();
        PlatformImpl paltform = c8yPlatform(credentials);
        return new TrackerPlatform(paltform);
    }
    
    private TrackerPlatform createTenantPlatform(String tenant) {
    	DeviceCredentials agentCredentials = deviceCredentialsRepository.getAgentCredentials(tenant);
    	CumulocityCredentials credentials = cumulocityCredentials(agentCredentials.getUsername(), agentCredentials.getPassword()).withTenantId(tenant).build();
    	PlatformImpl platform = c8yPlatform(credentials);
    	TrackerPlatform trackerPlatform = new TrackerPlatform(platform);
    	setupAgent(trackerPlatform, tenant);
    	return trackerPlatform;
    }

    private PlatformImpl c8yPlatform(CumulocityCredentials credentials) {
        PlatformImpl platform = new PlatformImpl(config.getPlatformHost(), credentials, new ClientConfiguration(null, false));
        platform.setForceInitialHost(config.getForceInitialHost());
        return platform;
    }

    private void setupAgent(TrackerPlatform platform, String tenant) {
        synchronized (lock) {
        	DeviceCredentials agentCredentials = deviceCredentialsRepository.getAgentCredentials(tenant);
            DeviceManagedObject deviceManagedObject = new DeviceManagedObject(platform, contextService, inventoryRepository, agentCredentials);
            ManagedObjectRepresentation agentMo = deviceManagedObject.assureTrackerAgentExisting();
            platform.setAgent(agentMo);
        }
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