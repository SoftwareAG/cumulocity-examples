package com.cumulocity.snmp.factory.platform;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.snmp.factory.gateway.core.PlatformRepresentationFactory;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.cumulocity.snmp.model.gateway.type.core.Register;
import com.cumulocity.snmp.model.gateway.type.mapping.AlarmMapping;
import com.cumulocity.snmp.model.gateway.type.mapping.AlarmSeverity;
import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

@Slf4j
@Component
public class AlarmFactory implements PlatformRepresentationFactory<AlarmMapping, AlarmRepresentation> {
    @Override
    public Optional<AlarmRepresentation> apply(DateTime date, Gateway gateway, Device device, Register register, AlarmMapping mapping, Object value) {

        final ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        if (device == null) {
            source.setId(gateway.getId());
        } else {
            source.setId(device.getId());
        }

        final AlarmRepresentation result = new AlarmRepresentation();
        result.setSource(source);
        result.setSeverity(AlarmSeverity.asString(mapping.getSeverity()));
        result.setType(mapping.getType());
        result.setText(mapping.getText());
        result.setDateTime(date);
        result.setStatus(mapping.getStatus());

        return of(result);
    }
}
