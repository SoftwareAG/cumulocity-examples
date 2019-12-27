package com.cumulocity.agent.snmp.cucumber.steps;

import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Counter32;
import org.snmp4j.smi.Counter64;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.Variable;

import com.cumulocity.agent.snmp.device.SnmpDevice;
import com.cumulocity.agent.snmp.device.SnmpTCPDevice;
import com.cumulocity.agent.snmp.device.SnmpUDPDevice;
import com.cumulocity.agent.snmp.device.SnmpTCPTrapSender;
import com.cumulocity.agent.snmp.device.SnmpTrapSender;
import com.cumulocity.agent.snmp.device.SnmpUDPTrapSender;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SnmpSimulationSteps {

    @Given("^I run snmp UDP simulation device with ip (.+) and port ([0-9]+)$")
    public void startSnmpUdpSimulation(String ipAddress, int port) throws Exception {
        SnmpDevice snmpUdpDevice = new SnmpUDPDevice(ipAddress, "49:U9:39:900:FJ8");
        log.info("Starting snmp UDP simulation device with endpoint: {}/{}", ipAddress, port);
        runSnmpDeviceInNewThread(snmpUdpDevice, port);
    }

    @Given("^I run snmp TCP simulation device with ip (.+) and port ([0-9]+)$")
    public void startSnmpTcpSimulation(String ipAddress, int port) throws Exception {
        SnmpDevice snmpTcpDevice = new SnmpTCPDevice(ipAddress, "49:U9:39:900:FJ8");
        log.info("Starting snmp TCP simulation device with endpoint: {}/{}", ipAddress, port);
        runSnmpDeviceInNewThread(snmpTcpDevice, port);
    }

    @Given("^I run snmp UDP simulation device with ip (.+) and port ([0-9]+) that has variable (.+) with value (.+) and OId (.+)$")
    public void startSnmpUdpSimulation(String ipAddress, int port, String variable, String valueStr, String oId) {
        Variable smiVariable = getVariableObject(variable, valueStr);
        if (smiVariable != null) {
            SnmpDevice snmpUdpDevice = new SnmpUDPDevice(ipAddress, "49:U9:39:900:FJ8");
            log.info("Starting snmp UDP simulation device with endpoint: {}/{}", ipAddress, port);
            snmpUdpDevice.setVariable(smiVariable, oId);
            runSnmpDeviceInNewThread(snmpUdpDevice, port);
        }
    }

    @Given("^I run snmp TCP simulation device with ip (.+) and port ([0-9]+) that has variable (.+) with value (.+) and OId (.+)$")
    public void startSnmpTcpSimulation(String ipAddress, int port, String variable, String valueStr, String oId) {
        Variable smiVariable = getVariableObject(variable, valueStr);
        if (smiVariable != null) {
            SnmpDevice snmpTcpDevice = new SnmpTCPDevice(ipAddress, "49:U9:39:900:FJ8");
            log.info("Starting snmp TCP simulation device with endpoint: {}/{}", ipAddress, port);
            snmpTcpDevice.setVariable(smiVariable, oId);
            runSnmpDeviceInNewThread(snmpTcpDevice, port);
        }
    }

    private void runSnmpDeviceInNewThread(SnmpDevice snmpDevice, int port) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    snmpDevice.startSnmpSimulator(snmpDevice.getAddress(), port, snmpDevice.getEngineId());
                } catch (Exception e) {
                    log.error("Cannot start snmp simulation device", e);
                }
            }
        }).start();
    }

    @Given("^I send UDP trap message with trap version (.+) and OId (.+)$")
    public void sendUdpTrapMessage(String version, String trapOid) {
        SnmpTrapSender udpTrapSender = new SnmpUDPTrapSender();
        udpTrapSender.setTrapOid(trapOid);
        sendTrap(udpTrapSender, version);
    }

    @Given("^I send TCP trap message with trap version (.+) and OId (.+)$")
    public void sendTcpTrapMessage(String version, String trapOid) {
        SnmpTrapSender tcpTrapSender = new SnmpTCPTrapSender();
        tcpTrapSender.setTrapOid(trapOid);
        sendTrap(tcpTrapSender, version);
    }

    @Given("^I send UDP trap message with trap version (.+), OId (.+), variable (.+) and value (.+)$")
    public void sendUdpTrapMessage(String version, String trapOid, String variable, String valueStr) {
        Variable smiVariable = getVariableObject(variable, valueStr);
        if (smiVariable != null) {
            SnmpTrapSender udpTrapSender = new SnmpUDPTrapSender();
            udpTrapSender.setTrapOid(trapOid);
            udpTrapSender.setVariable(smiVariable);
            sendTrap(udpTrapSender, version);
        }
    }

    @Given("^I send TCP trap message with trap version (.+), OId (.+), variable (.+) and value (.+)$")
    public void sendTcpTrapMessage(String version, String trapOid, String variable, String valueStr) {
        Variable smiVariable = getVariableObject(variable, valueStr);
        if (smiVariable != null) {
            SnmpTrapSender udpTrapSender = new SnmpUDPTrapSender();
            udpTrapSender.setTrapOid(trapOid);
            udpTrapSender.setVariable(smiVariable);
            sendTrap(udpTrapSender, version);
        }
    }

    private void sendTrap(SnmpTrapSender trapSender, String version) {
        switch (version) {
            case "1":
                trapSender.sendSnmpV1V2Trap(SnmpConstants.version1);
                break;
            case "2c":
                trapSender.sendSnmpV1V2Trap(SnmpConstants.version2c);
                break;
            case "3":
                trapSender.sendSnmpV3Trap();
                break;
            default:
                log.warn("Unsupported snmp version in cucumber tests.");
                break;
        }
    }

    private Variable getVariableObject(String variable, String valueStr) {
        Variable smiVariable = null;
        switch (variable) {
            case "Integer32":
                smiVariable = new Integer32(Integer.valueOf(valueStr));
                break;
            case "OctetString":
                smiVariable = new OctetString(valueStr);
                break;
            case "Counter32":
                smiVariable = new Counter32(Long.valueOf(valueStr));
                break;
            case "Counter64":
                smiVariable = new Counter64(Long.valueOf(valueStr));
                break;
            case "TimeTicks":
                smiVariable = new TimeTicks(Long.valueOf(valueStr));
                break;
            default:
                log.warn("Unsupported smi variable type in cucumber tests.");
                break;
        }
        return smiVariable;
    }

    @Then("^I stop snmp simulation device$")
    public void stopSnmpSimulation() throws InterruptedException {
        SnmpDevice.stopSnmpSimulator();
    }
}
