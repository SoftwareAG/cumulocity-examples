package com.cumulocity.agent.snmp.platform.model;

import com.cumulocity.model.JSONBase;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GatewayManagedObjectWrapperTest {

    @Test
    public void shouldCreateGatewayManagedObjectWrapperSuccessfully() {
        ManagedObjectRepresentation gatewayMo = JSONBase.fromJSON("{\"id\":\"111\",\"name\":\"snmp-agent-test\",\"c8y_SNMPGateway\":{\"transmitRate\":10,\"ipRange\":\"192.168.0.1-192.168.0.3,192.168.0.6-192.168.0.7\",\"pollingRate\":100,\"autoDiscoveryInterval\":5,\"maxFieldbusVersion\":4}}", ManagedObjectRepresentation.class);
        GatewayManagedObjectWrapper gatewayManagedObjectWrapper = new GatewayManagedObjectWrapper(gatewayMo);

        assertEquals("111", gatewayManagedObjectWrapper.getId().getValue());
        assertEquals("snmp-agent-test", gatewayManagedObjectWrapper.getName());
        assertEquals("/inventory/managedObjects/111/childDevices", gatewayManagedObjectWrapper.getChildDevicesPath());

        GatewayManagedObjectWrapper.SnmpCommunicationProperties snmpCommunicationProperties = gatewayManagedObjectWrapper.getSnmpCommunicationProperties();

        assertNotNull(snmpCommunicationProperties);
        assertEquals("192.168.0.1-192.168.0.3,192.168.0.6-192.168.0.7", snmpCommunicationProperties.getIpRange());
        assertEquals(10, snmpCommunicationProperties.getTransmitRate());
        assertEquals(5, snmpCommunicationProperties.getAutoDiscoveryInterval());
        assertEquals(100, snmpCommunicationProperties.getPollingRate());
    }

    @Test
    public void shouldCreateGatewayManagedObjectWrapperWithEmptySnmpCommunicationProperties() {
        ManagedObjectRepresentation gatewayMo = JSONBase.fromJSON("{\"id\":\"111\",\"name\":\"snmp-agent-test\",\"c8y_SNMPGateway\":{}}", ManagedObjectRepresentation.class);
        GatewayManagedObjectWrapper gatewayManagedObjectWrapper = new GatewayManagedObjectWrapper(gatewayMo);

        assertEquals("111", gatewayManagedObjectWrapper.getId().getValue());
        assertEquals("snmp-agent-test", gatewayManagedObjectWrapper.getName());
        assertEquals("/inventory/managedObjects/111/childDevices", gatewayManagedObjectWrapper.getChildDevicesPath());

        GatewayManagedObjectWrapper.SnmpCommunicationProperties snmpCommunicationProperties = gatewayManagedObjectWrapper.getSnmpCommunicationProperties();

        assertEquals(null, snmpCommunicationProperties.getIpRange());
        assertEquals(0, snmpCommunicationProperties.getTransmitRate());
        assertEquals(0, snmpCommunicationProperties.getAutoDiscoveryInterval());
        assertEquals(0, snmpCommunicationProperties.getPollingRate());
    }

    @Test
    public void shouldCreateGatewayManagedObjectWrapperWithNoSnmpCommunicationProperties() {
        ManagedObjectRepresentation gatewayMo = JSONBase.fromJSON("{\"id\":\"111\",\"name\":\"snmp-agent-test\"}", ManagedObjectRepresentation.class);
        GatewayManagedObjectWrapper gatewayManagedObjectWrapper = new GatewayManagedObjectWrapper(gatewayMo);

        assertEquals("111", gatewayManagedObjectWrapper.getId().getValue());
        assertEquals("snmp-agent-test", gatewayManagedObjectWrapper.getName());
        assertEquals("/inventory/managedObjects/111/childDevices", gatewayManagedObjectWrapper.getChildDevicesPath());

        GatewayManagedObjectWrapper.SnmpCommunicationProperties snmpCommunicationProperties = gatewayManagedObjectWrapper.getSnmpCommunicationProperties();

        assertNotNull(snmpCommunicationProperties);
        assertEquals(null, snmpCommunicationProperties.getIpRange());
        assertEquals(0, snmpCommunicationProperties.getTransmitRate());
        assertEquals(0, snmpCommunicationProperties.getAutoDiscoveryInterval());
        assertEquals(0, snmpCommunicationProperties.getPollingRate());
    }
}