package com.cumulocity.agent.snmp.platform.model;

import com.cumulocity.model.JSONBase;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DeviceManagedObjectWrapperTest {

    @Test
    public void shouldCreateDeviceManagedObjectWrapperSuccessfully() {
        ManagedObjectRepresentation deviceMo = JSONBase.fromJSON("{\"id\":\"1000\",\"name\":\"child-device-1\",\"type\":\"some-device-type\",\"c8y_SNMPDevice\":{\"port\":\"161\",\"auth\":{\"securityLevel\":3,\"privPassword\":\"priv-password\",\"privProtocol\":2,\"authPassword\":\"auth-password\",\"authProtocol\":1,\"username\":\"user-1\",\"engineId\":\"engine-id-1\"},\"ipAddress\":\"198.162.0.1\",\"type\":\"/inventory/managedObjects/900\",\"version\":3}}", ManagedObjectRepresentation.class);

        DeviceManagedObjectWrapper deviceWrapper = new DeviceManagedObjectWrapper(deviceMo);

        assertEquals("1000", deviceWrapper.getId().getValue());
        assertEquals("child-device-1", deviceWrapper.getName());
        assertEquals("900", deviceWrapper.getDeviceProtocol());

        DeviceManagedObjectWrapper.SnmpDeviceProperties deviceProperties = deviceWrapper.getProperties();
        assertNotNull(deviceProperties);
        assertEquals(3, deviceProperties.getVersion());
        assertEquals("161", deviceProperties.getPort());
        assertEquals("/inventory/managedObjects/900", deviceProperties.getType());
        assertEquals("198.162.0.1", deviceProperties.getIpAddress());


        DeviceManagedObjectWrapper.DeviceAuthentication devicePropertiesAuth = deviceProperties.getAuth();
        assertNotNull(devicePropertiesAuth);
        assertEquals("engine-id-1", devicePropertiesAuth.getEngineId());
        assertEquals("user-1", devicePropertiesAuth.getUsername());
        assertEquals("priv-password", devicePropertiesAuth.getPrivPassword());
        assertEquals("auth-password", devicePropertiesAuth.getAuthPassword());
        assertEquals(1, devicePropertiesAuth.getAuthProtocol());
        assertEquals(2, devicePropertiesAuth.getPrivProtocol());
        assertEquals(3, devicePropertiesAuth.getSecurityLevel());
    }

    @Test
    public void shouldCreateDeviceManagedObjectWrapperWithEmptyAuthProperties() {
        ManagedObjectRepresentation deviceMo = JSONBase.fromJSON("{\"id\":\"1000\",\"name\":\"child-device-1\",\"type\":\"some-device-type\",\"c8y_SNMPDevice\":{\"port\":\"161\",\"auth\":{},\"ipAddress\":\"198.162.0.1\",\"type\":\"/inventory/managedObjects/900\",\"version\":3}}", ManagedObjectRepresentation.class);

        DeviceManagedObjectWrapper deviceWrapper = new DeviceManagedObjectWrapper(deviceMo);

        assertEquals("1000", deviceWrapper.getId().getValue());
        assertEquals("child-device-1", deviceWrapper.getName());
        assertEquals("900", deviceWrapper.getDeviceProtocol());

        DeviceManagedObjectWrapper.SnmpDeviceProperties deviceProperties = deviceWrapper.getProperties();
        assertNotNull(deviceProperties);
        assertEquals(3, deviceProperties.getVersion());
        assertEquals("161", deviceProperties.getPort());
        assertEquals("/inventory/managedObjects/900", deviceProperties.getType());
        assertEquals("198.162.0.1", deviceProperties.getIpAddress());


        DeviceManagedObjectWrapper.DeviceAuthentication devicePropertiesAuth = deviceProperties.getAuth();
        assertNotNull(devicePropertiesAuth);
        assertEquals(null, devicePropertiesAuth.getEngineId());
        assertEquals(null, devicePropertiesAuth.getUsername());
        assertEquals(null, devicePropertiesAuth.getPrivPassword());
        assertEquals(null, devicePropertiesAuth.getAuthPassword());
        assertEquals(0, devicePropertiesAuth.getAuthProtocol());
        assertEquals(0, devicePropertiesAuth.getPrivProtocol());
        assertEquals(0, devicePropertiesAuth.getSecurityLevel());
    }

    @Test
    public void shouldCreateDeviceManagedObjectWrapperWithNoAuthProperties() {
        ManagedObjectRepresentation deviceMo = JSONBase.fromJSON("{\"id\":\"1000\",\"name\":\"child-device-1\",\"type\":\"some-device-type\",\"c8y_SNMPDevice\":{\"port\":\"161\",\"ipAddress\":\"198.162.0.1\",\"type\":\"/inventory/managedObjects/900\",\"version\":3}}", ManagedObjectRepresentation.class);

        DeviceManagedObjectWrapper deviceWrapper = new DeviceManagedObjectWrapper(deviceMo);

        assertEquals("1000", deviceWrapper.getId().getValue());
        assertEquals("child-device-1", deviceWrapper.getName());
        assertEquals("900", deviceWrapper.getDeviceProtocol());

        DeviceManagedObjectWrapper.SnmpDeviceProperties deviceProperties = deviceWrapper.getProperties();
        assertNotNull(deviceProperties);
        assertEquals(3, deviceProperties.getVersion());
        assertEquals("161", deviceProperties.getPort());
        assertEquals("/inventory/managedObjects/900", deviceProperties.getType());
        assertEquals("198.162.0.1", deviceProperties.getIpAddress());


        DeviceManagedObjectWrapper.DeviceAuthentication devicePropertiesAuth = deviceProperties.getAuth();
        assertNotNull(devicePropertiesAuth);
        assertEquals(null, devicePropertiesAuth.getEngineId());
        assertEquals(null, devicePropertiesAuth.getUsername());
        assertEquals(null, devicePropertiesAuth.getPrivPassword());
        assertEquals(null, devicePropertiesAuth.getAuthPassword());
        assertEquals(0, devicePropertiesAuth.getAuthProtocol());
        assertEquals(0, devicePropertiesAuth.getPrivProtocol());
        assertEquals(0, devicePropertiesAuth.getSecurityLevel());
    }

    @Test
    public void shouldCreateDeviceManagedObjectWrapperWithEmptyProperties() {
        ManagedObjectRepresentation deviceMo = JSONBase.fromJSON("{\"id\":\"1000\",\"name\":\"child-device-1\",\"type\":\"some-device-type\",\"c8y_SNMPDevice\":{}}", ManagedObjectRepresentation.class);

        DeviceManagedObjectWrapper deviceWrapper = new DeviceManagedObjectWrapper(deviceMo);

        assertEquals("1000", deviceWrapper.getId().getValue());
        assertEquals("child-device-1", deviceWrapper.getName());
        assertEquals(null, deviceWrapper.getDeviceProtocol());

        DeviceManagedObjectWrapper.SnmpDeviceProperties deviceProperties = deviceWrapper.getProperties();
        assertNotNull(deviceProperties);
        assertEquals(0, deviceProperties.getVersion());
        assertEquals(null, deviceProperties.getPort());
        assertEquals(null, deviceProperties.getType());
        assertEquals(null, deviceProperties.getIpAddress());


        DeviceManagedObjectWrapper.DeviceAuthentication devicePropertiesAuth = deviceProperties.getAuth();
        assertNotNull(devicePropertiesAuth);
        assertEquals(null, devicePropertiesAuth.getEngineId());
        assertEquals(null, devicePropertiesAuth.getUsername());
        assertEquals(null, devicePropertiesAuth.getPrivPassword());
        assertEquals(null, devicePropertiesAuth.getAuthPassword());
        assertEquals(0, devicePropertiesAuth.getAuthProtocol());
        assertEquals(0, devicePropertiesAuth.getPrivProtocol());
        assertEquals(0, devicePropertiesAuth.getSecurityLevel());
    }

    @Test
    public void shouldCreateDeviceManagedObjectWrapperWithNoProperties() {
        ManagedObjectRepresentation deviceMo = JSONBase.fromJSON("{\"id\":\"1000\",\"name\":\"child-device-1\",\"type\":\"some-device-type\"}", ManagedObjectRepresentation.class);

        DeviceManagedObjectWrapper deviceWrapper = new DeviceManagedObjectWrapper(deviceMo);

        assertEquals("1000", deviceWrapper.getId().getValue());
        assertEquals("child-device-1", deviceWrapper.getName());
        assertEquals(null, deviceWrapper.getDeviceProtocol());

        DeviceManagedObjectWrapper.SnmpDeviceProperties deviceProperties = deviceWrapper.getProperties();
        assertNotNull(deviceProperties);
        assertEquals(0, deviceProperties.getVersion());
        assertEquals(null, deviceProperties.getPort());
        assertEquals(null, deviceProperties.getType());
        assertEquals(null, deviceProperties.getIpAddress());


        DeviceManagedObjectWrapper.DeviceAuthentication devicePropertiesAuth = deviceProperties.getAuth();
        assertNotNull(devicePropertiesAuth);
        assertEquals(null, devicePropertiesAuth.getEngineId());
        assertEquals(null, devicePropertiesAuth.getUsername());
        assertEquals(null, devicePropertiesAuth.getPrivPassword());
        assertEquals(null, devicePropertiesAuth.getAuthPassword());
        assertEquals(0, devicePropertiesAuth.getAuthProtocol());
        assertEquals(0, devicePropertiesAuth.getPrivProtocol());
        assertEquals(0, devicePropertiesAuth.getSecurityLevel());
    }
}