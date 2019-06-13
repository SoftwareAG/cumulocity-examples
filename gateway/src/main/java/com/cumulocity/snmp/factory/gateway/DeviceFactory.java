package com.cumulocity.snmp.factory.gateway;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.snmp.configuration.service.SNMPConfigurationProperties;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.cumulocity.snmp.utils.IPAddressUtil;
import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.cumulocity.snmp.model.gateway.device.Device.c8y_SNMPDevice;
import static com.cumulocity.snmp.utils.SimpleTypeUtils.parseGId;
import static com.google.common.base.Optional.absent;
import static com.googlecode.ipv6.IPv6Address.fromString;

@Component
@Slf4j
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
            Map<String, Object> authMap = (HashMap) property.get("auth");
            return isAuthMapNullOrEmpty(authMap) ? getDeviceWithoutAuth(property, deviceId, port, version) :
                    getDeviceWithAuth(property, deviceId, port, version, authMap);
        }
        return absent();
    }

    private boolean isAuthMapNullOrEmpty(Map<String, Object> authMap) {
        return authMap == null || authMap.size() == 0;
    }

    private int getPort(Object port) {
        return (port != null) ? Integer.parseInt(port.toString()) : config.getPollingPort();
    }

    private int getSnmpVersion(Object version) {
        return (version != null) ? Integer.parseInt(version.toString()) : config.getVersion();
    }

    private int getIntValue(Object value) {
        return (value != null) ? Integer.parseInt(value.toString()) : 0;
    }

    private Optional<Device> getDeviceWithoutAuth(Map property, GId deviceId, int port, int version) {
        return Optional.of(Device.builder()
                .id(deviceId)
                .ipAddress(getFormattedIpAddress(property.get("ipAddress").toString()))
                .port(port)
                .snmpVersion(version)
                .deviceType(parseGId(property.get("type")))
                .build());
    }

    private Optional<Device> getDeviceWithAuth(Map property, GId deviceId, int port,
                                               int version, Map<String, Object> authMap) {
        return Optional.of(Device.builder()
                .id(deviceId)
                .ipAddress(getFormattedIpAddress(property.get("ipAddress").toString()))
                .port(port)
                .snmpVersion(version)
                .deviceType(parseGId(property.get("type")))
                .username((String) authMap.get("username"))
                .securityLevel(getIntValue(authMap.get("securityLevel")))
                .authProtocol(getIntValue(authMap.get("authProtocol")))
                .authProtocolPassword((String) authMap.get("authPassword"))
                .privacyProtocol(getIntValue(authMap.get("privProtocol")))
                .privacyProtocolPassword((String) authMap.get("privPassword"))
                .engineId((String) authMap.get("engineId"))
                .build());
    }

    private String getFormattedIpAddress(String ipAddress){
        try {
            if (IPAddressUtil.isValidIPv6(ipAddress.split("[/%]", 2)[0])) {
                return fromString(ipAddress.split("[/%]", 2)[0]).toString();
            }
        } catch (Exception e){
          log.error("Invalid IP-Address format!!");
          log.error(e.getMessage());
        }
        return ipAddress;
    }
}
