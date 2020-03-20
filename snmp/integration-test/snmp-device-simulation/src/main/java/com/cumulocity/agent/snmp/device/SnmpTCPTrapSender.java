package com.cumulocity.agent.snmp.device;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.snmp4j.CommunityTarget;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.transport.DefaultTcpTransportMapping;

public class SnmpTCPTrapSender extends SnmpTrapSender {

    private TransportMapping<?> transportMapping = null;

    public static void main(String args[]) throws InterruptedException {
        SnmpTCPTrapSender tcpTrapSender = new SnmpTCPTrapSender();
        tcpTrapSender.sendSnmpV1V2Trap(SnmpConstants.version1);
        //tcpTrapSender.sendSnmpV1V2Trap(SnmpConstants.version2c);
        //tcpTrapSender.sendSnmpV3Trap();
    }

    @Override
    protected CommunityTarget getCommunityTarget(int snmpVersion, String community) {
        CommunityTarget comTarget = super.getCommunityTarget(snmpVersion, community);
        List<TransportMapping<?>> list = new ArrayList<TransportMapping<?>>();
        list.add(transportMapping);
        comTarget.setPreferredTransports(list);
        return comTarget;
    }

    @Override
    protected TransportMapping<?> getTransportMapping() throws IOException {
        if (transportMapping == null) {
            transportMapping = new DefaultTcpTransportMapping();
        }
        return transportMapping;
    }

    @Override
    protected Address getTargetAddress() {
        return GenericAddress.parse("tcp:" + ipAddress + "/" + port);
    }

}
