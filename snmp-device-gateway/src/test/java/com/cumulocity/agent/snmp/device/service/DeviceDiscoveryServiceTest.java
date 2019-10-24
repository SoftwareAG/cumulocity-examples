package com.cumulocity.agent.snmp.device.service;

import c8y.RequiredAvailability;
import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.platform.model.AlarmSeverity;
import com.cumulocity.agent.snmp.platform.model.DeviceManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.model.GatewayManagedObjectWrapper;
import com.cumulocity.agent.snmp.platform.model.ReceivedOperationForGatewayEvent;
import com.cumulocity.agent.snmp.platform.pubsub.publisher.AlarmPublisher;
import com.cumulocity.agent.snmp.platform.service.GatewayDataProvider;
import com.cumulocity.agent.snmp.util.IpAddressUtil;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.util.ExtensibilityConverter;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.inventory.InventoryMediaType;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.RestOperations;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.google.common.net.InetAddresses;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

import static com.cumulocity.agent.snmp.platform.model.AlarmMapping.c8y_DeviceNotResponding;
import static com.cumulocity.agent.snmp.platform.model.AlarmMapping.c8y_DeviceSnmpNotEnabled;
import static com.cumulocity.agent.snmp.platform.model.DeviceManagedObjectWrapper.*;
import static com.cumulocity.agent.snmp.platform.model.ReceivedOperationForGatewayEvent.C8Y_SNMP_AUTO_DISCOVERY_FRAGMENT_KEY;
import static com.cumulocity.agent.snmp.platform.model.ReceivedOperationForGatewayEvent.IP_RANGE_KEY;
import static com.cumulocity.model.operation.OperationStatus.*;
import static org.junit.Assert.*;
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

    @Mock
    private GatewayManagedObjectWrapper.SnmpCommunicationProperties snmpCommunicationPropertiesFromPlatform;

    @Captor
    ArgumentCaptor<AlarmRepresentation> alarmRepresentationCaptor;

    @Captor
    ArgumentCaptor<ManagedObjectRepresentation> childDeviceCaptor;

    @Captor
    ArgumentCaptor<ManagedObjectReferenceRepresentation> childReferenceCaptor;

    @Spy
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
        deviceDiscoveryService.handleNoResponseFromDevice(type, text);

        // then
        verify(alarmPublisher, times(1)).publish(alarmRepresentationCaptor.capture());

        AlarmRepresentation alarmRepresentation = alarmRepresentationCaptor.getValue();
        assertEquals(AlarmSeverity.MAJOR.name(), alarmRepresentation.getSeverity());
        assertEquals(gatewayDeviceManagedObjectRepresentation, alarmRepresentation.getSource());
        assertEquals(type, alarmRepresentation.getType());
        assertEquals(text, alarmRepresentation.getText());
    }

    @Test
    public void shouldCreateAndRegisterAChildDeviceSuccessfully() {
        // given
        String childDeviceIp = "192.168.0.1";
        int port = 162;
        String childDevicesPath = "/inventory/managedObjects/111/childDevices";

        when(gatewayDataProvider.getGatewayDevice()).thenReturn(gatewayManagedObjectWrapper);
        when(gatewayManagedObjectWrapper.getChildDevicesPath()).thenReturn(childDevicesPath);

        ManagedObjectRepresentation newChildDevice = new ManagedObjectRepresentation();
        newChildDevice.setId(GId.asGId("NEW_CHILD_DEVICE"));
        when(inventoryApi.create(any(ManagedObjectRepresentation.class))).thenReturn(newChildDevice);

        // when
        deviceDiscoveryService.createAndRegisterAChildDevice(childDeviceIp, port);


        // then
        verifyChildDeviceCreated(childDeviceIp, port, childDevicesPath);
    }

    private void verifyChildDeviceCreated(String childDeviceIp, int port, String childDevicesPath) {
        verify(inventoryApi, times(1)).create(childDeviceCaptor.capture());
        ManagedObjectRepresentation createChildDeviceInput = childDeviceCaptor.getValue();
        assertEquals("Device-" + childDeviceIp, createChildDeviceInput.getName());
        assertEquals(new RequiredAvailability((int) ReflectionTestUtils.getField(DeviceDiscoveryService.class, "DEFAULT_CONNECTION_INTERVAL_IN_MINUTES")),
                createChildDeviceInput.getProperty(ExtensibilityConverter.classToStringRepresentation(RequiredAvailability.class)));

        Map<String, String> deviceIpMap = (Map<String, String>)createChildDeviceInput.get(C8Y_SNMP_DEVICE);
        assertEquals(childDeviceIp, deviceIpMap.get(SNMP_DEVICE_IP));
        assertEquals(String.valueOf(port), deviceIpMap.get(SNMP_DEVICE_PORT));

        verify(restOperations, times(1)).post(eq(childDevicesPath), eq(InventoryMediaType.MANAGED_OBJECT_REFERENCE), childReferenceCaptor.capture());
        assertEquals(GId.asGId("NEW_CHILD_DEVICE"), childReferenceCaptor.getValue().getManagedObject().getId());
    }

    @Test
    public void shouldParseIpRangesSuccessfully() {
        // given
        String givenIpRange =
                "192.168.0.1" + "-" + "192.168.0.2"
                        + ","
                        + "192.168.0.4" + "-" + "192.168.0.10"
                        + ","
                        + "2001:0db8:85a3:0000:0000:8a2e:0370:7334" + "-" + "2001:0db8:85a3:0000:0000:8a2e:0370:7337";

        // when
        List<InetAddress[]> parsedIpList = deviceDiscoveryService.parseIpRanges(givenIpRange);


        // then
        assertEquals(3, parsedIpList.size());

        InetAddress[] oneIpRange = parsedIpList.get(0);
        assertEquals(IpAddressUtil.forString("192.168.0.1"), oneIpRange[0]);
        assertEquals(IpAddressUtil.forString("192.168.0.2"), oneIpRange[1]);

        oneIpRange = parsedIpList.get(1);
        assertEquals(IpAddressUtil.forString("192.168.0.4"), oneIpRange[0]);
        assertEquals(IpAddressUtil.forString("192.168.0.10"), oneIpRange[1]);

        oneIpRange = parsedIpList.get(2);
        assertEquals(IpAddressUtil.forString("2001:0db8:85a3:0000:0000:8a2e:0370:7334"), oneIpRange[0]);
        assertEquals(IpAddressUtil.forString("2001:0db8:85a3:0000:0000:8a2e:0370:7337"), oneIpRange[1]);
    }

    @Test
    public void shouldParseIpRangesSuccessfullyWhenMultipleSeparators() {
        // given
        String givenIpRange =
                "192.168.0.1" + "-" + "192.168.0.2"
                        + ","
                        + ", ,"
                        + " ,  "
                        + "192.168.0.4" + "-" + "192.168.0.10"
                        + ","
                        + "2001:0db8:85a3:0000:0000:8a2e:0370:7334" + "-" + "2001:0db8:85a3:0000:0000:8a2e:0370:7337"
                        + ","
                        + ", ,"
                        + " ,  ";

        // when
        List<InetAddress[]> parsedIpList = deviceDiscoveryService.parseIpRanges(givenIpRange);


        // then
        assertEquals(3, parsedIpList.size());

        InetAddress[] oneIpRange = parsedIpList.get(0);
        assertEquals(IpAddressUtil.forString("192.168.0.1"), oneIpRange[0]);
        assertEquals(IpAddressUtil.forString("192.168.0.2"), oneIpRange[1]);

        oneIpRange = parsedIpList.get(1);
        assertEquals(IpAddressUtil.forString("192.168.0.4"), oneIpRange[0]);
        assertEquals(IpAddressUtil.forString("192.168.0.10"), oneIpRange[1]);

        oneIpRange = parsedIpList.get(2);
        assertEquals(IpAddressUtil.forString("2001:0db8:85a3:0000:0000:8a2e:0370:7334"), oneIpRange[0]);
        assertEquals(IpAddressUtil.forString("2001:0db8:85a3:0000:0000:8a2e:0370:7337"), oneIpRange[1]);
    }

    @Test
    public void parseEmptyIpRangesAndReturnEmptyList_1() {
        // given
        String givenIpRange = "";

        // when
        List<InetAddress[]> parsedIpList = deviceDiscoveryService.parseIpRanges(givenIpRange);

        // then
        assertTrue(parsedIpList.isEmpty());
    }

    @Test
    public void parseEmptyIpRangesAndReturnEmptyList_2() {
        // given
        String givenIpRange = " ,  , ,";

        // when
        List<InetAddress[]> parsedIpList = deviceDiscoveryService.parseIpRanges(givenIpRange);

        // then
        assertTrue(parsedIpList.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseIpRangesFailForInvalidIPRanges_1() {
        // given
        String givenIpRange = "---" + "-" + "192.168.0.2";

        // when
        List<InetAddress[]> parsedIpList = deviceDiscoveryService.parseIpRanges(givenIpRange);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseIpRangesFailForInvalidIPRanges_2() {
        // given
        String givenIpRange =
                "192.168.0.1" + "-" + "192.168.0.2"
                        + ","
                        + "192.168.0.4"
                        + ","
                        + "2001:0db8:85a3:0000:0000:8a2e:0370:7334" + "-" + "2001:0db8:85a3:0000:0000:8a2e:0370:7337";


        // when
        List<InetAddress[]> parsedIpList = deviceDiscoveryService.parseIpRanges(givenIpRange);
    }

    @Test
    public void shouldCheckIfDeviceSnmpEnabled() {
        // given
        InetAddress ipAddress = InetAddress.getLoopbackAddress();
        int port = 8080;
        boolean isProtocolUdp = true;
        String communityTarget = "public";

        // when
        boolean isEnabled = deviceDiscoveryService.isDeviceSnmpEnabled(ipAddress, port, isProtocolUdp, communityTarget);

        // then
        assertFalse(isEnabled);
    }


    @Test
    public void shouldScanForSnmpDevicesAndCreateChildDevices_successfully() {
        // given
        int port = 162;
        boolean isProtocolUdp = true;
        int pingTimeoutInSeconds = 1;
        String communityTarget = "public";

        String startIpAddress = "192.168.0.1"; // is reachable
        String endIpAddress = "192.168.0.2";   // is not reachable as the second one's isReachable is not mocked

        List<InetAddress[]> ipRangesList = new ArrayList<>();
        InetAddress[] oneRange = new InetAddress[2];
        oneRange[0] = mock(InetAddress.class); // InetAddresses.forString("192.168.0.1")
        oneRange[1] = mock(InetAddress.class); // InetAddresses.forString("192.168.0.2")
        try {
            doReturn(startIpAddress).when(oneRange[0]).getHostAddress();
            doReturn(InetAddresses.forString(startIpAddress).getAddress()).when(oneRange[0]).getAddress();
            doReturn(true).when(oneRange[0]).isReachable(pingTimeoutInSeconds * 1000);

//            doReturn(endIpAddress).when(oneRange[1]).getHostAddress();
            doReturn(InetAddresses.forString(endIpAddress).getAddress()).when(oneRange[1]).getAddress();
        } catch (IOException e) {
            fail(e.getMessage());
        }
        ipRangesList.add(oneRange);

        Map<String, DeviceManagedObjectWrapper> existingDeviceMap = new HashMap<>();
//        existingDeviceMap.put(oneRange[0].getHostAddress(), null);

        doReturn(true).when(deviceDiscoveryService).isDeviceSnmpEnabled(oneRange[0], port, isProtocolUdp, communityTarget);
        doNothing().when(deviceDiscoveryService).createAndRegisterAChildDevice(startIpAddress, port);

        // when
        deviceDiscoveryService.scanForSnmpDevicesAndCreateChildDevices(ipRangesList, port, isProtocolUdp, pingTimeoutInSeconds, communityTarget, existingDeviceMap);

        // then
        verify(deviceDiscoveryService, times(1)).createAndRegisterAChildDevice(eq(startIpAddress), eq(port));
        verify(deviceDiscoveryService, times(0)).createAndRegisterAChildDevice(eq(endIpAddress), eq(port));
    }

    @Test
    public void shouldScanForSnmpDevicesAndCreateChildDevices_handleNoResponseFromDevice() {
        // given
        int port = 162;
        boolean isProtocolUdp = true;
        int pingTimeoutInSeconds = 1;
        String communityTarget = "public";

        String startIpAddress = "192.168.0.1"; // is reachable but device is not snmp enabled
        String endIpAddress = "192.168.0.2";   // is not reachable as the second one's isReachable is not mocked and also it is an existing device

        List<InetAddress[]> ipRangesList = new ArrayList<>();
        InetAddress[] oneRange = new InetAddress[2];
        oneRange[0] = mock(InetAddress.class); // InetAddresses.forString("192.168.0.1")
        oneRange[1] = mock(InetAddress.class); // InetAddresses.forString("192.168.0.2")
        try {
            doReturn(startIpAddress).when(oneRange[0]).getHostAddress();
            doReturn(InetAddresses.forString(startIpAddress).getAddress()).when(oneRange[0]).getAddress();
            doReturn(true).when(oneRange[0]).isReachable(pingTimeoutInSeconds * 1000);

            doReturn(endIpAddress).when(oneRange[1]).getHostAddress();
            doReturn(InetAddresses.forString(endIpAddress).getAddress()).when(oneRange[1]).getAddress();
        } catch (IOException e) {
            fail(e.getMessage());
        }
        ipRangesList.add(oneRange);

        Map<String, DeviceManagedObjectWrapper> existingDeviceMap = new HashMap<>();
//        existingDeviceMap.put(oneRange[0].getHostAddress(), null);
        existingDeviceMap.put(oneRange[1].getHostAddress(), null);

        doReturn(false).when(deviceDiscoveryService).isDeviceSnmpEnabled(oneRange[0], port, isProtocolUdp, communityTarget);

        doNothing().when(deviceDiscoveryService).handleNoResponseFromDevice(any(String.class), eq(c8y_DeviceSnmpNotEnabled + startIpAddress));
        doNothing().when(deviceDiscoveryService).handleNoResponseFromDevice(any(String.class), eq(c8y_DeviceNotResponding + endIpAddress));

        // when
        deviceDiscoveryService.scanForSnmpDevicesAndCreateChildDevices(ipRangesList, port, isProtocolUdp, pingTimeoutInSeconds, communityTarget, existingDeviceMap);

        // then
        verify(deviceDiscoveryService, times(1)).handleNoResponseFromDevice(any(String.class), eq(c8y_DeviceSnmpNotEnabled + startIpAddress));
        verify(deviceDiscoveryService, times(1)).handleNoResponseFromDevice(any(String.class), eq(c8y_DeviceNotResponding + endIpAddress));
    }

    @Test
    public void shouldExecuteOperation_successfully() {
        // given
        String givenIpRange =
                "192.168.0.1" + "-" + "192.168.0.2"
                        + ","
                        + "192.168.0.4" + "-" + "192.168.0.10"
                        + ","
                        + "2001:0db8:85a3:0000:0000:8a2e:0370:7334" + "-" + "2001:0db8:85a3:0000:0000:8a2e:0370:7337";
        int port = 162;
        boolean isProtocolUdp = true;
        int pingTimeoutInSeconds = 1;
        String communityTarget = "public";
        Map<String, DeviceManagedObjectWrapper> snmpDeviceMap = new HashMap<>();
        GId operationId = GId.asGId("111");


        OperationRepresentation operation = new TestOperationRepresentation(operationId, PENDING.name(), null);
        Map<String, String> autoDiscoveryFragment = new HashMap<>();
        autoDiscoveryFragment.put(IP_RANGE_KEY, givenIpRange);
        operation.set(autoDiscoveryFragment, C8Y_SNMP_AUTO_DISCOVERY_FRAGMENT_KEY);

        ReceivedOperationForGatewayEvent operationEvent = new ReceivedOperationForGatewayEvent(operationId, "snmp-agent-test", operation);

        when(snmpProperties.getPollingPort()).thenReturn(port);
        when(snmpProperties.isTrapListenerProtocolUdp()).thenReturn(isProtocolUdp);
        when(snmpProperties.getAutoDiscoveryDevicePingTimeoutPeriod()).thenReturn(pingTimeoutInSeconds);
        when(snmpProperties.getCommunityTarget()).thenReturn(communityTarget);

        when(gatewayDataProvider.getSnmpDeviceMap()).thenReturn(snmpDeviceMap);

        doNothing().when(deviceDiscoveryService).scanForSnmpDevicesAndCreateChildDevices(any(List.class),
                eq(port), eq(isProtocolUdp), eq(pingTimeoutInSeconds), eq(communityTarget), eq(snmpDeviceMap));

        // when
        deviceDiscoveryService.executeOperation(operationEvent);

        // then
        verify(deviceDiscoveryService, times(1)).scanForSnmpDevicesAndCreateChildDevices(any(List.class),
                eq(port), eq(isProtocolUdp), eq(pingTimeoutInSeconds), eq(communityTarget), eq(snmpDeviceMap));

        verify(deviceControlApi, times(2)).update(eq(new TestOperationRepresentation(operationId, SUCCESSFUL.name(), null)));
    }

    @Test
    public void shouldExecuteOperation_failureWithThrowable() {
        // given
        String givenIpRange =
                "192.168.0.1" + "-" + "192.168.0.2"
                        + ","
                        + "192.168.0.4" + "-" + "192.168.0.10"
                        + ","
                        + "2001:0db8:85a3:0000:0000:8a2e:0370:7334" + "-" + "2001:0db8:85a3:0000:0000:8a2e:0370:7337";
        int port = 162;
        boolean isProtocolUdp = true;
        int pingTimeoutInSeconds = 1;
        String communityTarget = "public";
        Map<String, DeviceManagedObjectWrapper> snmpDeviceMap = new HashMap<>();
        GId operationId = GId.asGId("111");


        OperationRepresentation operation = new TestOperationRepresentation(operationId, PENDING.name(), null);
        Map<String, String> autoDiscoveryFragment = new HashMap<>();
        autoDiscoveryFragment.put(IP_RANGE_KEY, givenIpRange);
        operation.set(autoDiscoveryFragment, C8Y_SNMP_AUTO_DISCOVERY_FRAGMENT_KEY);

        ReceivedOperationForGatewayEvent operationEvent = new ReceivedOperationForGatewayEvent(operationId, "snmp-agent-test", operation);

        when(snmpProperties.getPollingPort()).thenReturn(port);
        when(snmpProperties.isTrapListenerProtocolUdp()).thenReturn(isProtocolUdp);
        when(snmpProperties.getAutoDiscoveryDevicePingTimeoutPeriod()).thenReturn(pingTimeoutInSeconds);
        when(snmpProperties.getCommunityTarget()).thenReturn(communityTarget);

        when(gatewayDataProvider.getSnmpDeviceMap()).thenReturn(snmpDeviceMap);

        doThrow(new NullPointerException("SOME EXCEPTION")).when(deviceDiscoveryService).scanForSnmpDevicesAndCreateChildDevices(any(List.class),
                eq(port), eq(isProtocolUdp), eq(pingTimeoutInSeconds), eq(communityTarget), eq(snmpDeviceMap));

        // when
        deviceDiscoveryService.executeOperation(operationEvent);

        // then
        verify(deviceDiscoveryService, times(1)).scanForSnmpDevicesAndCreateChildDevices(any(List.class),
                eq(port), eq(isProtocolUdp), eq(pingTimeoutInSeconds), eq(communityTarget), eq(snmpDeviceMap));

        verify(deviceControlApi, times(2)).update(eq(new TestOperationRepresentation(operationId, FAILED.name(), "Unexpected error occurred while scanning the network for SNMP devices.")));
    }

    @Test
    public void shouldExecuteOperation_failureWithInvalidIPRange() {
        // given
        String givenIpRange =
                "192.168.0.1" + "-" + "192.168.0.2"
                        + ","
                        + "192.168.0.4"
                        + ","
                        + "2001:0db8:85a3:0000:0000:8a2e:0370:7334" + "-" + "2001:0db8:85a3:0000:0000:8a2e:0370:7337";
        GId operationId = GId.asGId("111");


        OperationRepresentation operation = new TestOperationRepresentation(operationId, PENDING.name(), null);
        Map<String, String> autoDiscoveryFragment = new HashMap<>();
        autoDiscoveryFragment.put(IP_RANGE_KEY, givenIpRange);
        operation.set(autoDiscoveryFragment, C8Y_SNMP_AUTO_DISCOVERY_FRAGMENT_KEY);

        ReceivedOperationForGatewayEvent operationEvent = new ReceivedOperationForGatewayEvent(operationId, "snmp-agent-test", operation);

        // when
        deviceDiscoveryService.executeOperation(operationEvent);

        // then
        verify(deviceDiscoveryService, times(0)).scanForSnmpDevicesAndCreateChildDevices(any(List.class),
                anyInt(), anyBoolean(), anyInt(), anyString(), anyMap());
        verify(deviceControlApi, times(2)).update(eq(new TestOperationRepresentation(operationId, FAILED.name(), "Error while parsing the provided " + givenIpRange + " IP Address ranges to scan for devices.")));
    }

    @Test
    public void shouldExecuteOperation_failureWithNoIpRanges() {
        // given
        String givenIpRange = null;
        GId operationId = GId.asGId("111");


        OperationRepresentation operation = new TestOperationRepresentation(operationId, PENDING.name(), null);
        Map<String, String> autoDiscoveryFragment = new HashMap<>();
        autoDiscoveryFragment.put(IP_RANGE_KEY, givenIpRange);
        operation.set(autoDiscoveryFragment, C8Y_SNMP_AUTO_DISCOVERY_FRAGMENT_KEY);

        ReceivedOperationForGatewayEvent operationEvent = new ReceivedOperationForGatewayEvent(operationId, "snmp-agent-test", operation);

        // when
        deviceDiscoveryService.executeOperation(operationEvent);

        // then
        verify(deviceDiscoveryService, times(0)).scanForSnmpDevicesAndCreateChildDevices(any(List.class),
                anyInt(), anyBoolean(), anyInt(), anyString(), anyMap());
        verify(deviceControlApi, times(2)).update(eq(new TestOperationRepresentation(operationId, FAILED.name(), "Didn't provide the IP Address ranges to scan for devices.")));
    }

    @Test
    public void shouldScheduleAutoDiscoveryProcess_successfully() {
        // given
        String givenIpRange =
                "192.168.0.1" + "-" + "192.168.0.2"
                        + ","
                        + "192.168.0.4" + "-" + "192.168.0.10"
                        + ","
                        + "2001:0db8:85a3:0000:0000:8a2e:0370:7334" + "-" + "2001:0db8:85a3:0000:0000:8a2e:0370:7337";
        long autoDiscoveryInterval = 10;

        when(gatewayDataProvider.getGatewayDevice()).thenReturn(gatewayManagedObjectWrapper);
        when(gatewayManagedObjectWrapper.getSnmpCommunicationProperties()).thenReturn(snmpCommunicationPropertiesFromPlatform);

        when(snmpCommunicationPropertiesFromPlatform.getIpRange()).thenReturn(givenIpRange);
        when(snmpCommunicationPropertiesFromPlatform.getAutoDiscoveryInterval()).thenReturn(autoDiscoveryInterval);

        ScheduledFuture autoDiscoveryScheduleMock = mock(ScheduledFuture.class);
        doReturn(autoDiscoveryScheduleMock).when(taskScheduler).scheduleWithFixedDelay(any(Runnable.class), eq(Duration.ofMinutes(autoDiscoveryInterval)));

        // when
        deviceDiscoveryService.scheduleAutoDiscoveryProcess();

        // then
        verify(taskScheduler).scheduleWithFixedDelay(any(Runnable.class), eq(Duration.ofMinutes(autoDiscoveryInterval)));
        assertNotNull(ReflectionTestUtils.getField(deviceDiscoveryService, "autoDiscoverySchedule"));
        assertNotNull(ReflectionTestUtils.getField(deviceDiscoveryService, "autoDiscoveryIpRangesList"));
        assertEquals(givenIpRange, ReflectionTestUtils.getField(deviceDiscoveryService, "autoDiscoveryIpRanges"));
        assertEquals(autoDiscoveryInterval, ReflectionTestUtils.getField(deviceDiscoveryService, "autoDiscoveryScheduleInterval"));
    }

    @Test
    public void shouldNotScheduleAutoDiscoveryProcess_withEmptyIpRanges() {
        // given
        String givenIpRange = "";
        long autoDiscoveryInterval = 10;

        when(gatewayDataProvider.getGatewayDevice()).thenReturn(gatewayManagedObjectWrapper);
        when(gatewayManagedObjectWrapper.getSnmpCommunicationProperties()).thenReturn(snmpCommunicationPropertiesFromPlatform);

        when(snmpCommunicationPropertiesFromPlatform.getIpRange()).thenReturn(givenIpRange);
        when(snmpCommunicationPropertiesFromPlatform.getAutoDiscoveryInterval()).thenReturn(autoDiscoveryInterval);

        // when
        deviceDiscoveryService.scheduleAutoDiscoveryProcess();

        // then
        verifyZeroInteractions(taskScheduler);
        assertNull(ReflectionTestUtils.getField(deviceDiscoveryService, "autoDiscoverySchedule"));
        assertNull(ReflectionTestUtils.getField(deviceDiscoveryService, "autoDiscoveryIpRangesList"));
        assertEquals(givenIpRange, ReflectionTestUtils.getField(deviceDiscoveryService, "autoDiscoveryIpRanges"));
        assertEquals(autoDiscoveryInterval, ReflectionTestUtils.getField(deviceDiscoveryService, "autoDiscoveryScheduleInterval"));
    }

    @Test
    public void shouldNotScheduleAutoDiscoveryProcess_withZeroScheduleInterval() {
        // given
        String givenIpRange =
                "192.168.0.1" + "-" + "192.168.0.2"
                        + ","
                        + "192.168.0.4" + "-" + "192.168.0.10"
                        + ","
                        + "2001:0db8:85a3:0000:0000:8a2e:0370:7334" + "-" + "2001:0db8:85a3:0000:0000:8a2e:0370:7337";
        long autoDiscoveryInterval = 0;

        when(gatewayDataProvider.getGatewayDevice()).thenReturn(gatewayManagedObjectWrapper);
        when(gatewayManagedObjectWrapper.getSnmpCommunicationProperties()).thenReturn(snmpCommunicationPropertiesFromPlatform);

        when(snmpCommunicationPropertiesFromPlatform.getIpRange()).thenReturn(givenIpRange);
        when(snmpCommunicationPropertiesFromPlatform.getAutoDiscoveryInterval()).thenReturn(autoDiscoveryInterval);

        // when
        deviceDiscoveryService.scheduleAutoDiscoveryProcess();

        // then
        verifyZeroInteractions(taskScheduler);
        assertNull(ReflectionTestUtils.getField(deviceDiscoveryService, "autoDiscoverySchedule"));
        assertNull(ReflectionTestUtils.getField(deviceDiscoveryService, "autoDiscoveryIpRangesList"));
        assertEquals(givenIpRange, ReflectionTestUtils.getField(deviceDiscoveryService, "autoDiscoveryIpRanges"));
        assertEquals(autoDiscoveryInterval, ReflectionTestUtils.getField(deviceDiscoveryService, "autoDiscoveryScheduleInterval"));
    }

    @Test
    public void shouldNotScheduleAutoDiscoveryProcess_withInvalidIpRange() {
        // given
        String givenIpRange =
                "192.168.0.1" + "-" + "192.168.0.2"
                        + ","
                        + "192.168.0.4"
                        + ","
                        + "2001:0db8:85a3:0000:0000:8a2e:0370:7334" + "-" + "2001:0db8:85a3:0000:0000:8a2e:0370:7337";
        long autoDiscoveryInterval = 10;

        when(gatewayDataProvider.getGatewayDevice()).thenReturn(gatewayManagedObjectWrapper);
        when(gatewayManagedObjectWrapper.getSnmpCommunicationProperties()).thenReturn(snmpCommunicationPropertiesFromPlatform);

        when(snmpCommunicationPropertiesFromPlatform.getIpRange()).thenReturn(givenIpRange);
        when(snmpCommunicationPropertiesFromPlatform.getAutoDiscoveryInterval()).thenReturn(autoDiscoveryInterval);

        // when
        deviceDiscoveryService.scheduleAutoDiscoveryProcess();

        // then
        verifyZeroInteractions(taskScheduler);
        assertNull(ReflectionTestUtils.getField(deviceDiscoveryService, "autoDiscoverySchedule"));
        assertNull(ReflectionTestUtils.getField(deviceDiscoveryService, "autoDiscoveryIpRangesList"));
        assertEquals(givenIpRange, ReflectionTestUtils.getField(deviceDiscoveryService, "autoDiscoveryIpRanges"));
        assertEquals(autoDiscoveryInterval, ReflectionTestUtils.getField(deviceDiscoveryService, "autoDiscoveryScheduleInterval"));
    }

    @Test
    public void shouldRefreshAutoDiscoverySchedule_successfully() {
        // given
        // given
        String givenIpRange =
                "192.168.0.1" + "-" + "192.168.0.2"
                        + ","
                        + "192.168.0.4" + "-" + "192.168.0.10"
                        + ","
                        + "2001:0db8:85a3:0000:0000:8a2e:0370:7334" + "-" + "2001:0db8:85a3:0000:0000:8a2e:0370:7337";
        long autoDiscoveryInterval = 10;

        when(gatewayDataProvider.getGatewayDevice()).thenReturn(gatewayManagedObjectWrapper);
        when(gatewayManagedObjectWrapper.getSnmpCommunicationProperties()).thenReturn(snmpCommunicationPropertiesFromPlatform);

        when(snmpCommunicationPropertiesFromPlatform.getIpRange()).thenReturn(givenIpRange);
        when(snmpCommunicationPropertiesFromPlatform.getAutoDiscoveryInterval()).thenReturn(autoDiscoveryInterval);

        ScheduledFuture autoDiscoveryScheduleMock = mock(ScheduledFuture.class);
        ReflectionTestUtils.setField(deviceDiscoveryService, "autoDiscoverySchedule", autoDiscoveryScheduleMock);
        ReflectionTestUtils.setField(deviceDiscoveryService, "autoDiscoveryIpRanges", "");
        ReflectionTestUtils.setField(deviceDiscoveryService, "autoDiscoveryScheduleInterval", 5);

        // when
        deviceDiscoveryService.refreshAutoDiscoverySchedule();

        // then
        verify(autoDiscoveryScheduleMock).cancel(eq(true));
        assertNull(ReflectionTestUtils.getField(deviceDiscoveryService, "autoDiscoverySchedule"));

        verify(deviceDiscoveryService).scheduleAutoDiscoveryProcess();
    }

    @Test
    public void shouldRefreshAutoDiscoverySchedule_evenWhenIpRangesAreSameButIntervalChanges() {
        // given
        // given
        String givenIpRange =
                "192.168.0.1" + "-" + "192.168.0.2"
                        + ","
                        + "192.168.0.4" + "-" + "192.168.0.10"
                        + ","
                        + "2001:0db8:85a3:0000:0000:8a2e:0370:7334" + "-" + "2001:0db8:85a3:0000:0000:8a2e:0370:7337";
        long autoDiscoveryInterval = 10;

        when(gatewayDataProvider.getGatewayDevice()).thenReturn(gatewayManagedObjectWrapper);
        when(gatewayManagedObjectWrapper.getSnmpCommunicationProperties()).thenReturn(snmpCommunicationPropertiesFromPlatform);

        when(snmpCommunicationPropertiesFromPlatform.getIpRange()).thenReturn(givenIpRange);
        when(snmpCommunicationPropertiesFromPlatform.getAutoDiscoveryInterval()).thenReturn(autoDiscoveryInterval);

        ScheduledFuture autoDiscoveryScheduleMock = mock(ScheduledFuture.class);
        ReflectionTestUtils.setField(deviceDiscoveryService, "autoDiscoverySchedule", autoDiscoveryScheduleMock);
        ReflectionTestUtils.setField(deviceDiscoveryService, "autoDiscoveryIpRanges", givenIpRange);
        ReflectionTestUtils.setField(deviceDiscoveryService, "autoDiscoveryScheduleInterval", 5);

        // when
        deviceDiscoveryService.refreshAutoDiscoverySchedule();

        // then
        verify(autoDiscoveryScheduleMock).cancel(eq(true));
        assertNull(ReflectionTestUtils.getField(deviceDiscoveryService, "autoDiscoverySchedule"));

        verify(deviceDiscoveryService).scheduleAutoDiscoveryProcess();
    }

    @Test
    public void shouldNotRefreshAutoDiscoverySchedule_NeitherIpRangesNorIntervalHasChanged() {
        // given
        // given
        String givenIpRange =
                "192.168.0.1" + "-" + "192.168.0.2"
                        + ","
                        + "192.168.0.4" + "-" + "192.168.0.10"
                        + ","
                        + "2001:0db8:85a3:0000:0000:8a2e:0370:7334" + "-" + "2001:0db8:85a3:0000:0000:8a2e:0370:7337";
        long autoDiscoveryInterval = 10;

        when(gatewayDataProvider.getGatewayDevice()).thenReturn(gatewayManagedObjectWrapper);
        when(gatewayManagedObjectWrapper.getSnmpCommunicationProperties()).thenReturn(snmpCommunicationPropertiesFromPlatform);

        when(snmpCommunicationPropertiesFromPlatform.getIpRange()).thenReturn(givenIpRange);
        when(snmpCommunicationPropertiesFromPlatform.getAutoDiscoveryInterval()).thenReturn(autoDiscoveryInterval);

        ScheduledFuture autoDiscoveryScheduleMock = mock(ScheduledFuture.class);
        ReflectionTestUtils.setField(deviceDiscoveryService, "autoDiscoverySchedule", autoDiscoveryScheduleMock);
        ReflectionTestUtils.setField(deviceDiscoveryService, "autoDiscoveryIpRanges", givenIpRange);
        ReflectionTestUtils.setField(deviceDiscoveryService, "autoDiscoveryScheduleInterval", autoDiscoveryInterval);

        // when
        deviceDiscoveryService.refreshAutoDiscoverySchedule();

        // then
        verifyZeroInteractions(autoDiscoveryScheduleMock);
        assertNotNull(ReflectionTestUtils.getField(deviceDiscoveryService, "autoDiscoverySchedule"));

        verify(deviceDiscoveryService, times(0)).scheduleAutoDiscoveryProcess();
    }

    /**
     * Overriden the equals to just match the ID, Status and failureReason
     * which gets used while verifying the update method invocation
     * the deviceControlApi
     */
    private static class TestOperationRepresentation extends OperationRepresentation {
        public TestOperationRepresentation(GId id, String status, String failureReason) {
            setId(id);
            setStatus(status);
            setFailureReason(failureReason);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OperationRepresentation that = (OperationRepresentation) o;
            return Objects.equals(getId(), that.getId()) &&
                    Objects.equals(getStatus(), that.getStatus()) &&
                    Objects.equals(getFailureReason(), that.getFailureReason());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getId(), getStatus(), getFailureReason());
        }
    }
}