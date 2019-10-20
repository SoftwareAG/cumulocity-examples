package com.cumulocity.agent.snmp.platform.pubsub.publisher;

import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import com.cumulocity.agent.snmp.platform.model.MeasurementMapping;
import com.cumulocity.agent.snmp.platform.pubsub.service.MeasurementPubSub;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.google.common.collect.Maps;

@Service
public class MeasurementPublisher extends Publisher<MeasurementPubSub> {

    public void publish(MeasurementMapping measurementMapping, ManagedObjectRepresentation source, Object value, String unit) {
        Map<String, Object> series = Maps.newHashMap();
        if(value != null) {
            series.put("value", value);
        }
        if(unit != null && !unit.isEmpty()) {
            series.put("unit", unit);
        }

        Map<String, Object> typeMap = Maps.newHashMap();
        typeMap.put(measurementMapping.getSeries(), series);

        MeasurementRepresentation newMeasurement = new MeasurementRepresentation();
        newMeasurement.setSource(source);
        newMeasurement.setDateTime(DateTime.now());
        newMeasurement.setType(measurementMapping.getType());
        newMeasurement.setProperty(measurementMapping.getType(), typeMap);

        Map<String, Map<?, ?>> staticFragmentsMap = measurementMapping.getStaticFragmentsMap();
        if (staticFragmentsMap != null && !staticFragmentsMap.isEmpty()) {
            newMeasurement.getAttrs().putAll(staticFragmentsMap);
        }

        publish(newMeasurement);
    }
}
