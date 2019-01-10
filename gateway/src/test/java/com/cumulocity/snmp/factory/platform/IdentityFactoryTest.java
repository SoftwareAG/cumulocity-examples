package com.cumulocity.snmp.factory.platform;

import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;

import static com.cumulocity.model.idtype.GId.asGId;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class IdentityFactoryTest {

    @InjectMocks
    IdentityFactory identityFactory;

    @Test
    public void createExternalId(){

        final HashMap<Object, Object> property = new HashMap<>();
        property.put("transmitRate", "20");
        property.put("userIdentityName", "dummyUser");
        final ManagedObjectRepresentation managedObjectRepresentation = new ManagedObjectRepresentation();
        managedObjectRepresentation.setId(asGId("10400"));
        managedObjectRepresentation.setProperty("c8y_SNMPGateway", property);
        ExternalIDRepresentation externalIDRepresentation = identityFactory.create("snmp-agent", managedObjectRepresentation);

        Assert.assertEquals(externalIDRepresentation.getType(),"c8y_Serial");

    }
}