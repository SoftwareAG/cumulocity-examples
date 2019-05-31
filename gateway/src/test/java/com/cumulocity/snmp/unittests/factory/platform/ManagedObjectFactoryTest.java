package com.cumulocity.snmp.unittests.factory.platform;

import c8y.RequiredAvailability;
import c8y.SupportedOperations;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.snmp.configuration.service.SNMPConfigurationProperties;
import com.cumulocity.snmp.factory.platform.ManagedObjectFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ManagedObjectFactoryTest {

    @InjectMocks
    private ManagedObjectFactory managedObjectFactory;

    @Mock
    private SNMPConfigurationProperties config;

    @Test
    public void createManagedObject() {
        ManagedObjectRepresentation managedObjectRepresentation = managedObjectFactory.create("snmp-agent");
        Assert.assertEquals(managedObjectRepresentation.getType(), "c8y_SNMP");
    }

    @Test
    public void shouldCreateDeviceObjectWithRequiredDetails() {

        ManagedObjectRepresentation managedObjectRepresentation = managedObjectFactory.create("snmp-agent");

        Map<String, Object> map = managedObjectRepresentation.getAttrs();
        RequiredAvailability requiredAvailability = (RequiredAvailability) map.get("c8y_RequiredAvailability");

        Assert.assertEquals(10, requiredAvailability.getResponseInterval());
    }

    @Test
    public void shouldCreateDeviceObjectWithSupportFragments() {

        ManagedObjectRepresentation managedObjectRepresentation = managedObjectFactory.create("snmp-agent");

        Map<String, Object> map = managedObjectRepresentation.getAttrs();
        SupportedOperations supportedOperations = (SupportedOperations) map.get("c8y_SupportedOperations");

        Assert.assertEquals(1, supportedOperations.size());
    }

    @Test
    public void shouldCreateChildDeviceForAutoDiscoveryFlow() {
        when(config.getPollingPort()).thenReturn(161);
        ManagedObjectRepresentation managedObjectRepresentation = managedObjectFactory.createChildDevice("snmp-agent", "localhost");
        Map<String, Object> map = managedObjectRepresentation.getAttrs();
        Map<String, String> keyValue = (HashMap) map.get("c8y_SNMPDevice");

        Assert.assertEquals("161", keyValue.get("port"));
    }
}