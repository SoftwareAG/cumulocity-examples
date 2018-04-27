package com.cumulocity.route.repository;

import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ManagedObjectRepository {

    private final InventoryApi inventory;
    private final MicroserviceSubscriptionsService subscriptions;

    public ManagedObjectRepresentation findManagedObjectById(String tenant, GId id) {
        return subscriptions.callForTenant(tenant, () -> inventory.get(id));
    }

    public ManagedObjectRepresentation create(String type, String name) {
        final ManagedObjectRepresentation managedObject = new ManagedObjectRepresentation();
        managedObject.setType(type);
        managedObject.setName(name);
        return inventory.create(managedObject);
    }

    public void update(ManagedObjectRepresentation source) {
        inventory.update(source);
    }
}
