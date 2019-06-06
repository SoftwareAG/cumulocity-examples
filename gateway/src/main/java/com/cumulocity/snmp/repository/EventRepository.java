package com.cumulocity.snmp.repository;

import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.snmp.annotation.gateway.RunWithinContext;
import com.cumulocity.snmp.model.core.Credentials;
import com.cumulocity.snmp.model.core.EventUnit;
import com.cumulocity.snmp.repository.core.PlatformRepresentationRepository;
import com.google.common.base.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static com.cumulocity.snmp.utils.PlatformRepositoryUtils.handleException;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventRepository implements PlatformRepresentationRepository<EventRepresentation> {

    private final EventUnit eventUnit;
    private final com.cumulocity.snmp.repository.core.Repository<EventUnit> eventUnitRepository;

    @RunWithinContext
    public Optional<EventRepresentation> apply(final Credentials gateway, final EventRepresentation eventRepresentation) {
        try {
            eventUnit.syncRepresentation(eventRepresentation);
            return eventUnit.execute();
        } catch (final Exception ex) {
            eventUnitRepository.save(eventUnit);
            return handleException(ex);
        }
    }
}
