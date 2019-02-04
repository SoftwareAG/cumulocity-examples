package com.cumulocity.snmp.factory.platform;

import c8y.*;
import com.cumulocity.model.Agent;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.snmp.factory.gateway.core.PlatformRepresentationFactory;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.cumulocity.snmp.model.gateway.type.core.Register;
import com.cumulocity.snmp.model.gateway.type.mapping.StatusMapping;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

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
    public Optional<ManagedObjectRepresentation> apply(DateTime time, Gateway gateway, Device device, Register register, StatusMapping var1, Object value) {
        final ManagedObjectRepresentation result = cache.getUnchecked(device);
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
}
