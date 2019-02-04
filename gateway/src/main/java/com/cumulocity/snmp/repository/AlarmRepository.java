package com.cumulocity.snmp.repository;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.sdk.client.alarm.AlarmApi;
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
public class AlarmRepository implements PlatformRepresentationRepository<AlarmRepresentation> {

    private final AlarmApi alarmApi;

    @RunWithinContext
    public Optional<AlarmRepresentation> apply(final Credentials gateway, final AlarmRepresentation alarmRepresentation) {
        return create(gateway, alarmRepresentation);
    }

    @RunWithinContext
    public Optional<AlarmRepresentation> create(final Credentials gateway, final AlarmRepresentation alarmRepresentation) {
        try {
            return handleSuccess(alarmApi.create(alarmRepresentation));
        } catch (final Exception ex) {
            return handleException(ex);
        }
    }

    @RunWithinContext
    public Optional<AlarmRepresentation> update(Credentials gateway, final AlarmRepresentation alarmRepresentation) {
        try {
            return handleSuccess(alarmApi.update(alarmRepresentation));
        } catch (final Exception ex) {
            return handleException(ex);
        }
    }

    @RunWithinContext
    public Optional<AlarmRepresentation> clear(Credentials gateway, final AlarmRepresentation alarmRepresentation) {
        try {
            alarmRepresentation.setStatus("CLEARED");
            return handleSuccess(alarmApi.update(alarmRepresentation));
        } catch (final Exception ex) {
            return handleException(ex);
        }
    }
}
