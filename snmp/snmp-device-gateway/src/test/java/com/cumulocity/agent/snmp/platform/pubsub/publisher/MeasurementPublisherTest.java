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

package com.cumulocity.agent.snmp.platform.pubsub.publisher;

import com.cumulocity.agent.snmp.platform.model.MeasurementMapping;
import com.cumulocity.agent.snmp.platform.pubsub.service.MeasurementPubSub;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.google.common.collect.Maps;
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

import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class MeasurementPublisherTest {

    @Mock
    private MeasurementPubSub measurementPubSub;

    @InjectMocks
    private MeasurementPublisher measurementPublisher;

    @Before
    public void setup() {
        DateTimeUtils.setCurrentMillisFixed(System.currentTimeMillis());
    }

    @After
    public void teardown() {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void shouldPublishMeasurement() {
        MeasurementRepresentation measurement = new MeasurementRepresentation();
        ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(GId.asGId(1));

        measurement.setSource(source);
        measurement.setType("c8y_TemperatureMeasurement");
        measurement.setDateTime(DateTime.now());

        measurementPublisher.publish(measurement);

        Mockito.verify(measurementPubSub).publish(measurement.toJSON());
    }

    @Test
    public void shouldPublishMeasurementFromMeasurementMapping() {
        ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(GId.asGId(1));

        MeasurementMapping mapping = new MeasurementMapping();
        mapping.setType("c8y_Temperature");
        mapping.setSeries("T");

        mapping.setStaticFragments(new String[] {"fragment_1", "fragment_2"});

        measurementPublisher.publish(mapping.buildMeasurementRepresentation(source, 100, "CENTI"));


        MeasurementRepresentation measurement = new MeasurementRepresentation();
        measurement.setSource(source);
        measurement.setDateTime(DateTime.now());
        measurement.setType(mapping.getType());


        Map<String, Object> seriesMap = Maps.newHashMap();
        seriesMap.put("value", 100);
        seriesMap.put("unit", "CENTI");

        Map<String, Object> typeMap = Maps.newHashMap();
        typeMap.put(mapping.getSeries(), seriesMap);
        measurement.setProperty(mapping.getType(), typeMap);

        Map<String, Map<?, ?>> staticFragmentsMap = mapping.getStaticFragmentsMap();
        if (staticFragmentsMap != null && !staticFragmentsMap.isEmpty()) {
            measurement.getAttrs().putAll(staticFragmentsMap);
        }

        Mockito.verify(measurementPubSub).publish(measurement.toJSON());
    }
}