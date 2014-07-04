package com.cumulocity.agent.server.repository;

import javax.inject.Inject;
import javax.inject.Named;

import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;

@Named
public class InventoryRepository {

    private final InventoryApi inventoryApi;

    private final IdentityRepository identityRepository;

    @Inject
    public InventoryRepository(InventoryApi inventoryApi, IdentityRepository identityRepository) {
        this.inventoryApi = inventoryApi;
        this.identityRepository = identityRepository;
    }

    public ManagedObjectRepresentation findByExternalId(ID externalID) {
        return findById(identityRepository.find(externalID));
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

}
