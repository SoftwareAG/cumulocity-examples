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
