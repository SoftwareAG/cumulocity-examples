package com.cumulocity.agent.snmp.device;

import java.io.IOException;

import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SnmpUDPTrapSender extends SnmpTrapSender {

    public static void main(String args[]) {
        SnmpUDPTrapSender udpTrapSender = new SnmpUDPTrapSender();
        udpTrapSender.sendSnmpV1V2Trap(SnmpConstants.version1);
        //udpTrapSender.sendSnmpV1V2Trap(SnmpConstants.version2c);
        //udpTrapSender.sendSnmpV3Trap();
    }

    @Override
    protected TransportMapping<?> getTransportMapping() throws IOException {
        return new DefaultUdpTransportMapping();
    }

    @Override
    protected Address getTargetAddress() {
        return GenericAddress.parse("udp:" + ipAddress + "/" + port);
    }

}
