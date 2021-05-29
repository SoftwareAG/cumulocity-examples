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

package com.cumulocity.agent.snmp.platform.pubsub.publisher;

import com.cumulocity.agent.snmp.platform.model.AlarmMapping;
import com.cumulocity.agent.snmp.platform.model.EventMapping;
import com.cumulocity.agent.snmp.platform.pubsub.service.EventPubSub;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EventPublisherTest {

    @Mock
    private EventPubSub eventPubSub;

    @InjectMocks
    private EventPublisher eventPublisher;

    @Before
    public void setup() {
        DateTimeUtils.setCurrentMillisFixed(System.currentTimeMillis());
    }

    @After
    public void teardown() {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void shouldPublishEvent() {
        ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(GId.asGId(1));

        EventRepresentation event = new EventRepresentation();
        event.setSource(source);
        event.setText("NEW CRITICAL EVENT");
        event.setDateTime(DateTime.now());

        eventPublisher.publish(event);

        Mockito.verify(eventPubSub).publish(event.toJSON());
    }

    @Test
    public void shouldPublishEventFromEventMapping() {
        ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(GId.asGId(1));

        EventMapping mapping = new EventMapping();
        mapping.setType(AlarmMapping.c8y_TRAPReceivedFromUnknownDevice);
        mapping.setText("EVENT TEXT");

        eventPublisher.publish(mapping.buildEventRepresentation(source));

        EventRepresentation event = new EventRepresentation();
        event.setSource(source);
        event.setDateTime(DateTime.now());
        event.setType(mapping.getType());
        event.setText(mapping.getText());

        Mockito.verify(eventPubSub).publish(event.toJSON());
    }
}