package com.cumulocity.agent.server.repository;

import static com.cumulocity.tixi.server.model.ManagedObjects.asManagedObject;

import javax.inject.Inject;

import com.cumulocity.agent.server.annotation.Named;
import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.tixi.server.model.SerialNumber;

@Named
public class InventoryRepository {

    private final InventoryApi inventoryApi;

    private final IdentityApi identityApi;

    @Inject
    public InventoryRepository(InventoryApi inventoryApi, IdentityApi identityApi) {
        this.inventoryApi = inventoryApi;
        this.identityApi = identityApi;
    }

    public ManagedObjectRepresentation findByExternalId(ID externalID) {
        final ExternalIDRepresentation externalIDRepresentation = identityApi.getExternalId(externalID);
        return findById(externalIDRepresentation.getManagedObject().getId());
    }

    public ManagedObjectRepresentation findById(GId id) {
        return inventoryApi.get(id);
    }

    public ManagedObjectRepresentation save(ManagedObjectRepresentation managedObjectRepresentation) {
        if (managedObjectRepresentation.getId() == null) {
            return inventoryApi.create(managedObjectRepresentation);
        } else {
            return inventoryApi.update(managedObjectRepresentation);
        }
    }

    public ExternalIDRepresentation createExternalId(GId id, SerialNumber serialNumber) {
        ExternalIDRepresentation externalIDRepresentation = new ExternalIDRepresentation();
        externalIDRepresentation.setExternalId(serialNumber.getValue());
        externalIDRepresentation.setType(serialNumber.getType());
        externalIDRepresentation.setManagedObject(asManagedObject(id));
        return identityApi.create(externalIDRepresentation);
    }
}
