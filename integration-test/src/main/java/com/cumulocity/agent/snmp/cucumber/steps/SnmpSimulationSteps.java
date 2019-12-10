package com.cumulocity.agent.snmp.cucumber.steps;

import java.io.IOException;

import com.cumulocity.agent.snmp.cucumber.tools.ProcessInstance;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SnmpSimulationSteps {

    private ProcessInstance snmpSimProcess = null;

    @Given("^I run snmp simulation device with udp endpoint (.+)$")
    public void startSnmpSimulation(String udpEndpoint) throws IOException {
        log.info("Starting snmp simulation with udp endpoint: {}", udpEndpoint);
        ProcessBuilder processBuilder = new ProcessBuilder("snmpsimd.py", "--data-dir=./data", "--agent-udpv4-endpoint=" + udpEndpoint);
        snmpSimProcess = new ProcessInstance();
        snmpSimProcess.start(processBuilder);
    }

    @Then("^I stop the snmp simulation device$")
    public void stopSnmpSimulation() throws InterruptedException {
        log.info("Shutting down snmp simulation process...");
        snmpSimProcess.stop();
    }
}
