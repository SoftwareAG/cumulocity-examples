package com.cumulocity.agent.snmp.cucumber.steps;

import com.cumulocity.agent.snmp.device.SnmpDevice;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SnmpSimulationSteps {

    @Given("^I run snmp simulation device with ip (.+) and port (.+)$")
    public void startSnmpSimulation(String ipAddress, int port) throws Exception {
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                try {
                    log.info("Starting snmp simulation with endpoint: {}/{}", ipAddress, port);
                    SnmpDevice.startSnmpSimulator(ipAddress, port, "cucumberSnmpSimulationDevice");
                } catch (Exception e) {
                    log.error("Cannot start snmp simulation device", e);
                }
            }
        }).start();
    }

    public void stopSnmpSimulation() throws InterruptedException {
        log.info("Shutting down snmp simulation process");
        SnmpDevice.stopSnmpSimulator();
    }
}
