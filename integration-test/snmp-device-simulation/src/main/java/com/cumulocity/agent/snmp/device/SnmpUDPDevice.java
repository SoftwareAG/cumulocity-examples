package com.cumulocity.agent.snmp.device;

import org.snmp4j.TransportMapping;
import org.snmp4j.agent.*;
import org.snmp4j.agent.mo.*;
import org.snmp4j.agent.mo.snmp.*;
import org.snmp4j.agent.security.MutableVACM;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.TransportMappings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnmpUDPDevice extends SnmpDevice {

    private String address;

    public static void main(String[] args) throws Exception {
        int port = 1025;
        SnmpUDPDevice snmpUdpDevice = new SnmpUDPDevice("127.0.0.1/" + port, "49:U9:39:900:FJ8");
        snmpUdpDevice.startSnmpSimulator();
    }

    public SnmpUDPDevice(String address, String engineId) {
        super(address, engineId);
        this.address = address;
    }

    @Override
    protected void initTransportMappings() {
        transportMappings = new TransportMapping[1];
        Address addr = GenericAddress.parse("udp:" + address);
        TransportMapping tm = TransportMappings.getInstance().createTransportMapping(addr);
        transportMappings[0] = tm;
    }

}