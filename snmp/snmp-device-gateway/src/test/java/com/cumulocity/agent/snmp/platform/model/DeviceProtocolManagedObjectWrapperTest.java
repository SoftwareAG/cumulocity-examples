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

package com.cumulocity.agent.snmp.platform.model;

import com.cumulocity.model.JSONBase;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import org.junit.Test;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class DeviceProtocolManagedObjectWrapperTest {

    @Test
    public void shouldCreateDeviceProtocolManagedObjectWrapperObjectSuccessfully() {
        ManagedObjectRepresentation deviceProtocolMo = JSONBase.fromJSON("{\"id\":\"900\",\"name\":\"device-protocol-1\",\"c8y_IsDeviceType\":{},\"c8y_Registers\":[{\"id\":\"906426107140115\",\"name\":\"register-1\",\"oid\":\"1.3.6.1.4.868.2.4.1.1\",\"measurementMapping\":{\"sendMeasurementTemplate\":302,\"series\":\"T\",\"type\":\"c8y_Temperature\"},\"alarmMapping\":{\"severity\":\"CRITICAL\",\"text\":\"Temperature is above 100\",\"raiseAlarmTemplate\":300,\"type\":\"c8y_HighTemperature\"},\"eventMapping\":{\"eventTemplate\":303,\"text\":\"Temperature is normal\",\"type\":\"c8y_TemperatureUpdate\"}},{\"id\":\"08395830237920188\",\"name\":\"register-2\",\"oid\":\"1.3.6.1.4.868.2.4.1.2\",\"alarmMapping\":{\"severity\":\"WARNING\",\"text\":\"Temperature is below 0\",\"raiseAlarmTemplate\":301,\"type\":\"c8y_LowTemperature\"},\"eventMapping\":{\"eventTemplate\":304,\"text\":\"Temperature is changing\",\"type\":\"c8y_TemperatureUpdate\"}}]}", ManagedObjectRepresentation.class);
        DeviceProtocolManagedObjectWrapper protocolWrapper = new DeviceProtocolManagedObjectWrapper(deviceProtocolMo);

        assertEquals("900", protocolWrapper.getId().getValue());
        assertEquals("device-protocol-1", protocolWrapper.getName());

        Map<OID, Register> oidMap = protocolWrapper.getOidMap();
        assertNotNull(oidMap);
        assertEquals(2, oidMap.size());

        // Check OID 1.3.6.1.4.868.2.4.1.1
        OID oid_1 = new OID("1.3.6.1.4.868.2.4.1.1");
        assertTrue(oidMap.containsKey(oid_1));

        Register oneRegister = oidMap.get(oid_1);
        assertEquals("1.3.6.1.4.868.2.4.1.1", oneRegister.getOid());
        assertEquals("register-1", oneRegister.getName());

        MeasurementMapping measurementMapping = oneRegister.getMeasurementMapping();
        assertNotNull(measurementMapping);
        assertEquals("T", measurementMapping.getSeries());
        assertEquals("c8y_Temperature", measurementMapping.getType());

        AlarmMapping alarmMapping = oneRegister.getAlarmMapping();
        assertNotNull(alarmMapping);
        assertEquals(AlarmSeverity.CRITICAL, AlarmSeverity.fromString(alarmMapping.getSeverity()));
        assertEquals("c8y_HighTemperature", alarmMapping.getType());
        assertEquals("Temperature is above 100", alarmMapping.getText());

        EventMapping eventMapping = oneRegister.getEventMapping();
        assertNotNull(eventMapping);
        assertEquals("c8y_TemperatureUpdate", eventMapping.getType());
        assertEquals("Temperature is normal", eventMapping.getText());

        // Check OID 1.3.6.1.4.868.2.4.1.2
        OID oid_2 = new OID("1.3.6.1.4.868.2.4.1.2");
        assertTrue(oidMap.containsKey(oid_2));

        oneRegister = oidMap.get(oid_2);
        assertEquals("1.3.6.1.4.868.2.4.1.2", oneRegister.getOid());
        assertEquals("register-2", oneRegister.getName());

        measurementMapping = oneRegister.getMeasurementMapping();
        assertNull(measurementMapping);

        alarmMapping = oneRegister.getAlarmMapping();
        assertNotNull(alarmMapping);
        assertEquals(AlarmSeverity.WARNING, AlarmSeverity.fromString(alarmMapping.getSeverity()));
        assertEquals("c8y_LowTemperature", alarmMapping.getType());
        assertEquals("Temperature is below 0", alarmMapping.getText());

        eventMapping = oneRegister.getEventMapping();
        assertNotNull(eventMapping);
        assertEquals("c8y_TemperatureUpdate", eventMapping.getType());
        assertEquals("Temperature is changing", eventMapping.getText());


        List<VariableBinding> measurementVariableBindingList = protocolWrapper.getMeasurementVariableBindingList();
        assertNotNull(measurementVariableBindingList);
        assertEquals(1, measurementVariableBindingList.size());
        assertEquals(oid_1, measurementVariableBindingList.get(0).getOid());
    }

}