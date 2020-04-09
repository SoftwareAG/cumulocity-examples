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

public class SnmpTCPDevice extends SnmpDevice {

    private String address;

    public static void main(String[] args) throws Exception {
        int port = 1025;
        SnmpDevice snmpTcpDevice = new SnmpTCPDevice("127.0.0.1/" + port, "49:U9:39:900:FJ8");
        snmpTcpDevice.startSnmpSimulator();
    }

    public SnmpTCPDevice(String address, String engineId) {
        super(address, engineId);
        this.address = address;
    }

    @Override
    protected void initTransportMappings() {
        transportMappings = new TransportMapping[1];
        Address addr = GenericAddress.parse("tcp:" + address);
        TransportMapping tm = TransportMappings.getInstance().createTransportMapping(addr);
        transportMappings[0] = tm;
    }

}