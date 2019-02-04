package com.cumulocity.snmp.repository;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.InventoryFilter;
import com.cumulocity.snmp.annotation.gateway.RunWithinContext;
import com.cumulocity.snmp.model.core.Credentials;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.repository.core.PlatformRepresentationRepository;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static com.cumulocity.snmp.utils.PlatformRepositoryUtils.handleException;
import static com.cumulocity.snmp.utils.PlatformRepositoryUtils.handleSuccess;

@Repository
public class ManagedObjectRepository implements PlatformRepresentationRepository<ManagedObjectRepresentation> {

    @Autowired
    InventoryApi inventory;

    @NonNull
    @RunWithinContext
    public Optional<ManagedObjectRepresentation> apply(@NonNull Credentials user, final ManagedObjectRepresentation managedObject) {
        return update(user, managedObject.getId(), managedObject);
    }

    @NonNull
    @RunWithinContext
    public Optional<ManagedObjectRepresentation> update(@NonNull Credentials user, GId id, ManagedObjectRepresentation managedObject) {
        try {
            managedObject.setId(id);
            return handleSuccess(inventory.update(managedObject));
        } catch (final Exception ex) {
            return handleException(ex);
        }
    }

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
    public void delete(@NonNull Credentials user, GId id) {
        try {
            inventory.delete(id);
            handleSuccess(null);
        } catch (final Exception ex) {
            handleException(ex);
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

    @NonNull
    public Optional<ManagedObjectRepresentation> get(@NonNull GId managedObjectId) {
        try {
            return handleSuccess(inventory.get(managedObjectId));
        } catch (final Exception ex) {
            return handleException(ex);
        }
    }

    @NonNull
    @RunWithinContext
    public Optional<Iterable<ManagedObjectRepresentation>> findAll(@NonNull Gateway gateway, InventoryFilter filter) {
        try {
            return handleSuccess(inventory.getManagedObjectsByFilter(filter).get().allPages());
        } catch (final Exception ex) {
            return handleException(ex);
        }
    }

    @VisibleForTesting
    public void setInventory(InventoryApi inventory) {
        this.inventory = inventory;
    }
}
