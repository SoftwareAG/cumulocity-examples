package com.cumulocity.agent.server.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;

@Component
public class InventoryRepository {

    private final InventoryApi inventoryApi;

    private final IdentityRepository identityRepository;

    @Autowired
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
