/*
 * Copyright © 2012 - 2017 Cumulocity GmbH.
 * Copyright © 2017 - 2020 Software AG, Darmstadt, Germany and/or its licensors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
