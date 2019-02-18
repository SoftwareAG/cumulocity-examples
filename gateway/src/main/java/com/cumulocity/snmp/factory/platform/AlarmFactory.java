package com.cumulocity.snmp.factory.platform;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.snmp.factory.gateway.core.PlatformRepresentationFactory;
import com.cumulocity.snmp.model.gateway.type.mapping.AlarmMapping;
import com.cumulocity.snmp.model.gateway.type.mapping.AlarmSeverity;
import com.cumulocity.snmp.model.notification.platform.PlatformRepresentationEvent;
import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.google.common.base.Optional.of;

@Slf4j
@Component
public class AlarmFactory implements PlatformRepresentationFactory<AlarmMapping, AlarmRepresentation> {
    @Override
    public Optional<AlarmRepresentation> apply(PlatformRepresentationEvent platformRepresentationEvent) {

        final ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        if (platformRepresentationEvent.getDevice()== null) {
            source.setId(platformRepresentationEvent.getGateway().getId());
        } else {
            source.setId(platformRepresentationEvent.getDevice().getId());
        }

        final AlarmRepresentation result = new AlarmRepresentation();
        result.setSource(source);
        result.setDateTime(platformRepresentationEvent.getDate());
        if(platformRepresentationEvent.getMapping() instanceof AlarmMapping) {
            AlarmMapping alarmMapping = (AlarmMapping) platformRepresentationEvent.getMapping();
            result.setSeverity(AlarmSeverity.asString(alarmMapping.getSeverity()));
            result.setType(alarmMapping.getType());
            result.setText(alarmMapping.getText());
            result.setStatus(alarmMapping.getStatus());
        }

        return of(result);
    }
}
