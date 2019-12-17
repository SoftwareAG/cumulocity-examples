package com.cumulocity.agent.snmp.cucumber.steps;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.agent.snmp.cucumber.config.PlatformProvider;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.event.EventFilter;

import cucumber.api.java.en.Then;

public class EventSteps {

    @Autowired
    protected PlatformProvider platformProvider;

    @Autowired
    protected SnmpDeviceSteps snmpDeviceSteps;

    @Then("^There should be (.+) event with type \"(.+)\" and text \"(.+)\" created for snmp device$")
    public void findEventForSnmpDevice(int count, String type, String text) {
        EventFilter eventFilter = new EventFilter()
                .byType(type)
                .bySource(snmpDeviceSteps.getLastSnmpDevice().getId());
        List<EventRepresentation> events = eventApi().getEventsByFilter(eventFilter).get(2000).getEvents();
        assertThat(events.size()).isEqualTo(count);
        boolean allMatch = events.stream()
                .allMatch(event -> {
                    if (text.equals(event.getText())) {
                        return true;
                    }
                    return false;
                });
        assertThat(allMatch).isTrue();
    }

    private EventApi eventApi() {
        return platformProvider.getTestPlatform().getEventApi();
    }
}
