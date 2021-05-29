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

package com.cumulocity.agent.snmp.platform.model;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EventMappingTest {

    @Before
    public void setup() {
        DateTimeUtils.setCurrentMillisFixed(System.currentTimeMillis());
    }

    @After
    public void teardown() {
        DateTimeUtils.setCurrentMillisSystem();
    }


    @Test
    public void shouldBuildEventRepresentation() {
        ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(GId.asGId(1));

        EventMapping mapping = new EventMapping();
        mapping.setType("c8y_Temperature");
        mapping.setText("SOME EVENT TEXT");

        EventRepresentation event = mapping.buildEventRepresentation(source);

        assertEquals(source.getId(), event.getSource().getId());
        assertEquals(DateTime.now(), event.getDateTime());
        assertEquals(mapping.getType(), event.getType());
        assertEquals(mapping.getText(), event.getText());
    }
}