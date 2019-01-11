package com.cumulocity.snmp.repository;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.snmp.annotation.gateway.RunWithinContext;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static com.cumulocity.snmp.utils.PlatformRepositoryUtils.handleException;
import static com.cumulocity.snmp.utils.PlatformRepositoryUtils.handleSuccess;

@Repository
public class ManagedObjectRepository{

    @Autowired
    InventoryApi inventory;

    @NonNull
    @RunWithinContext
    public Optional<ManagedObjectRepresentation> save(@NonNull DeviceCredentialsRepresentation user, @NonNull ManagedObjectRepresentation managedObject) {
        try {
            return handleSuccess(inventory.create(managedObject));
        } catch (final Exception ex) {
            return handleException(ex);
        }
    }

    @NonNull
    @RunWithinContext
    public Optional<ManagedObjectRepresentation> get(@NonNull Gateway gateway) {
        return get(gateway, gateway.getId());
    }

    @NonNull
    @RunWithinContext
    public Optional<ManagedObjectRepresentation> get(@NonNull DeviceCredentialsRepresentation user, @NonNull GId managedObjectId) {
        try {
            return handleSuccess(inventory.get(managedObjectId));
        } catch (final Exception ex) {
            return handleException(ex);
        }
    }

    @NonNull
    @RunWithinContext
    public Optional<ManagedObjectRepresentation> get(@NonNull Gateway gateway, @NonNull GId managedObjectId) {
        try {
            return handleSuccess(inventory.get(managedObjectId));
        } catch (final Exception ex) {
            return handleException(ex);
        }
    }

    @VisibleForTesting
    public void setInventory(InventoryApi inventory){
        this.inventory = inventory;
    }
}
