package com.cumulocity.snmp.factory.platform;

import c8y.Hardware;
import c8y.IsDevice;
import c8y.Mobile;
import c8y.SupportedOperations;
import com.cumulocity.model.Agent;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.snmp.factory.gateway.core.PlatformRepresentationFactory;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.cumulocity.snmp.model.gateway.type.mapping.StatusMapping;
import com.cumulocity.snmp.model.notification.platform.PlatformRepresentationEvent;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.cumulocity.snmp.model.gateway.Gateway.TYPE;
import static com.cumulocity.snmp.model.gateway.Gateway.c8y_SNMPGateway;
import static com.cumulocity.snmp.model.gateway.type.core.Register.c8y_RegisterStatus;
import static com.google.common.base.Optional.of;

@Component
public class ManagedObjectFactory implements PlatformRepresentationFactory<StatusMapping, ManagedObjectRepresentation> {

    private LoadingCache<Device, ManagedObjectRepresentation> cache = CacheBuilder
            .newBuilder()
            .expireAfterAccess(1, TimeUnit.DAYS)
            .build(new CacheLoader<Device, ManagedObjectRepresentation>() {
                public ManagedObjectRepresentation load(Device device) throws Exception {
                    final ManagedObjectRepresentation result = new ManagedObjectRepresentation();
                    result.setId(device.getId());
                    result.setProperty(c8y_RegisterStatus, new ConcurrentHashMap<>());
                    return result;
                }
            });

    @Override
    public Optional<ManagedObjectRepresentation> apply(PlatformRepresentationEvent platformRepresentationEvent) {
        final ManagedObjectRepresentation result = cache.getUnchecked(platformRepresentationEvent.getDevice());
        return of(result);
    }

    @Nonnull
    public ManagedObjectRepresentation create(String name) {
        final ManagedObjectRepresentation result = new ManagedObjectRepresentation();
        result.setType(TYPE);
        result.setName(name);

        result.set(new Agent());
        result.set(new IsDevice());
        result.set(new Hardware());
        result.set(new Mobile());
        result.set(new Object(), c8y_SNMPGateway);

        result.set(createSupportedOperationsFragment());

        return result;
    }

    private SupportedOperations createSupportedOperationsFragment() {
        SupportedOperations result = new SupportedOperations();
        result.add("c8y_SNMPConfiguration");
        return result;
    }

    @Nonnull
    public ManagedObjectRepresentation createChildDevice(String name, String ipAddress) {
        final ManagedObjectRepresentation result = new ManagedObjectRepresentation();
        Map<String, String> deviceIpMap = new HashMap();
        deviceIpMap.put("ipAddress", ipAddress);
        deviceIpMap.put("port", "161");
        result.setName(name);
        result.setProperty(Device.c8y_SNMPDevice, deviceIpMap);
//        result.set(new Object(), Device.c8y_SNMPDevice);


        return result;
    }
}
