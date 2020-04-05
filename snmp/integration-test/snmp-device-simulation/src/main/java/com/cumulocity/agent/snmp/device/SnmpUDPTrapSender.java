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
