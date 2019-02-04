package com.cumulocity.snmp.factory.gateway;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.google.common.base.Optional;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Map;

import static com.cumulocity.snmp.model.gateway.device.Device.c8y_SNMPDevice;
import static com.cumulocity.snmp.utils.SimpleTypeUtils.parseGId;
import static com.google.common.base.Optional.absent;

@Component
public class DeviceFactory {
    @Nonnull
    public Optional<Device> create(ManagedObjectRepresentation managedObject) {
        if (managedObject.hasProperty(c8y_SNMPDevice)) {
            final Map property = (Map) managedObject.getProperty(c8y_SNMPDevice);
            final GId deviceId = managedObject.getId();
            return Optional.of(Device.builder()
                    .id(deviceId)
                    .ipAddress(property.get("ipAddress").toString())
                    .deviceType(parseGId(property.get("type")))
                    .build());
        }
        return absent();
    }
}
