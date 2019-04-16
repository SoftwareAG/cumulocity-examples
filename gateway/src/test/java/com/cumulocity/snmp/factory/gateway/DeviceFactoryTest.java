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
    public void shouldCreateDevice() {

        //Given
        HashMap<Object, Object> deviceFragment = new HashMap<>();
        deviceFragment.put("ipAddress", "192.168.0.1");
        deviceFragment.put("port", "161");
        deviceFragment.put("version", "1");
        deviceFragment.put("type", "/inventory/managedObjects/15257");
        ManagedObjectRepresentation managedObject = new ManagedObjectRepresentation();
        managedObject.setId(asGId("15256"));
        managedObject.setProperty(Device.c8y_SNMPDevice, deviceFragment);

        //When
        Optional<Device> deviceOptional = deviceFactory.convert(managedObject);

        //Then
        assertThat(deviceOptional).is(present());
        assertThat(deviceOptional.get()).isEqualTo(new Device(asGId("15256"), "192.168.0.1", asGId("15257"),
                161, 1));
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