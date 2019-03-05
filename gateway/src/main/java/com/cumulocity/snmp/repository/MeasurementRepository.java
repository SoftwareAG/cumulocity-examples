package com.cumulocity.snmp.repository;

import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.snmp.annotation.gateway.RunWithinContext;
import com.cumulocity.snmp.model.core.MeasurementUnit;
import com.cumulocity.snmp.model.core.Credentials;
import com.cumulocity.snmp.repository.core.PlatformRepresentationRepository;
import com.cumulocity.snmp.service.gateway.GroupMeasurementService;
import com.google.common.base.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static com.cumulocity.snmp.utils.PlatformRepositoryUtils.handleException;
import static com.cumulocity.snmp.utils.PlatformRepositoryUtils.handleSuccess;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeasurementRepository implements PlatformRepresentationRepository<MeasurementRepresentation> {

    private final GroupMeasurementService groupMeasurementService;

    @RunWithinContext
    public Optional<MeasurementRepresentation> apply(final Credentials gateway, final MeasurementRepresentation measurementRepresentation) {
        try {
            groupMeasurementService.queueForExecution(gateway, new MeasurementUnit(measurementRepresentation));
            return handleSuccess(measurementRepresentation);
        } catch (final Exception ex) {
            return handleException(ex);
        }
    }
}
