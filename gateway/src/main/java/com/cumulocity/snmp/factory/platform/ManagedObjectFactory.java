package com.cumulocity.snmp.factory.platform;

import c8y.*;
import com.cumulocity.model.Agent;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

import static com.cumulocity.snmp.model.gateway.Gateway.TYPE;
import static com.cumulocity.snmp.model.gateway.Gateway.c8y_SNMPGateway;

@Component
public class ManagedObjectFactory {

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
        result.set(createRequiredAvailabilityFragment());

        return result;
    }

    private RequiredAvailability createRequiredAvailabilityFragment() {
        return new RequiredAvailability(10);
    }

    private SupportedOperations createSupportedOperationsFragment() {
        SupportedOperations result = new SupportedOperations();
        result.add("c8y_SNMPConfiguration");
        return result;
    }
}
