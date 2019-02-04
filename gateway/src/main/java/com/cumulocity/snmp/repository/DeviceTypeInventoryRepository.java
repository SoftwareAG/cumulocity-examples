package com.cumulocity.snmp.repository;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.snmp.annotation.gateway.RunWithinContext;
import com.cumulocity.snmp.factory.gateway.DeviceTypeFactory;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.type.DeviceType;
import com.google.common.base.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DeviceTypeInventoryRepository {
    private final ManagedObjectRepository managedObjectRepository;
    private final DeviceTypeFactory deviceTypeFactory;

    @RunWithinContext
    public Optional<DeviceType> get(@NonNull Gateway gateway, @NonNull GId managedObjectId) {
        final Optional<ManagedObjectRepresentation> managedObject = managedObjectRepository.get(managedObjectId);
        if (managedObject.isPresent()) {
            return deviceTypeFactory.create(managedObject.get());
        }
        return Optional.absent();
    }
}
