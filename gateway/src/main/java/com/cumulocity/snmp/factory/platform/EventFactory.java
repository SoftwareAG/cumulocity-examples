package com.cumulocity.snmp.factory.platform;

import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.snmp.factory.gateway.core.PlatformRepresentationFactory;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.cumulocity.snmp.model.gateway.device.Device;
import com.cumulocity.snmp.model.gateway.type.core.Register;
import com.cumulocity.snmp.model.gateway.type.mapping.EventMapping;
import com.google.common.base.Optional;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import static com.google.common.base.Optional.of;

@Component
public class EventFactory implements PlatformRepresentationFactory<EventMapping, EventRepresentation> {
    @Override
    public Optional<EventRepresentation> apply(DateTime time, Gateway gateway, Device device, Register register, final EventMapping mapping, final Object object) {
        final ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(device.getId());

        final EventRepresentation result = new EventRepresentation();
        result.setSource(source);
        result.setType(mapping.getType());
        result.setText(mapping.getText());
        result.setDateTime(time);
        return of(result);
    }
}
