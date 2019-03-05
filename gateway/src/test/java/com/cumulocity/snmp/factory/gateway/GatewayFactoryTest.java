package com.cumulocity.snmp.factory.gateway;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.snmp.factory.platform.ManagedObjectMapper;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.google.common.base.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import static com.cumulocity.model.idtype.GId.asGId;
import static com.cumulocity.snmp.Conditions.present;
import static com.cumulocity.snmp.repository.configuration.RepositoryConfiguration.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.not;

@RunWith(MockitoJUnitRunner.class)
public class GatewayFactoryTest {

    @Spy
    GatewayFactory gatewayFactory;

    ManagedObjectMapper managedObjectMapper = new ManagedObjectMapper(objectMapper());

    @Before
    public void init(){
        gatewayFactory.setManagedObjectMapper(managedObjectMapper);
    }

    @Test
    public void shouldCreate() throws InvocationTargetException, IllegalAccessException {
        final HashMap<Object, Object> property = new HashMap<>();
        property.put("tenant", "New Tenant");
        property.put("name", "dummyUser");
        final ManagedObjectRepresentation managedObjectRepresentation = new ManagedObjectRepresentation();
        managedObjectRepresentation.setId(asGId("10400"));
        managedObjectRepresentation.setProperty("c8y_SNMPGateway", property);
        final DeviceCredentialsRepresentation deviceCredentialsRepresentation = new DeviceCredentialsRepresentation();
        deviceCredentialsRepresentation.setTenantId("dummyTenant");
        deviceCredentialsRepresentation.setUsername("dummyUsername");
        deviceCredentialsRepresentation.setPassword("dummyPassword");

        final Optional<Gateway> gatewayOptional = gatewayFactory.create(deviceCredentialsRepresentation, managedObjectRepresentation);

        assertThat(gatewayOptional).is(present());
        Assert.assertEquals(gatewayOptional.get().getTenant(),"dummyTenant");
        Assert.assertEquals(gatewayOptional.get().getName(),"dummyUsername");
        Assert.assertEquals(gatewayOptional.get().getPassword(),"dummyPassword");
        Assert.assertEquals(gatewayOptional.get().getId(),GId.asGId("10400"));
    }

    @Test
    public void shouldNotCreate() throws InvocationTargetException, IllegalAccessException {
        final ManagedObjectRepresentation managedObjectRepresentation = new ManagedObjectRepresentation();
        final DeviceCredentialsRepresentation deviceCredentialsRepresentation = new DeviceCredentialsRepresentation();

        Optional<Gateway> gatewayOptional = gatewayFactory.create(deviceCredentialsRepresentation, managedObjectRepresentation);

        assertThat(gatewayOptional).is(not(present()));
    }

}