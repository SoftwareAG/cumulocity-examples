package c8y.trackeragent.utils;

import static com.cumulocity.model.authentication.CumulocityCredentials.Builder.cumulocityCredentials;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import c8y.trackeragent.DeviceManagedObject;
import c8y.trackeragent.TrackerPlatform;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;

import com.cumulocity.model.Agent;
import com.cumulocity.model.ID;
import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.SDKException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class TrackerPlatformProvider {

    private final DeviceCredentialsRepository deviceCredentialsRepository;
    private final Cache<String, TrackerPlatform> cache;
    private final TrackerConfiguration config;
    private final Object lock = new Object();

    public TrackerPlatformProvider(TrackerConfiguration config, DeviceCredentialsRepository deviceCredentialsRepository) {
        this.config = config;
        this.deviceCredentialsRepository = deviceCredentialsRepository;
        this.cache = CacheBuilder.newBuilder().build();
    }

    public TrackerPlatform getDevicePlatform(final String imei) {
        try {
            return cache.get(imei, new Callable<TrackerPlatform>() {

                @Override
                public TrackerPlatform call() throws Exception {
                    return createDevicePlatform(imei);
                }

            });
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause != null && cause instanceof SDKException) {
                throw (SDKException) cause;
            } else {
                throw new SDKException("Can't access device platform for imei = " + imei, e);
            }
        }
    }

    public TrackerPlatform getBootstrapPlatform() {
        return createBootstrapPlatform();
    }

    private TrackerPlatform createDevicePlatform(String imei) {
        DeviceCredentials deviceCredentials = deviceCredentialsRepository.getCredentials(imei);
        String tenantId = deviceCredentials.getTenantId();
        CumulocityCredentials credentials = cumulocityCredentials(deviceCredentials.getUser(), deviceCredentials.getPassword()).withTenantId(tenantId).build();
        TrackerPlatform trackerPlatform = new TrackerPlatform(new PlatformImpl(config.getPlatformHost(), credentials));
        setupAgent(trackerPlatform);
        return trackerPlatform;
    }

    private TrackerPlatform createBootstrapPlatform() {
        CumulocityCredentials credentials = cumulocityCredentials(config.getBootstrapUser(), config.getBootstrapPassword()).withTenantId(config.getBootstrapTenant()).build();
        return new TrackerPlatform(new PlatformImpl(config.getPlatformHost(), credentials));
    }

    private void setupAgent(TrackerPlatform platform) {
        synchronized (lock) {
            DeviceManagedObject deviceManagedObject = new DeviceManagedObject(platform);
            ManagedObjectRepresentation agentMo = new ManagedObjectRepresentation();
            agentMo.setType("c8y_TrackerAgent");
            agentMo.setName("Tracker agent");
            agentMo.set(new Agent());
            ID extId = DeviceManagedObject.getAgentExternalId();
            deviceManagedObject.createOrUpdate(agentMo, extId, null);
            platform.setAgent(agentMo);
        }
    }

}
