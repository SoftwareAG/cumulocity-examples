package com.cumulocity.snmp.factory.gateway;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.snmp.model.gateway.type.core.Register;
import com.cumulocity.snmp.model.type.DeviceType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.cumulocity.snmp.model.type.DeviceType.FIELDBUS_TYPE;
import static com.google.common.base.Optional.fromNullable;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DeviceTypeFactory {
    private final ObjectMapper objectMapper;

    @NonNull
    @SneakyThrows
    public Optional<DeviceType> create(ManagedObjectRepresentation managedObject) {
        if (DeviceType.SNMP_TYPE.equals(managedObject.getAttrs().get(FIELDBUS_TYPE))) {
            final DeviceType value = objectMapper.convertValue(managedObject.getAttrs(), DeviceType.class);
            value.setId(managedObject.getId());
            value.setName(managedObject.getName());
            value.setType(managedObject.getType());
            if (value.getRegisters() == null) {
                value.setRegisters(Lists.<Register>newArrayList());
            }
            return fromNullable(value);
        }
        return Optional.absent();
    }
}
