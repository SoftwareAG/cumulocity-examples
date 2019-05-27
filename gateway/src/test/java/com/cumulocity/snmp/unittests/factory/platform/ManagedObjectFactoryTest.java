package com.cumulocity.snmp.unittests.factory.platform;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.snmp.factory.platform.ManagedObjectFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ManagedObjectFactoryTest {

    @InjectMocks
    ManagedObjectFactory managedObjectFactory;

    @Test
    public void createManagedObject() {
        ManagedObjectRepresentation managedObjectRepresentation = managedObjectFactory.create("snmp-agent");
        Assert.assertEquals(managedObjectRepresentation.getType(), "c8y_SNMP");
    }
}