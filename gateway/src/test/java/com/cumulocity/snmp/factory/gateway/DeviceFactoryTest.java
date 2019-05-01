package com.cumulocity.snmp.factory.gateway;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.google.common.base.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;

import static com.cumulocity.model.idtype.GId.asGId;
import static com.cumulocity.snmp.Conditions.present;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DeviceFactoryTest {

    @Spy
    DeviceFactory deviceFactory;

    @Test
    public void shouldCreateDeviceWithoutAuthDetails() {

        //Given
        HashMap<Object, Object> deviceFragment = new HashMap<>();
        deviceFragment.put("ipAddress", "192.168.0.1");
        deviceFragment.put("port", "161");
        deviceFragment.put("version", "0");
        deviceFragment.put("type", "/inventory/managedObjects/15257");
        ManagedObjectRepresentation managedObject = new ManagedObjectRepresentation();
        managedObject.setId(asGId("15256"));
        managedObject.setProperty(Device.c8y_SNMPDevice, deviceFragment);
        Device device = new Device();
        device.setId(asGId("15256"));
        device.setIpAddress("192.168.0.1");
        device.setDeviceType(asGId("15257"));
        device.setPort(161);
        device.setSnmpVersion(0);

        //When
        Optional<Device> deviceOptional = deviceFactory.convert(managedObject);

        //Then
        assertThat(deviceOptional).is(present());
        assertThat(deviceOptional.get()).isEqualTo(device);
    }

    @Test
    public void shouldCreateDeviceWithAuthDetails() {

        //Given
        HashMap<Object, Object> authFragment = new HashMap<>();
        authFragment.put("username", "testsnmp");
        authFragment.put("securityLevel", 3);
        authFragment.put("authProtocol", 2);
        authFragment.put("authPassword", "testsnmp");
        authFragment.put("privProtocol", 1);
        authFragment.put("privPassword", "testsnmp");
        authFragment.put("engineId", "12345");

        HashMap<Object, Object> deviceFragment = new HashMap<>();
        deviceFragment.put("ipAddress", "192.168.0.1");
        deviceFragment.put("port", "161");
        deviceFragment.put("version", "0");
        deviceFragment.put("type", "/inventory/managedObjects/15257");
        deviceFragment.put("auth", authFragment);

        ManagedObjectRepresentation managedObject = new ManagedObjectRepresentation();
        managedObject.setId(asGId("15256"));
        managedObject.setProperty(Device.c8y_SNMPDevice, deviceFragment);

        Device device = new Device();
        device.setId(asGId("15256"));
        device.setIpAddress("192.168.0.1");
        device.setDeviceType(asGId("15257"));
        device.setPort(161);
        device.setSnmpVersion(0);
        device.setUsername("testsnmp");
        device.setSecurityLevel(3);
        device.setAuthProtocol(2);
        device.setAuthProtocolPassword("testsnmp");
        device.setPrivacyProtocol(1);
        device.setPrivacyProtocolPassword("testsnmp");
        device.setEngineId("12345");

        //When
        Optional<Device> deviceOptional = deviceFactory.convert(managedObject);

        //Then
        assertThat(deviceOptional).is(present());
        assertThat(deviceOptional.get()).isEqualTo(device);
    }

    @Test
    public void shouldNotCreateDevice() {

        //Given
        ManagedObjectRepresentation representation = new ManagedObjectRepresentation();

        //When
        Optional<Device> deviceOptional = deviceFactory.convert(representation);

        //Then
        assertThat(deviceOptional).isNot(present());
    }
}