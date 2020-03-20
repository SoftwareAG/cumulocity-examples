package com.cumulocity.agent.snmp.cucumber.steps;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.agent.snmp.cucumber.config.PlatformProvider;
import com.cumulocity.agent.snmp.cucumber.tools.AssertionType;
import com.cumulocity.agent.snmp.cucumber.tools.CompareUtil;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.event.EventFilter;

import cucumber.api.java.en.Then;

public class EventSteps {

    @Autowired
    protected PlatformProvider platformProvider;

    @Autowired
    protected SnmpDeviceSteps snmpDeviceSteps;

    @Then("^There should be ([0-9]+) event with type \"(.+)\" and text \"(.+)\" created for snmp device$")
    public void findEventForSnmpDevice(int count, String type, String text) {
        List<EventRepresentation> events = getEvents(type);
        events = events.stream()
                .filter(event -> {
                    return text.equals(event.getText());
                })
                .collect(Collectors.toList());
        CompareUtil.checkExistence(count, events.size(), AssertionType.EXACT);
    }

    @Then("^There should be ([0-9]+)\\-([0-9]+) event with type \"(.+)\" and text \"(.+)\" created for snmp device$")
    public void findEventForSnmpDevice(int min, int max, String type, String text) {
        List<EventRepresentation> events = getEvents(type);
        CompareUtil.checkExistence(min, events.size(), AssertionType.AT_LEAST);
        CompareUtil.checkExistence(max, events.size(), AssertionType.AT_MAX);
    }

    private List<EventRepresentation> getEvents(String type) {
        EventFilter eventFilter = new EventFilter()
                .byType(type)
                .bySource(snmpDeviceSteps.getLastSnmpDevice().getId());
        return eventApi().getEventsByFilter(eventFilter).get(2000).getEvents();
    }

    private EventApi eventApi() {
        return platformProvider.getTestPlatform().getEventApi();
    }
}
