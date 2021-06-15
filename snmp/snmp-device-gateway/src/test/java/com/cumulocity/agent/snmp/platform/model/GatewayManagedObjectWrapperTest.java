/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, 
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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