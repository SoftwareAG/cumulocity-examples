package com.cumulocity.snmp.integration;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.snmp.configuration.service.GatewayConfigurationProperties;
import com.cumulocity.snmp.integration.platform.service.MeasurementMockService;
import com.cumulocity.snmp.model.device.DeviceAddedEvent;
import com.cumulocity.snmp.model.gateway.DeviceTypeAddedEvent;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.gateway.GatewayAddedEvent;
import com.cumulocity.snmp.model.gateway.UnknownTrapOrDeviceEvent;
import com.cumulocity.snmp.model.gateway.client.ClientDataChangedEvent;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.cumulocity.snmp.model.gateway.type.mapping.AlarmCreatedEvent;
import com.cumulocity.snmp.model.operation.OperationEvent;
import com.cumulocity.snmp.repository.OperationRepository;
import com.cumulocity.snmp.repository.core.Repository;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.VariableBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.cumulocity.snmp.integration.client.SNMPDevice.startSnmpSimulator;
import static com.cumulocity.snmp.model.gateway.device.Device.c8y_SNMPDevice;
import static com.cumulocity.snmp.model.gateway.type.mapping.AlarmMapping.c8y_TRAPReceivedFromUnknownDevice;
import static com.cumulocity.snmp.repository.configuration.ContextProvider.doInvoke;
import static com.cumulocity.snmp.utils.SimpleTypeUtils.GID_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;

public class MainTest extends BaseIntegrationTest {

    private static final String community = "public";
    private static final String Oid = "1.3.6.1.2.1.34.4.0.1";
    private static final String ipAddress = "127.0.0.1";
    private static final int port = 6690;

    private static int snmpDevicePort = 9000;
    private static String engineId = "123456";

    private static ExecutorService executorService = Executors.newFixedThreadPool(1);

    @Autowired
    private Repository<Gateway> gatewayRepository;

    @Autowired
    private Repository<Device> deviceRepository;

    @Autowired
    private GatewayConfigurationProperties properties;

    @MockBean
    private OperationRepository operationRepository;

    /*Execute only once*/
    @BeforeClass
    public static void init() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    startSnmpSimulator(ipAddress, snmpDevicePort, engineId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Test
    public void shouldBootstrapNewGateway() throws InvocationTargetException, IllegalAccessException {
        //when
        registerAndSyncGateway();

        //then
        assertThat(gatewayRepository.findAll()).isNotEmpty();
        assertThat(inventoryMockService.findByType("c8y_SNMP").getManagedObjects()).isNotEmpty();
    }

    @Test
    public void shouldSaveNewDeviceWhenChildDeviceConfigured() throws InvocationTargetException, IllegalAccessException, InterruptedException {
        //given
        final ManagedObjectRepresentation deviceType = createDeviceType(Oid);
        final ManagedObjectRepresentation device = createDevice("10.23.52.51", deviceType.getId(), SnmpConstants.version2c);

        //when
        GatewayAddedEvent gatewayAddedEvent = registerAndSyncGateway();

        Thread.sleep(2000);
        inventoryMockService.addChildDevice(gatewayAddedEvent.getGateway().getId(), device);
        eventWatcher.waitFor(DeviceAddedEvent.class);

        //then
        doInvoke(gatewayAddedEvent.getGateway(), new Runnable() {
            public void run() {
                assertThat(deviceRepository.exists(device.getId())).isTrue();
            }
        });
    }

    @Test
    public void shouldsendV1Trap() throws InvocationTargetException, IllegalAccessException, InterruptedException {
        //given
        registerAndSyncGateway();

        Thread.sleep(2000);

        //when
        sendTrap(ipAddress, port, Oid, community, SnmpConstants.version1);
        UnknownTrapOrDeviceEvent unknownTrapOrDeviceEvent = eventWatcher.waitFor(UnknownTrapOrDeviceEvent.class);

        //then
//        Assert.assertTrue(unknownTrapOrDeviceEvent.getFragmentType().equals(c8y_TRAPReceivedFromUnknownDevice + ipAddress));

        //when
        AlarmCreatedEvent alarmCreatedEvent = eventWatcher.waitFor(AlarmCreatedEvent.class);

        //then
        Assertions.assertThat(alarmCreatedEvent).isNotNull();
//        Assert.assertTrue(alarmCreatedEvent.getRepresentation().getSeverity().equals(MAJOR.name()));
//        Assert.assertTrue(alarmCreatedEvent.getRepresentation().getType().equals(c8y_TRAPReceivedFromUnknownDevice + ipAddress));
    }

    @Test
    public void shouldsendV2Trap() throws InvocationTargetException, IllegalAccessException, InterruptedException {
        //given
        registerAndSyncGateway();

        Thread.sleep(2000);

        //when
        sendTrap(ipAddress, port, Oid, community, SnmpConstants.version2c);
        UnknownTrapOrDeviceEvent unknownTrapOrDeviceEvent = eventWatcher.waitFor(UnknownTrapOrDeviceEvent.class);

        //then
        Assert.assertTrue(unknownTrapOrDeviceEvent.getFragmentType().equals(c8y_TRAPReceivedFromUnknownDevice + ipAddress));

        //when
        AlarmCreatedEvent alarmCreatedEvent = eventWatcher.waitFor(AlarmCreatedEvent.class);

        //then
        Assertions.assertThat(alarmCreatedEvent).isNotNull();
//        Assert.assertTrue(alarmCreatedEvent.getRepresentation().getSeverity().equals(MAJOR.name()));
//        Assert.assertTrue(alarmCreatedEvent.getRepresentation().getType().equals(c8y_TRAPReceivedFromUnknownDevice + ipAddress));
    }

    @Test
    public void shouldSendV3Trap() throws InvocationTargetException, IllegalAccessException, InterruptedException {

        //given
        final ManagedObjectRepresentation deviceType = createDeviceType(Oid);
        final ManagedObjectRepresentation device = createDevice("10.23.52.51", deviceType.getId(), SnmpConstants.version3);

        //when
        GatewayAddedEvent gatewayAddedEvent = registerAndSyncGateway();

        Thread.sleep(2000);
        inventoryMockService.addChildDevice(gatewayAddedEvent.getGateway().getId(), device);
        eventWatcher.waitFor(DeviceAddedEvent.class);

        //when
        Thread.sleep(2000);
        sendV3Trap(ipAddress, port, Oid, "Myuser1", "Myuserauth1", "Myuserprv1");
        UnknownTrapOrDeviceEvent unknownTrapOrDeviceEvent = eventWatcher.waitFor(UnknownTrapOrDeviceEvent.class);

        //then
        Assert.assertTrue(unknownTrapOrDeviceEvent.getFragmentType().equals(c8y_TRAPReceivedFromUnknownDevice + ipAddress));

        //when
        AlarmCreatedEvent alarmCreatedEvent = eventWatcher.waitFor(AlarmCreatedEvent.class);

        //then
//        Assert.assertTrue(alarmCreatedEvent.getRepresentation().getSeverity().equals(MAJOR.name()));
//        Assert.assertTrue(alarmCreatedEvent.getRepresentation().getType().equals(c8y_TRAPReceivedFromUnknownDevice + ipAddress));
    }

    @Test
    public void shouldSendV3KnownTrap() throws InvocationTargetException, IllegalAccessException, InterruptedException {

        //given
        final ManagedObjectRepresentation deviceType = createDeviceType(Oid);
        final ManagedObjectRepresentation device = createDevice(ipAddress, deviceType.getId(), SnmpConstants.version3);

        //when
        GatewayAddedEvent gatewayAddedEvent = registerAndSyncGateway();

        Thread.sleep(2000);
        inventoryMockService.addChildDevice(gatewayAddedEvent.getGateway().getId(), device);
        eventWatcher.waitFor(DeviceAddedEvent.class);

        //when
        Thread.sleep(2000);
        sendV3Trap(ipAddress, port, Oid, "Myuser1", "Myuserauth1", "Myuserprv1");
        ClientDataChangedEvent clientDataChangedEvent = eventWatcher.waitFor(ClientDataChangedEvent.class);

        //then
        Assert.assertTrue(((VariableBinding) clientDataChangedEvent.getValue()).getVariable().toString().equals("123"));
    }

    @Test
    public void shouldsendV2KnownTrap() throws InvocationTargetException, IllegalAccessException, InterruptedException {
        //given
        final ManagedObjectRepresentation deviceType = createDeviceType(Oid);
        final ManagedObjectRepresentation device = createDevice(ipAddress, deviceType.getId(), SnmpConstants.version2c);

        //when
        GatewayAddedEvent gatewayAddedEvent = registerAndSyncGateway();

        Thread.sleep(2000);
        inventoryMockService.addChildDevice(gatewayAddedEvent.getGateway().getId(), device);
        eventWatcher.waitFor(DeviceAddedEvent.class);

        //when
        Thread.sleep(2000);
        sendTrap(ipAddress, port, Oid, community, SnmpConstants.version2c);
        ClientDataChangedEvent clientDataChangedEvent = eventWatcher.waitFor(ClientDataChangedEvent.class);

        //then
        Assertions.assertThat(clientDataChangedEvent).isNotNull();
//        Assert.assertTrue(((VariableBinding) clientDataChangedEvent.getValue()).getVariable().toString().equals("75"));
    }

    @Test
    public void shouldSendAutoDiscoveryOperation() throws InvocationTargetException, IllegalAccessException, InterruptedException {
        GatewayAddedEvent gatewayAddedEvent = registerAndSyncGateway();
        gatewayAddedEvent.getGateway().setIpRangeForAutoDiscovery("10.23.51.52-10.23.51.54");
        OperationRepresentation operation = new OperationRepresentation();
        operation.setDeviceId(gatewayAddedEvent.getGateway().getId());
        operation.setProperty("description","Request autodiscovery");
        HashMap<Object,Object> fragment = new HashMap<>();
        fragment.put("ipRange","10.23.52.51-10.23.52.54");
        operation.setProperty("c8y_SnmpAutoDiscovery",fragment);

        eventPublisher.publishEvent(new OperationEvent(gatewayAddedEvent.getGateway(), operation.getId()));
        Mockito.verify(operationRepository).successful(gatewayAddedEvent.getGateway(),operation.getId());
    }

    @Test
    public void shouldScheduleAutoDiscovery() throws InvocationTargetException, IllegalAccessException, InterruptedException {
        GatewayAddedEvent gatewayAddedEvent = registerAndSyncGateway();
        final ManagedObjectRepresentation result = new ManagedObjectRepresentation();
        Thread.sleep(1000);
        Map<String, Object> fragment = new HashMap();
        fragment.put("transmitRate", 0);
        fragment.put("maxFieldbusVersion", 4);
        fragment.put("autoDiscoveryInterval", 1);
        fragment.put("ipRange", "10.23.52.51-10.23.52.54");
        result.setId(gatewayAddedEvent.getGateway().getId());
        result.setProperty(Device.c8y_SNMPDevice, fragment);
        result.setProperty("c8y_SNMPGateway", fragment);
        managedObjectRepository.apply(gatewayAddedEvent.getGateway(),result);

        Thread.sleep(1000);
        OperationRepresentation operation = new OperationRepresentation();
        operation.setDeviceId(gatewayAddedEvent.getGateway().getId());
        operation.setProperty("description","Request autodiscovery");
        HashMap<Object,Object> fragment_operation = new HashMap<>();
        fragment_operation.put("ipRange","10.23.52.51-10.23.52.54");
        operation.setProperty("c8y_SnmpAutoDiscovery",fragment_operation);

        eventPublisher.publishEvent(new OperationEvent(gatewayAddedEvent.getGateway(), operation.getId()));
        Mockito.verify(operationRepository).failed(gatewayAddedEvent.getGateway(),operation.getId(),"Device scanning via auto-discovery scheduler is already in progress");
    }

    @Test
    public void shouldCreateMeasurementAfterAddingSnmpV1DeviceSuccessfully() throws Exception {
        //given
        final ManagedObjectRepresentation deviceType = getDeviceType("1.3.6.1.4.1.52032.1.1.1.0");
        final ManagedObjectRepresentation device = getV1Device(ipAddress, deviceType.getId());

        //when
        deviceControlService.registerGateway(properties.getIdentifier());
        bootstrapService.syncGateways();
        final GatewayAddedEvent event = eventWatcher.waitFor(GatewayAddedEvent.class);

        Thread.sleep(3000);

        inventoryMockService.addChildDevice(event.getGateway().getId(), device);

        DeviceAddedEvent deviceAddedEvent = eventWatcher.waitFor(DeviceAddedEvent.class);
        DeviceTypeAddedEvent deviceTypeAddedEvent = eventWatcher.waitFor(DeviceTypeAddedEvent.class);
        ClientDataChangedEvent clientDataChangedEvent = eventWatcher.waitFor(ClientDataChangedEvent.class);
        MeasurementMockService.MeasurementAdded measurementAdded =
                eventWatcher.waitFor(MeasurementMockService.MeasurementAdded.class);

        //then
        Assertions.assertThat(deviceAddedEvent).isNotNull();
        Assertions.assertThat(deviceTypeAddedEvent).isNotNull();
        Assertions.assertThat(clientDataChangedEvent).isNotNull();
        Assertions.assertThat(measurementAdded).isNotNull();
    }

    @Test
    public void shouldCreateMeasurementAfterAddingSnmpV2DeviceSuccessfully() throws Exception {
        //given
        final ManagedObjectRepresentation deviceType = getDeviceType("1.3.6.1.4.1.52032.1.1.1.0");
        final ManagedObjectRepresentation device = getV2Device(ipAddress, deviceType.getId());

        //when
        deviceControlService.registerGateway(properties.getIdentifier());
        bootstrapService.syncGateways();
        final GatewayAddedEvent event = eventWatcher.waitFor(GatewayAddedEvent.class);

        Thread.sleep(3000);

        inventoryMockService.addChildDevice(event.getGateway().getId(), device);

        DeviceAddedEvent deviceAddedEvent = eventWatcher.waitFor(DeviceAddedEvent.class);
        DeviceTypeAddedEvent deviceTypeAddedEvent = eventWatcher.waitFor(DeviceTypeAddedEvent.class);
        ClientDataChangedEvent clientDataChangedEvent = eventWatcher.waitFor(ClientDataChangedEvent.class);
        MeasurementMockService.MeasurementAdded measurementAdded =
                eventWatcher.waitFor(MeasurementMockService.MeasurementAdded.class);

        //then
        Assertions.assertThat(deviceAddedEvent).isNotNull();
        Assertions.assertThat(deviceTypeAddedEvent).isNotNull();
        Assertions.assertThat(clientDataChangedEvent).isNotNull();
        Assertions.assertThat(measurementAdded).isNotNull();
    }

    @Test
    public void shouldCreateMeasurementAfterAddingSnmpV3DeviceSuccessfully() throws Exception {
        //given
        final ManagedObjectRepresentation deviceType = getDeviceType("1.3.6.1.4.1.52032.1.1.1.0");
        final ManagedObjectRepresentation device = getV3Device(ipAddress, deviceType.getId());

        //when
        deviceControlService.registerGateway(properties.getIdentifier());
        bootstrapService.syncGateways();
        final GatewayAddedEvent event = eventWatcher.waitFor(GatewayAddedEvent.class);

        Thread.sleep(3000);

        inventoryMockService.addChildDevice(event.getGateway().getId(), device);

        DeviceAddedEvent deviceAddedEvent = eventWatcher.waitFor(DeviceAddedEvent.class);
        DeviceTypeAddedEvent deviceTypeAddedEvent = eventWatcher.waitFor(DeviceTypeAddedEvent.class);
        ClientDataChangedEvent clientDataChangedEvent = eventWatcher.waitFor(ClientDataChangedEvent.class);
        MeasurementMockService.MeasurementAdded measurementAdded =
                eventWatcher.waitFor(MeasurementMockService.MeasurementAdded.class);

        //then
        Assertions.assertThat(deviceAddedEvent).isNotNull();
        Assertions.assertThat(deviceTypeAddedEvent).isNotNull();
        Assertions.assertThat(clientDataChangedEvent).isNotNull();
        Assertions.assertThat(measurementAdded).isNotNull();
    }

    private GatewayAddedEvent registerAndSyncGateway() throws InvocationTargetException, IllegalAccessException {
        deviceControlService.registerGateway(properties.getIdentifier());
        bootstrapService.syncGateways();
        return eventWatcher.waitFor(GatewayAddedEvent.class);
    }

    private ManagedObjectRepresentation getDeviceType(String oid) {
        final List<ManagedObjectRepresentation> registerList = getReisters(oid);

        final ManagedObjectRepresentation managedObject = new ManagedObjectRepresentation();
        managedObject.setProperty("fieldbusType", "snmp");
        managedObject.setProperty("c8y_Registers", registerList);
        return inventoryMockService.store(managedObject);
    }

    private ManagedObjectRepresentation getV1Device(String ipAddress, GId deviceType) {
        final HashMap<Object, Object> fragment = new HashMap<>();
        fragment.put("ipAddress", ipAddress);
        fragment.put("port", snmpDevicePort);
        fragment.put("version", SnmpConstants.version1);
        fragment.put("auth", null);

        if (deviceType != null) {
            fragment.put("type", GID_PREFIX + deviceType.getValue());
        }

        final ManagedObjectRepresentation child = new ManagedObjectRepresentation();
        child.set(fragment, c8y_SNMPDevice);
        return inventoryMockService.store(child);
    }

    private ManagedObjectRepresentation getV2Device(String ipAddress, GId deviceType) {
        final HashMap<Object, Object> fragment = new HashMap<>();
        fragment.put("ipAddress", ipAddress);
        fragment.put("port", snmpDevicePort);
        fragment.put("version", SnmpConstants.version2c);
        fragment.put("auth", null);

        if (deviceType != null) {
            fragment.put("type", GID_PREFIX + deviceType.getValue());
        }

        final ManagedObjectRepresentation child = new ManagedObjectRepresentation();
        child.set(fragment, c8y_SNMPDevice);
        return inventoryMockService.store(child);
    }

    private ManagedObjectRepresentation getV3Device(String ipAddress, GId deviceType) {
        final HashMap<Object, Object> fragment = new HashMap<>();
        fragment.put("ipAddress", ipAddress);
        fragment.put("port", snmpDevicePort);
        fragment.put("version", SnmpConstants.version3);

        Map<String, Object> auth = new HashMap<>();
        auth.put("username", "adminUser");
        auth.put("engineId", engineId);
        auth.put("securityLevel", 3);
        auth.put("authProtocol", 1);
        auth.put("authPassword", "MD5AuthPassword");
        auth.put("privProtocol", 1);
        auth.put("privPassword", "DESPrivPassword");
        fragment.put("auth", auth);

        if (deviceType != null) {
            fragment.put("type", GID_PREFIX + deviceType.getValue());
        }

        final ManagedObjectRepresentation child = new ManagedObjectRepresentation();
        child.set(fragment, c8y_SNMPDevice);
        return inventoryMockService.store(child);
    }

    private List<ManagedObjectRepresentation> getReisters(String oid) {
        final ManagedObjectRepresentation register = new ManagedObjectRepresentation();
        register.setProperty("oid", oid);
        register.setProperty("name", "Test");
        register.setProperty("description", "Test DeviceType");

        Map<String, Object> measurementMapping = new HashMap<>();
        measurementMapping.put("series", "c8y_test");
        measurementMapping.put("type", "Test");
        register.setProperty("measurementMapping", measurementMapping);
        List<ManagedObjectRepresentation> registers = new ArrayList<>();
        registers.add(register);
        return registers;
    }
}
