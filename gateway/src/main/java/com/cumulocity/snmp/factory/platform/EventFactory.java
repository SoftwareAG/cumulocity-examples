package com.cumulocity.snmp.factory.platform;

import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.snmp.factory.gateway.core.PlatformRepresentationFactory;
import com.cumulocity.snmp.model.gateway.type.mapping.EventMapping;
import com.cumulocity.snmp.model.notification.platform.PlatformRepresentationEvent;
import com.google.common.base.Optional;
import org.springframework.stereotype.Component;

import static com.google.common.base.Optional.of;

@Component
public class EventFactory implements PlatformRepresentationFactory<EventMapping, EventRepresentation> {
    @Override
    public Optional<EventRepresentation> apply(PlatformRepresentationEvent platformRepresentationEvent) {
        final ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(platformRepresentationEvent.getDevice().getId());

        final EventRepresentation result = new EventRepresentation();
        result.setSource(source);
        result.setDateTime(platformRepresentationEvent.getDate());
        if(platformRepresentationEvent.getMapping() instanceof EventMapping) {
            EventMapping eventMapping = (EventMapping) platformRepresentationEvent.getMapping();
            result.setType(eventMapping.getType());
            result.setText(eventMapping.getText());
        }
        return of(result);
    }
}
