package com.cumulocity.greenbox.server.service;

import static com.cumulocity.greenbox.server.model.HubUid.asHubUid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.greenbox.server.model.GreenBoxSetupRequest;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.SDKException;

@Component
public class DeviceService {

    @Autowired
    private InventoryRepository inventoryRepository;

    public void setup(GreenBoxSetupRequest message) {
        try {
            final ManagedObjectRepresentation agent = inventoryRepository.findByExternalId(asHubUid(message.getHubUID()));
        } catch (SDKException ex) {
            initialize(message);
        }
    }

    private void initialize(GreenBoxSetupRequest message) {
        ManagedObjectRepresentation agent;
    }
}
