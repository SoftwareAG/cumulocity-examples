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
