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

package com.cumulocity.agent.snmp.cucumber.steps;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.agent.snmp.cucumber.config.PlatformProvider;
import com.cumulocity.agent.snmp.cucumber.tools.AssertionType;
import com.cumulocity.agent.snmp.cucumber.tools.CompareUtil;
import com.cumulocity.model.event.CumulocitySeverities;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.alarm.AlarmFilter;

import cucumber.api.java.en.Then;

public class AlarmSteps {

    @Autowired
    private GatewayRegistration gatewayRegistration;

    @Autowired
    protected PlatformProvider platformProvider;

    @Autowired
    protected SnmpDeviceSteps snmpDeviceSteps;

    @Then("^There should be an auto discovery alarm created for ip (.+) on gateway$")
    public void findAutoDiscoveryAlarmForGateway(String ipAddress) {
        List<AlarmRepresentation> alarms = getAlarms(
                gatewayRegistration.getGatewayDevice().getId(),
                "c8y_DeviceSnmpNotEnabled-" + ipAddress);
        CompareUtil.checkExistence(1, alarms.size(), AssertionType.EXACT);
        CompareUtil.checkExistence(1, alarms.get(0).getCount().intValue(), AssertionType.EXACT);
    }

    @Then("^There should be an alarm with count ([0-9]+), type \"(.+)\", text \"(.+)\" and severity \"(.+)\" created for gateway$")
    public void findAlarmForGateway(int count, String type, String text, CumulocitySeverities severity) {
        List<AlarmRepresentation> alarms = getAlarms(
                gatewayRegistration.getGatewayDevice().getId(),
                type, text, severity);
        CompareUtil.checkExistence(1, alarms.size(), AssertionType.EXACT);
        CompareUtil.checkExistence(count, alarms.get(0).getCount().intValue(), AssertionType.EXACT);
    }

    @Then("^There should be an alarm with count ([0-9]+), type \"(.+)\" and text \"(.+)\" created for snmp device$")
    public void findAlarmForSnmpDevice(int count, String type, String text) {
        List<AlarmRepresentation> alarms = getAlarms(
                snmpDeviceSteps.getLastSnmpDevice().getId(),
                type, text);
        CompareUtil.checkExistence(1, alarms.size(), AssertionType.EXACT);
        CompareUtil.checkExistence(count, alarms.get(0).getCount().intValue(), AssertionType.EXACT);
    }

    @Then("^There should be no alarm with type \"(.+)\" and text \"(.+)\" created for snmp device$")
    public void findNoAlarmForSnmpDevice(String type, String text) {
        List<AlarmRepresentation> alarms = getAlarms(
                snmpDeviceSteps.getLastSnmpDevice().getId(),
                type, text);
        CompareUtil.checkExistence(0, alarms.size(), AssertionType.EXACT);
    }

    @Then("^There should be an alarm with count ([0-9]+), type \"(.+)\", text \"(.+)\" and severity \"(.+)\" created for snmp device$")
    public void findAlarmForSnmpDevice(int count, String type, String text, CumulocitySeverities severity) {
        List<AlarmRepresentation> alarms = getAlarms(
                snmpDeviceSteps.getLastSnmpDevice().getId(),
                type, text, severity);
        CompareUtil.checkExistence(1, alarms.size(), AssertionType.EXACT);
        CompareUtil.checkExistence(count, alarms.get(0).getCount().intValue(), AssertionType.EXACT);
    }

    @Then("^There should be an alarm with count ([0-9]+)\\-([0-9]+), type \"(.+)\", text \"(.+)\" and severity \"(.+)\" created for snmp device$")
    public void findAlarmForSnmpDevice(int min, int max, String type, String text, CumulocitySeverities severity) {
        List<AlarmRepresentation> alarms = getAlarms(
                snmpDeviceSteps.getLastSnmpDevice().getId(),
                type, text, severity);
        CompareUtil.checkExistence(1, alarms.size(), AssertionType.EXACT);
        CompareUtil.checkExistence(min, alarms.get(0).getCount().intValue(), AssertionType.AT_LEAST);
        CompareUtil.checkExistence(max, alarms.get(0).getCount().intValue(), AssertionType.AT_MAX);
    }

    private List<AlarmRepresentation> getAlarms(GId source, String type, CumulocitySeverities severity) {
        AlarmFilter alarmFilter = new AlarmFilter()
                .byType(type)
                .bySource(source)
                .bySeverity(severity);
        List<AlarmRepresentation> alarms = alarmApi()
                .getAlarmsByFilter(alarmFilter).get(2000).getAlarms();
        return alarms;
    }

    private List<AlarmRepresentation> getAlarms(GId source, String type) {
        AlarmFilter alarmFilter = new AlarmFilter()
                .byType(type)
                .bySource(source);
        List<AlarmRepresentation> alarms = alarmApi()
                .getAlarmsByFilter(alarmFilter).get(2000).getAlarms();
        return alarms;
    }

    private List<AlarmRepresentation> getAlarms(GId source, String type, String text, CumulocitySeverities severity) {
        List<AlarmRepresentation> alarms = getAlarms(source, type, severity);
        alarms = alarms.stream()
                .filter(alarm -> text.equals(alarm.getText()))
                .collect(Collectors.toList());
        return alarms;
    }

    private List<AlarmRepresentation> getAlarms(GId source, String type, String text) {
        List<AlarmRepresentation> alarms = getAlarms(source, type);
        alarms = alarms.stream()
                .filter(alarm -> text.equals(alarm.getText()))
                .collect(Collectors.toList());
        return alarms;
    }

    private AlarmApi alarmApi() {
        return platformProvider.getTestPlatform().getAlarmApi();
    }
}
