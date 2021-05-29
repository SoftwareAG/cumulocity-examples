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
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;

public class MeasurementMappingTest {

    @Before
    public void setup() {
        DateTimeUtils.setCurrentMillisFixed(System.currentTimeMillis());
    }

    @After
    public void teardown() {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void shouldSetAndGetSeries() {
        MeasurementMapping mapping = new MeasurementMapping();

        mapping.setSeries(null);
        assertNull(mapping.getSeries());

        mapping.setSeries("");
        assertEquals("", mapping.getSeries());

        mapping.setSeries("TTT");
        assertEquals("TTT", mapping.getSeries());

        mapping.setSeries(" T T T ");
        assertEquals("_T_T_T_", mapping.getSeries());
    }

    @Test
    public void shouldSetAndGetType() {
        MeasurementMapping mapping = new MeasurementMapping();

        mapping.setType(null);
        assertNull(mapping.getType());

        mapping.setType("");
        assertEquals("", mapping.getType());

        mapping.setType("TTT");
        assertEquals("TTT", mapping.getType());

        mapping.setType(" T T T ");
        assertEquals("_T_T_T_", mapping.getType());
    }

    @Test
    public void shouldSetAndGetStaticFragmentsMap() {
        MeasurementMapping mapping = new MeasurementMapping();

        mapping.setStaticFragments(null);
        assertNull(mapping.getStaticFragmentsMap());

        mapping.setStaticFragments(new String[0]);
        assertNull(mapping.getStaticFragmentsMap());

        String[] fragments = new String[] {"fragment_1", "fragment_2"};
        mapping.setStaticFragments(fragments);
        assertNotNull(mapping.getStaticFragmentsMap());
        assertEquals(Collections.EMPTY_MAP, mapping.getStaticFragmentsMap().get(fragments[0]));
        assertEquals(Collections.EMPTY_MAP, mapping.getStaticFragmentsMap().get(fragments[1]));
    }

    @Test
    public void shouldBuildMeasurementRepresentation() {
        ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(GId.asGId(1));

        MeasurementMapping mapping = new MeasurementMapping();
        mapping.setType("c8y_Temperature");
        mapping.setSeries("T");
        mapping.setStaticFragments(new String[] {"fragment_1", "fragment_2"});

        MeasurementRepresentation expectedMeasurement = new MeasurementRepresentation();
        expectedMeasurement.setSource(source);
        expectedMeasurement.setDateTime(DateTime.now());
        expectedMeasurement.setType(mapping.getType());


        Map<String, Object> seriesMap = Maps.newHashMap();
        seriesMap.put("value", 100);
        seriesMap.put("unit", "CENTI");

        Map<String, Object> typeMap = Maps.newHashMap();
        typeMap.put(mapping.getSeries(), seriesMap);
        expectedMeasurement.setProperty(mapping.getType(), typeMap);

        Map<String, Map<?, ?>> staticFragmentsMap = mapping.getStaticFragmentsMap();
        if (staticFragmentsMap != null && !staticFragmentsMap.isEmpty()) {
            expectedMeasurement.getAttrs().putAll(staticFragmentsMap);
        }

        assertEquals(expectedMeasurement.toJSON(), mapping.buildMeasurementRepresentation(source, 100, "CENTI").toJSON());
    }
}