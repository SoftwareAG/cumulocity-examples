package com.cumulocity.agent.snmp.device.service;

import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.platform.model.AlarmSeverity;
import com.cumulocity.agent.snmp.platform.model.GatewayManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.pubsub.publisher.AlarmPublisher;
import com.cumulocity.agent.snmp.platform.pubsub.subscriber.AlarmSubscriber;
import com.cumulocity.agent.snmp.platform.service.GatewayDataProvider;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.RestOperations;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class DeviceDiscoveryServiceTest {

    @Mock
    private GatewayProperties.SnmpProperties snmpProperties;

    @Mock
    private GatewayDataProvider gatewayDataProvider;

    @Mock
    private TaskScheduler taskScheduler;

    @Mock
    private InventoryApi inventoryApi;

    @Mock
    private RestOperations restOperations;

    @Mock
    private DeviceControlApi deviceControlApi;

    @Mock
    private AlarmPublisher alarmPublisher;

    @Mock
    private GatewayManagedObjectWrapper gatewayManagedObjectWrapper;

    @Mock
    private ManagedObjectRepresentation gatewayDeviceManagedObjectRepresentation;

    @Captor
    ArgumentCaptor<AlarmRepresentation> alarmRepresentationCaptor;

    @InjectMocks
    private DeviceDiscoveryService deviceDiscoveryService;


    @Test
    public void shouldHandleNoResponseFromDeviceSuccessfully() {
        // given
        String type = "c8y_someType";
        String text = "SOME ALARM TEXT";

        when(gatewayDataProvider.getGatewayDevice()).thenReturn(gatewayManagedObjectWrapper);
        when(gatewayManagedObjectWrapper.getManagedObject()).thenReturn(gatewayDeviceManagedObjectRepresentation);

        // when
        ReflectionTestUtils.invokeMethod(deviceDiscoveryService, "handleNoResponseFromDevice", type, text);

        // then
        verify(alarmPublisher, times(1)).publish(alarmRepresentationCaptor.capture());

        AlarmRepresentation alarmRepresentation = alarmRepresentationCaptor.getValue();
        assertEquals(AlarmSeverity.MAJOR.name(), alarmRepresentation.getSeverity());
        assertEquals(gatewayDeviceManagedObjectRepresentation, alarmRepresentation.getSource());
        assertEquals(type, alarmRepresentation.getType());
        assertEquals(text, alarmRepresentation.getText());
    }
}