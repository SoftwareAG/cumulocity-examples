package com.cumulocity.agent.snmp.cucumber.steps;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.agent.snmp.cucumber.config.PlatformProvider;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.sdk.client.alarm.AlarmFilter;

import cucumber.api.java.en.Then;

public class AlarmSteps {

    @Autowired
    private PlatformProvider platformProvider;

    @Autowired
    private GatewayRegistration gatewayRegistration;

    @Then("^There should be an auto discovery alarm created for ip (.+) on gateway$")
    public void findAutoDiscoveryAlarmForGateway(String ipAddress) {
        AlarmFilter alarmFilter = new AlarmFilter()
                .bySource(gatewayRegistration.getGatewayDevice().getId());
        List<AlarmRepresentation> alarms = platformProvider.getTestPlatform().getAlarmApi()
                .getAlarmsByFilter(alarmFilter).get(2000).getAlarms();
        assertThat(alarms).isNotEmpty();
        boolean matchingFound = alarms.stream()
                .anyMatch(alarm -> alarm.getText().equals("c8y_DeviceSnmpNotEnabled-" + ipAddress));
        assertThat(matchingFound).isTrue();
    }
}
