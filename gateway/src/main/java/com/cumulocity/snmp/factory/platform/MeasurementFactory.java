package com.cumulocity.snmp.factory.platform;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.snmp.factory.gateway.core.PlatformRepresentationFactory;
import com.cumulocity.snmp.model.gateway.type.core.Register;
import com.cumulocity.snmp.model.gateway.type.mapping.MeasurementMapping;
import com.cumulocity.snmp.model.notification.platform.PlatformRepresentationEvent;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.google.common.base.Optional.of;

@Component
public class MeasurementFactory implements PlatformRepresentationFactory<MeasurementMapping, MeasurementRepresentation> {
    @Override
    public Optional<MeasurementRepresentation> apply(PlatformRepresentationEvent platformRepresentationEvent) {
        final ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(platformRepresentationEvent.getDevice().getId());

        final MeasurementRepresentation result = new MeasurementRepresentation();
        result.setSource(source);
        result.setDateTime(platformRepresentationEvent.getDate());
        storeValue(platformRepresentationEvent.getRegister(), (MeasurementMapping) platformRepresentationEvent.getMapping(), platformRepresentationEvent.getValue(), result);

        return of(result);
    }

    private void storeValue(Register register, MeasurementMapping mapping, Object value, MeasurementRepresentation result) {
        result.setType(mapping.getType());

        final Map<String, Object> series = Maps.newHashMap();

        //TODO: The value and the unit is to be defined after getting the polled data.
        series.put("value", register.convert(value));
        series.put("unit", register.getUnit());

        final Map<String, Object> type = Maps.newHashMap();
        type.put(mapping.getSeries().replace(" ", "_"), series);
        result.setProperty(mapping.getType().replace(" ", "_"), type);
    }

}
