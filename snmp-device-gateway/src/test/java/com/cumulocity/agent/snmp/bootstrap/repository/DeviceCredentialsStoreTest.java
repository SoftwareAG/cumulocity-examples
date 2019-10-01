package com.cumulocity.agent.snmp.bootstrap.repository;

import com.cumulocity.agent.snmp.bootstrap.model.DeviceCredentialsKey;
import com.cumulocity.agent.snmp.config.GatewayProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;

import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class DeviceCredentialsStoreTest {

    @Mock
    GatewayProperties gatewayProperties;

    DeviceCredentialsStore deviceCredentialsStore;

    @Captor
    ArgumentCaptor<String> mapName;

    @Captor
    ArgumentCaptor<Class<DeviceCredentialsKey>> keyClass;

    @Captor
    ArgumentCaptor<Double> averageKeySize;

    @Captor
    ArgumentCaptor<Class<String>> typeClass;

    @Captor
    ArgumentCaptor<Double> averageValueSize;

    @Captor
    ArgumentCaptor<Long> entries;

    @Captor
    ArgumentCaptor<File> persistenceFile;


    @Before
    public void setUp() {
        Mockito.when(gatewayProperties.getGatewayIdentifier()).thenReturn("snmp-agent-test");
        deviceCredentialsStore = Mockito.spy(new DeviceCredentialsStore(gatewayProperties));
    }

    @After
    public void tearDown() {
        try {
            deviceCredentialsStore.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

//    @Test
//    public void should() {
//        Mockito.verify(deviceCredentialsStore).(argCaptor.capture());
//
//        assertEquals(deviceCredentialsStore.getName(), "device-credentials-store");
//    }
}