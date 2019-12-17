package com.cumulocity.agent.snmp.cucumber.steps;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.agent.snmp.cucumber.config.PlatformProvider;
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
        AlarmFilter alarmFilter = new AlarmFilter()
                .bySource(gatewayRegistration.getGatewayDevice().getId());
        List<AlarmRepresentation> alarms = alarmApi()
                .getAlarmsByFilter(alarmFilter).get(2000).getAlarms();
        assertThat(alarms).isNotEmpty();
        boolean matchingFound = alarms.stream()
                .anyMatch(alarm -> alarm.getText().equals("c8y_DeviceSnmpNotEnabled-" + ipAddress));
        assertThat(matchingFound).isTrue();
    }

    @Then("^There should be (.+) alarm with type \"(.+)\" and text \"(.+)\" created for snmp device$")
    public void findAlarmForSnmpDevice(int count, String type, String text) {
        AlarmFilter alarmFilter = new AlarmFilter()
                .byType(type)
                .bySource(snmpDeviceSteps.getLastSnmpDevice().getId());
        List<AlarmRepresentation> alarms = alarmApi()
                .getAlarmsByFilter(alarmFilter).get(2000).getAlarms();

        assertThat(alarms.size()).isEqualTo(count);
        boolean allMatch = alarms.stream()
                .allMatch(alarm -> {
                    if (text.equals(alarm.getText())) {
                        return true;
                    }
                    return false;
                });
        assertThat(allMatch).isTrue();
    }

    private AlarmApi alarmApi() {
        return platformProvider.getTestPlatform().getAlarmApi();
    }
}
