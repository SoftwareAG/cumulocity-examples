package com.cumulocity.agent.snmp.cucumber.steps;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.agent.snmp.cucumber.config.PlatformProvider;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.InventoryFilter;
import com.cumulocity.sdk.client.inventory.ManagedObject;

import c8y.SNMPDevice;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.cucumber.datatable.DataTable;

public class SnmpDeviceSteps {

    @Autowired
    private PlatformProvider platformProvider;

    @Autowired
    private GatewayRegistration gatewayRegistration;

    @Autowired
    private DeviceProtocolSteps deviceProtocolSteps;

    private ManagedObjectRepresentation lastSnmpDevice = null;

    @Given("^I create a snmp device with device protocol \"(.+)\", ip \"(.+)\", port \"(.+)\" and version \"(.+)\"$")
    public void createSnmpDevice(String deviceProtocolName, String ipAddress, int port, int version) {
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
        lastSnmpDevice = inventoryApi().create(snmpDeviceMo);
    }

    @Given("^I create a snmp device with device protocol \"(.+)\", ip \"(.+)\", port \"(.+)\" and version \"3\" and authentication:$")
    public void createSnmpDeviceV3(String deviceProtocolName, String ipAddress, int port, DataTable authentication) {
        
    }
    @Given("^I add last snmp device as child device to the gateway$")
    public void addSnmpDeviceToGateway() {
        ManagedObject managedObjectApi = inventoryApi().getManagedObjectApi(gatewayRegistration.getGatewayDevice().getId());
        managedObjectApi.addChildDevice(lastSnmpDevice.getId());
    }

    @When("^I set snmp gateway configuration with ipRange (.+) and autoDiscoveryInterval (.+)$")
    public void setSnmpGatewayConfigurationForIpRange(String ipRange, int autoDiscoveryInterval) {
        ManagedObjectRepresentation gatewayUpdateMo = new ManagedObjectRepresentation();
        gatewayUpdateMo.setId(gatewayRegistration.getGatewayDevice().getId());
        Map<String, Object> gatewayConfig = setupGatewayConfig(0, 3, autoDiscoveryInterval, ipRange);
        gatewayUpdateMo.set(gatewayConfig, "c8y_SNMPGateway");
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

    private Map<String, Object> setupGatewayConfig(int transmitRate, int pollingRate, int autoDiscoveryInterval, String ipRange) {
        Map<String, Object> gatewayConfig = new HashMap<>();
        gatewayConfig.put("transmitRate", transmitRate);
        gatewayConfig.put("pollingRate", pollingRate);
        gatewayConfig.put("autoDiscoveryInterval", autoDiscoveryInterval);
        gatewayConfig.put("ipRange", ipRange);
        return gatewayConfig;
    }

    private ManagedObjectRepresentation getSnmpDevice(String ip, String port) {
        InventoryFilter inventoryFilter = new InventoryFilter().byFragmentType("c8y_SNMPDevice");
        Iterable<ManagedObjectRepresentation> snmpDevices = inventoryApi()
                .getManagedObjectsByFilter(inventoryFilter).get(2000).allPages();

        ManagedObjectRepresentation matchedSnmpDevice = null;
        for (ManagedObjectRepresentation snmpDevice : snmpDevices) {
            Map<String, String> snmpProperties = (Map<String, String>) snmpDevice.get("c8y_SNMPDevice");
            if (snmpProperties.containsKey("ipAddress")
                    && snmpProperties.containsKey("port")) {
                if (ip.equals(snmpProperties.get("ipAddress"))
                        && port.equals(snmpProperties.get("port"))) {
                    matchedSnmpDevice = snmpDevice;
                    break;
                }
            }
        }
        return matchedSnmpDevice;
    }

    private InventoryApi inventoryApi() {
        return platformProvider.getTestPlatform().getInventoryApi();
    }
}
