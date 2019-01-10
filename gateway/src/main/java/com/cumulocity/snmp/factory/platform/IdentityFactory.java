package com.cumulocity.snmp.factory.platform;

import com.cumulocity.model.ID;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
public class IdentityFactory {

    public static final String TYPE = "c8y_Serial";

    @Nonnull
    public ExternalIDRepresentation create(String identifier, ManagedObjectRepresentation managedObject) {
        final ExternalIDRepresentation id = create(identifier);
        id.setManagedObject(managedObject);
        return id;
    }

    @Nonnull
    public ExternalIDRepresentation create(String identifier) {
        final ExternalIDRepresentation id = new ExternalIDRepresentation();
        id.setType(TYPE);
        id.setExternalId(identifier);
        return id;
    }

    public ID createID(String identifier) {
        return new ID(TYPE, identifier);
    }
}
