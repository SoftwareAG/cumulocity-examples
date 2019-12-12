package com.cumulocity.agent.snmp.cucumber.steps;

import com.cumulocity.agent.snmp.device.SnmpTCPDevice;
import com.cumulocity.agent.snmp.device.SnmpUDPDevice;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SnmpSimulationSteps {

    @Given("^I run snmp UDP simulation device with ip (.+) and port (.+)$")
    public void startSnmpUdpSimulation(String ipAddress, int port) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    log.info("Starting snmp UDP simulation device with endpoint: {}/{}", ipAddress, port);
                    SnmpUDPDevice.startSnmpSimulator(ipAddress, port, "cucumberSnmpSimulationDevice");
                } catch (Exception e) {
                    log.error("Cannot start snmp simulation device", e);
                }
            }
        }).start();
    }

    @Given("^I run snmp TCP simulation device with ip (.+) and port (.+)$")
    public void startSnmpTcpSimulation(String ipAddress, int port) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    log.info("Starting snmp TCP simulation device with endpoint: {}/{}", ipAddress, port);
                    SnmpTCPDevice.startSnmpSimulator(ipAddress, port, "cucumberSnmpSimulationDevice");
                } catch (Exception e) {
                    log.error("Cannot start snmp simulation device", e);
                }
            }
        }).start();
    }

    @Then("^I stop snmp UDP simulation device$")
    public void stopSnmpUDPSimulation() throws InterruptedException {
        log.info("Shutting down snmp UDP simulation device process");
        SnmpUDPDevice.stopSnmpSimulator();
    }

    @Then("^I stop snmp TCP simulation device$")
    public void stopSnmpTCPSimulation() throws InterruptedException {
        log.info("Shutting down snmp TCP simulation device process");
        SnmpTCPDevice.stopSnmpSimulator();
    }
}
