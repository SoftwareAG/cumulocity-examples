package com.cumulocity.agent.snmp.cucumber.steps;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.agent.snmp.cucumber.config.PlatformProvider;
import com.cumulocity.agent.snmp.cucumber.model.SnmpDeviceAuthentication;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.InventoryFilter;
import com.cumulocity.sdk.client.inventory.ManagedObject;

import c8y.SNMPDevice;
import c8y.SNMPGateway;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.cucumber.datatable.DataTable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SnmpDeviceSteps {

    @Autowired
    private PlatformProvider platformProvider;

    @Autowired
    private GatewayRegistration gatewayRegistration;

    @Autowired
    private DeviceProtocolSteps deviceProtocolSteps;

    private ManagedObjectRepresentation lastSnmpDevice = null;

    @Given("^I create a snmp device with device protocol \"(.+)\", ip \"(.+)\", port \"(.+)\" and version model Id \"(.+)\"$")
    public void createSnmpDevice(String deviceProtocolName, String ipAddress, int port, int version) {
        lastSnmpDevice = inventoryApi().create(
                snmpDeviceRepresentation(deviceProtocolName, ipAddress, port, version));
    }

    @Given("^I create a snmp device with device protocol \"(.+)\", ip \"(.+)\", port \"(.+)\", version model Id \"3\" and authentication:$")
    public void createSnmpDeviceV3(String deviceProtocolName, String ipAddress, int port, DataTable authenticationTable) {
        ManagedObjectRepresentation snmpDeviceMo = snmpDeviceRepresentation(deviceProtocolName, ipAddress, port, 3);
        SnmpDeviceAuthentication auth = new SnmpDeviceAuthentication();
        List<Map<String, String>> authenticationFields = authenticationTable.asMaps();
        auth.setUsername(authenticationFields.get(0).get("username"));
        auth.setAuthPassword(authenticationFields.get(0).get("authPassword"));
        auth.setPrivPassword(authenticationFields.get(0).get("privPassword"));
        auth.setAuthProtocol(Integer.parseInt(authenticationFields.get(0).get("authProtocol")));
        auth.setPrivProtocol(Integer.parseInt(authenticationFields.get(0).get("privProtocol")));
        auth.setSecurityLevel(Integer.parseInt(authenticationFields.get(0).get("securityLevel")));
        auth.setEngineId(authenticationFields.get(0).get("engineId"));
        snmpDeviceMo.get(SNMPDevice.class).setAuth(auth);
        lastSnmpDevice = inventoryApi().create(snmpDeviceMo);
    }

    private ManagedObjectRepresentation snmpDeviceRepresentation(String deviceProtocolName, String ipAddress, int port, int version) {
        Optional<ManagedObjectRepresentation> deviceProtocolMo = deviceProtocolSteps.getDeviceProtocolByName(deviceProtocolName);
        assertThat(deviceProtocolMo.isPresent()).isTrue();
        SNMPDevice snmpDevice = new SNMPDevice(ipAddress,
                port,
                "/inventory/managedObjects/" + deviceProtocolMo.get().getId().getValue(),
                version);
        ManagedObjectRepresentation snmpDeviceMo = new ManagedObjectRepresentation();
        snmpDeviceMo.set(snmpDevice);
        snmpDeviceMo.setType(deviceProtocolName);
        snmpDeviceMo.setName("Snmp Device");
        snmpDeviceMo.setOwner(gatewayRegistration.getGatewayOwner());
        return snmpDeviceMo;
    }

    @Given("^I add last snmp device as child device to the gateway$")
    public void addSnmpDeviceToGateway() {
        ManagedObject managedObjectApi = inventoryApi().getManagedObjectApi(gatewayRegistration.getGatewayDevice().getId());
        managedObjectApi.addChildDevice(lastSnmpDevice.getId());
    }

    @When("^I set snmp gateway configuration with ipRange (.+) and autoDiscoveryInterval (.+)$")
    public void setSnmpGatewayConfigurationForIpRange(String ipRange, int autoDiscoveryInterval) {
        setSnmpGatewayConf(ipRange, autoDiscoveryInterval, 3);
    }

    @When("^I set snmp gateway configuration with ipRange (.+), autoDiscoveryInterval (.+) and polling rate (.+)")
    public void setSnmpGatewayConf(String ipRange, int autoDiscoveryInterval, int pollingRate) {
        ManagedObjectRepresentation gatewayUpdateMo = new ManagedObjectRepresentation();
        gatewayUpdateMo.setId(gatewayRegistration.getGatewayDevice().getId());
        SNMPGateway gatewayConfig = setupGatewayConfig(0, pollingRate, autoDiscoveryInterval, ipRange);
        gatewayUpdateMo.set(gatewayConfig);
        inventoryApi().update(gatewayUpdateMo);
    }

    @Then("^There should be a snmp device with ip (.+) and port (.+) created as child on gateway$")
    public void findChildSnmpDevice(String ip, String port) {
        final ManagedObjectRepresentation snmpDevice = getSnmpDevice(ip, port);
        assertThat(snmpDevice).isNotNull();
        ManagedObjectRepresentation gatewayDevice = inventoryApi()
                .get(gatewayRegistration.getGatewayDevice().getId());
        List<ManagedObjectReferenceRepresentation> childrenDevices = gatewayDevice.getChildDevices().getReferences();
        boolean matchingFound = childrenDevices.stream()
                .anyMatch(childRef -> snmpDevice.getId().equals(childRef.getManagedObject().getId()));
        assertThat(matchingFound).isTrue();
    }

    public ManagedObjectRepresentation getLastSnmpDevice() {
        return lastSnmpDevice;
    }

    private SNMPGateway setupGatewayConfig(int transmitRate, int pollingRate, int autoDiscoveryInterval, String ipRange) {
        SNMPGateway gatewayConfig = new SNMPGateway();
        gatewayConfig.setTransmitRate(transmitRate);
        gatewayConfig.setPollingRate(pollingRate);
        gatewayConfig.setAutoDiscoveryInterval(autoDiscoveryInterval);
        gatewayConfig.setIpRange(ipRange);
        return gatewayConfig;
    }

    private ManagedObjectRepresentation getSnmpDevice(String ip, String port) {
        InventoryFilter inventoryFilter = new InventoryFilter().byFragmentType("c8y_SNMPDevice");
        Iterable<ManagedObjectRepresentation> snmpDevices = inventoryApi()
                .getManagedObjectsByFilter(inventoryFilter).get(2000).allPages();

        ManagedObjectRepresentation matchedSnmpDevice = null;
        for (ManagedObjectRepresentation snmpDevice : snmpDevices) {
            SNMPDevice snmpProperties = snmpDevice.get(SNMPDevice.class);
            if (ip.equals(snmpProperties.getIpAddress())
                    && port.equals(String.valueOf(snmpProperties.getPort()))) {
                matchedSnmpDevice = snmpDevice;
                break;
            }
        }
        return matchedSnmpDevice;
    }

    private InventoryApi inventoryApi() {
        return platformProvider.getTestPlatform().getInventoryApi();
    }
}
