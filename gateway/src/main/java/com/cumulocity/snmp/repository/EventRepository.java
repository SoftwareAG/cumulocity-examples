package com.cumulocity.snmp.repository;

import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.snmp.annotation.gateway.RunWithinContext;
import com.cumulocity.snmp.model.core.Credentials;
import com.cumulocity.snmp.repository.core.PlatformRepresentationRepository;
import com.google.common.base.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static com.cumulocity.snmp.utils.PlatformRepositoryUtils.handleException;
import static com.cumulocity.snmp.utils.PlatformRepositoryUtils.handleSuccess;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventRepository implements PlatformRepresentationRepository<EventRepresentation> {

    private final EventApi eventApi;

    @RunWithinContext
    public Optional<EventRepresentation> apply(final Credentials gateway, final EventRepresentation eventRepresentation) {
        try {
            return handleSuccess( eventApi.create(eventRepresentation));
        } catch (final Exception ex) {
            return handleException(ex);
        }
    }
}
