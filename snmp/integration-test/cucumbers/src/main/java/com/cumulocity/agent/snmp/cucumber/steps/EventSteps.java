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
