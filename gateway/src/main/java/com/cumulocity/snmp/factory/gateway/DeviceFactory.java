package com.cumulocity.snmp.factory.gateway;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.snmp.configuration.service.SNMPConfigurationProperties;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.cumulocity.snmp.model.gateway.device.Device.c8y_SNMPDevice;
import static com.cumulocity.snmp.utils.SimpleTypeUtils.parseGId;
import static com.google.common.base.Optional.absent;

@Component
public class DeviceFactory implements Converter<ManagedObjectRepresentation, Optional<Device>> {

    @Autowired
    private SNMPConfigurationProperties config;

    @Override
    public Optional<Device> convert(ManagedObjectRepresentation managedObject) {
        if (managedObject.hasProperty(c8y_SNMPDevice)) {
            final Map property = (Map) managedObject.getProperty(c8y_SNMPDevice);
            final GId deviceId = managedObject.getId();
            int port = getPort(property.get("port"));
            int version = getSnmpVersion(property.get("version"));
            return Optional.of(Device.builder()
                    .id(deviceId)
                    .ipAddress(property.get("ipAddress").toString())
                    .port(port)
                    .snmpVersion(version)
                    .deviceType(parseGId(property.get("type")))
                    .build());
        }
        return absent();
    }

    private int getPort(Object port) {
        return (port != null) ? Integer.parseInt((String) port) : config.getPollingPort();
    }

    private int getSnmpVersion(Object version) {
        return (version != null) ? Integer.parseInt((String) version) : config.getVersion();
    }
}
