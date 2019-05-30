package com.cumulocity.snmp.integration;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.snmp.Main;
import com.cumulocity.snmp.configuration.service.GatewayConfigurationProperties;
import com.cumulocity.snmp.integration.configuration.EventWatcher;
import com.cumulocity.snmp.integration.notification.configuration.NotificationConfig;
import com.cumulocity.snmp.integration.platform.service.DeviceControlService;
import com.cumulocity.snmp.integration.platform.service.InventoryMockService;
import com.cumulocity.snmp.integration.platform.service.MeasurementMockService;
import com.cumulocity.snmp.model.type.DeviceType;
import com.cumulocity.snmp.persistance.repository.DBStore;
import com.cumulocity.snmp.repository.ManagedObjectRepository;
import com.cumulocity.snmp.repository.core.GatewayRepository;
import com.cumulocity.snmp.service.gateway.BootstrapService;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.snmp4j.*;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.cumulocity.snmp.model.gateway.device.Device.c8y_SNMPDevice;
import static com.cumulocity.snmp.utils.SimpleTypeUtils.GID_PREFIX;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Main.class, TestConfiguration.class, NotificationConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(value = "/snmp-agent-gateway.properties")
public abstract class BaseIntegrationTest {

    @Autowired
    protected InventoryMockService inventoryMockService;

    @Autowired
    protected MeasurementMockService measurementMockService;

    @Autowired
    protected ApplicationEventPublisher eventPublisher;

    @Autowired
    protected EventWatcher eventWatcher;

    @Autowired
    protected BootstrapService bootstrapService;

    @Autowired
    protected DeviceControlService deviceControlService;

    @Autowired
    protected GatewayRepository<DeviceType> deviceTypeRepository;

    @Autowired
    protected DBStore db;

    @Autowired
    protected GatewayConfigurationProperties properties;

    @Autowired
    protected ManagedObjectRepository managedObjectRepository;

    @Before
    public void setUp() {
        inventoryMockService.clear();
        measurementMockService.clear();
        eventWatcher.clear();
        db.clearAll();
    }

    @After
    public void tearDown() {
        inventoryMockService.clear();
        measurementMockService.clear();
        eventWatcher.clear();
        db.clearAll();
    }

    protected ManagedObjectRepresentation createDeviceType(String oid) {
        final ManagedObjectRepresentation register = new ManagedObjectRepresentation();
        register.setProperty("oid", oid);
        final List<ManagedObjectRepresentation> registerList = new ArrayList<>();
        registerList.add(register);

        final ManagedObjectRepresentation managedObject = new ManagedObjectRepresentation();
        managedObject.setProperty("fieldbusType", "snmp");
        managedObject.setProperty("c8y_Registers", registerList);
        return inventoryMockService.store(managedObject);
    }

    protected ManagedObjectRepresentation createDevice(String ipAddress, GId deviceType, int snmpVersion) {
        final HashMap<Object, Object> authFragment = new HashMap<>();
        authFragment.put("username", "Myuser1");
        authFragment.put("securityLevel", SecurityLevel.AUTH_PRIV);
        authFragment.put("authProtocol", 1);
        authFragment.put("authPassword", "Myuserauth1");
        authFragment.put("privProtocol", 1);
        authFragment.put("privPassword", "Myuserprv1");
        authFragment.put("engineId", "12345");

        final HashMap<Object, Object> fragment = new HashMap<>();
        fragment.put("ipAddress", ipAddress);
        fragment.put("port", 162);
        fragment.put("version", snmpVersion);
        if (snmpVersion == 3) {
            fragment.put("auth", authFragment);
        }

        if (deviceType != null) {
            fragment.put("type", GID_PREFIX + deviceType.getValue());
        }

        final ManagedObjectRepresentation child = new ManagedObjectRepresentation();
        child.set(fragment, c8y_SNMPDevice);
        return inventoryMockService.store(child);
    }

    protected void sendTrap(String ipAddress, int port, String Oid, String community, int snmpVersion) {
        try {
            TransportMapping transport = new DefaultUdpTransportMapping();
            Snmp snmp = new Snmp(transport);

            CommunityTarget cTarget = new CommunityTarget();
            cTarget.setCommunity(new OctetString(community));
            cTarget.setAddress(new UdpAddress(ipAddress + "/" + port));
            cTarget.setRetries(3);
            cTarget.setTimeout(5000);

            if (snmpVersion == SnmpConstants.version1) {
                cTarget.setVersion(SnmpConstants.version1);
                PDUv1 pdu = new PDUv1();
                pdu.setType(PDU.V1TRAP);
                pdu.add(new VariableBinding(SnmpConstants.sysUpTime, new OctetString(new Date().toString())));
                pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(Oid)));
                pdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress, new IpAddress(ipAddress)));
                pdu.add(new VariableBinding(new OID(Oid), new Integer32(75)));
                pdu.setGenericTrap(PDUv1.COLDSTART);
                snmp.send(pdu, cTarget);
            } else {
                cTarget.setVersion(SnmpConstants.version2c);
                PDU pdu = new PDU();
                pdu.add(new VariableBinding(SnmpConstants.sysUpTime, new OctetString(new Date().toString())));
                pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(Oid)));
                pdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress, new IpAddress(ipAddress)));
                pdu.add(new VariableBinding(new OID(Oid), new Integer32(75)));
                pdu.setType(PDU.NOTIFICATION);
                snmp.send(pdu, cTarget);
            }

            snmp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendV3Trap(String ipAddress, int port, String Oid, String username, String authpassphrase, String privacypassphrase) {
        try {
            TransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();

            Snmp snmp = new Snmp(transport);
            USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
            SecurityProtocols.getInstance().addPrivacyProtocol(new PrivAES192());
            SecurityProtocols.getInstance().addPrivacyProtocol(new PrivAES256());
            SecurityProtocols.getInstance().addPrivacyProtocol(new Priv3DES());
            SecurityModels.getInstance().addSecurityModel(usm);

            snmp.getUSM().addUser(new OctetString(username), new OctetString("12345"),
                    new UsmUser(new OctetString(username), AuthMD5.ID, new OctetString(authpassphrase), PrivDES.ID, new OctetString(privacypassphrase)));

            snmp.setLocalEngine(new OctetString("12345").getValue(), 0, 0);

            UserTarget target = new UserTarget();
            target.setVersion(SnmpConstants.version3);
            target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
            target.setSecurityName(new OctetString(username));
            target.setAddress(new UdpAddress(ipAddress + "/" + port));
            target.setRetries(2);
            target.setTimeout(5000);

            ScopedPDU pdu = new ScopedPDU();
            pdu.setType(ScopedPDU.NOTIFICATION);
            pdu.setRequestID(new Integer32(1234));
            pdu.add(new VariableBinding(SnmpConstants.sysUpTime));
            pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, SnmpConstants.linkDown));
            pdu.add(new VariableBinding(new OID(Oid), new Integer32(123)));

            snmp.send(pdu, target);
            snmp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
