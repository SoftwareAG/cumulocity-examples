package com.cumulocity.snmp.factory.gateway;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.snmp.factory.platform.ManagedObjectMapper;
import com.cumulocity.snmp.model.core.Alarms;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;

@Component
public class GatewayFactory {

    @Autowired
    ManagedObjectMapper managedObjectMapper;

    public Optional<Gateway> create(Gateway credentials, ManagedObjectRepresentation managedObject) throws InvocationTargetException, IllegalAccessException {
        return create(managedObject, credentials.getTenant(), credentials.getName(), credentials.getPassword(), credentials.getAlarms());
    }

    public Optional<Gateway> create(DeviceCredentialsRepresentation credentials, ManagedObjectRepresentation managedObject) throws InvocationTargetException, IllegalAccessException {
        return create(managedObject, credentials.getTenantId(), credentials.getUsername(), credentials.getPassword(), new Alarms());
    }

    private Optional<Gateway> create(final ManagedObjectRepresentation managedObject, final String tenant, final String username, final String password, final Alarms alarms) throws InvocationTargetException, IllegalAccessException {
        return managedObjectMapper.convert(Gateway.class, managedObject).transform(new Function<Gateway, Gateway>() {
            public Gateway apply(Gateway gateway) {
                return gateway
                        .withTenant(tenant)
                        .withName(username)
                        .withPassword(password)
                        .withAlarms(alarms)
                        .withCurrentDeviceIds(from(getChildDevices(managedObject)).transform(managedObjectToId()).toList());
            }
        });
    }

    private Iterable<ManagedObjectReferenceRepresentation> getChildDevices(ManagedObjectRepresentation managedObject) {
        if (managedObject.getChildDevices() == null) {
            return newArrayList();
        }
        return managedObject.getChildDevices();
    }

    private Function<ManagedObjectReferenceRepresentation, GId> managedObjectToId() {
        return new Function<ManagedObjectReferenceRepresentation, GId>() {
            @Override
            public GId apply(final ManagedObjectReferenceRepresentation representation) {
                return representation.getManagedObject().getId();
            }
        };
    }

    @VisibleForTesting
    public void setManagedObjectMapper(ManagedObjectMapper managedObjectMapper) {
        this.managedObjectMapper = managedObjectMapper;
    }
}
