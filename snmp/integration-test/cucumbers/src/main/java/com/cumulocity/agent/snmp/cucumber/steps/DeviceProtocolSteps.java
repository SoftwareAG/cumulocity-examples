package com.cumulocity.agent.snmp.cucumber.steps;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.agent.snmp.cucumber.config.PlatformProvider;
import com.cumulocity.model.JSONBase;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.InventoryFilter;

import cucumber.api.java.en.Given;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeviceProtocolSteps {

    @Autowired
    private PlatformProvider platformProvider;

    @Given("^I create snmp device protocol with JSON$")
    public void createDeviceProtocol(String deviceProtocolJson) {
        ManagedObjectRepresentation deviceProtocolMo = 
                JSONBase.getJSONParser().parse(ManagedObjectRepresentation.class, deviceProtocolJson);
        deviceProtocolMo = inventoryApi().create(deviceProtocolMo);
        log.info("device protocol created: {}", deviceProtocolMo);
    }

    public Optional<ManagedObjectRepresentation> getDeviceProtocolByName(String deviceProtocolName) {
        InventoryFilter inventoryFilter = new InventoryFilter().byFragmentType("c8y_IsDeviceType");
        Iterable<ManagedObjectRepresentation> deviceProtocols = inventoryApi().getManagedObjectsByFilter(inventoryFilter).get().allPages();
        for (ManagedObjectRepresentation deviceProtocol : deviceProtocols) {
            if (deviceProtocolName.equals(deviceProtocol.getName())) {
                return Optional.of(deviceProtocol);
            }
        }
        return Optional.empty();
    }

    private InventoryApi inventoryApi() {
        return platformProvider.getTestPlatform().getInventoryApi();
    }
}
