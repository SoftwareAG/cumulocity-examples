/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.agent.snmp.cucumber.config.PlatformProvider;
import com.cumulocity.agent.snmp.cucumber.tools.TaskExecutor;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;

import c8y.SNMPDevice;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OperationSteps {

    @Autowired
    private PlatformProvider platformProvider;

    @Autowired
    private GatewayRegistration gatewayRegistration;

    @Autowired
    private SnmpDeviceSteps snmpDeviceSteps;

    private OperationRepresentation lastOperation = null;

    @Given("^I create snmp auto discovery operation on gateway with ip range (.+)$")
    public void createAutoDiscoveryOperation(String ipRange) {
        OperationRepresentation operation = new OperationRepresentation();
        Map<String, String> ipRangeProperty = new HashMap<>();
        ipRangeProperty.put("ipRange", ipRange);
        operation.set("Autodiscovery request", "description");
        operation.set(ipRangeProperty, "c8y_SnmpAutoDiscovery");
        operation.setDeviceId(gatewayRegistration.getGatewayDevice().getId());
        lastOperation = deviceControlApi().create(operation);
    }

    @And("^I wait until last operation is successful on gateway with timeout ([0-9]+) seconds$")
    public void waitUntilCompletesWithTimeout(int timeout) {
        waitUntilCompletesWithTimeout(OperationStatus.SUCCESSFUL, timeout);
    }

    private void waitUntilCompletesWithTimeout(OperationStatus operationStatus, int timeout) {
        if (Objects.isNull(lastOperation)) {
            throw new AssertionError("Last operation does not exist");
        }
        if (!TaskExecutor.run(()-> {
            OperationRepresentation operation = platformProvider.getTestPlatform()
                    .getDeviceControlApi().getOperation(lastOperation.getId());
            if (!OperationStatus.PENDING.name().equalsIgnoreCase(operation.getStatus())
                    && !OperationStatus.EXECUTING.name().equalsIgnoreCase(operation.getStatus())
                    && !operation.getStatus().equalsIgnoreCase(operationStatus.name())) {
                return false;
            }
            return operation.getStatus().equalsIgnoreCase(operationStatus.name());
        }, timeout)) {
            throw new RuntimeException("Operation didn't complete in specified timeout!");
        }
        log.info("Operation completed!");
    }

    private DeviceControlApi deviceControlApi() {
        return platformProvider.getTestPlatform().getDeviceControlApi();
    }
}
