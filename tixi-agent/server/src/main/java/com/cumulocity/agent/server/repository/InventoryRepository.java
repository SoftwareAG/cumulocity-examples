package com.cumulocity.agent.server.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.tixi.server.model.SerialNumber;

@Component
public class InventoryRepository {

	private static final Logger logger = LoggerFactory.getLogger(InventoryRepository.class);
	
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
    	logger.debug("Save managed object: {}.", managedObjectRepresentation);
        if (managedObjectRepresentation.getId() == null) {
            return inventoryApi.create(managedObjectRepresentation);
        } else {
            return inventoryApi.update(managedObjectRepresentation);
        }
    }
    
	public ManagedObjectRepresentation save(ManagedObjectRepresentation managedObjectRepresentation, SerialNumber deviceSerial) {
		managedObjectRepresentation = save(managedObjectRepresentation);
		identityRepository.save(managedObjectRepresentation.getId(), deviceSerial);
	    return managedObjectRepresentation;
    }

    public void bindToAgent(GId agentId, GId deviceId) {
    	logger.debug("Bind device: {} to agentId {}.", deviceId, agentId);
    	inventoryApi.getManagedObjectApi(agentId).addChildDevice(deviceId);
    }

}
