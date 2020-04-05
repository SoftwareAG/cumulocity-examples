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

import com.cumulocity.agent.snmp.cucumber.config.TenantProvider;
import cucumber.api.java.After;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class CleanUpSteps {

    @Autowired
    private TenantProvider tenantProvider;

    @Autowired
    private GatewayRegistration gatewayRegSteps;

    @Autowired
    private SnmpSimulationSteps snmpDeviceSteps;

    @After
    public void cleanUp() {
        log.info("Cleaning up after scenario... ");

        try {
            gatewayRegSteps.stopGatewayProcess();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }

        try {
            gatewayRegSteps.deleteGatewayConfiguration();
        } catch (Exception e) {
            log.warn(e.getMessage());
        }

        try {
            tenantProvider.deleteIntegrationTestTenant();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }

        try {
            snmpDeviceSteps.stopSnmpSimulation();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }
}
