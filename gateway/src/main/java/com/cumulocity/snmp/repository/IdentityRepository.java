package com.cumulocity.snmp.repository;

import com.cumulocity.model.ID;
import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.snmp.annotation.gateway.RunWithinContext;
import com.cumulocity.snmp.model.gateway.Gateway;
import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static com.cumulocity.snmp.utils.PlatformRepositoryUtils.handleException;
import static com.cumulocity.snmp.utils.PlatformRepositoryUtils.handleSuccess;

@Repository
public class IdentityRepository {

    @Autowired
    private IdentityApi identity;

    @RunWithinContext
    public Optional<ExternalIDRepresentation> save(final Gateway gateway, final ExternalIDRepresentation id) {
        try {
            return handleSuccess(identity.create(id));
        } catch (final Exception ex) {
            return handleException(ex);
        }
    }

    @RunWithinContext
    public Optional<ExternalIDRepresentation> get(final Gateway gateway, final ID identifier) {
        try {
            return handleSuccess(identity.getExternalId(identifier));
        } catch (final SDKException ex) {
            return handleException(ex);
        }
    }

    @RunWithinContext
    public Optional<ExternalIDRepresentation> get(final DeviceCredentialsRepresentation gateway, final ID identifier) {
        try {
            return handleSuccess(identity.getExternalId(identifier));
        } catch (final SDKException ex) {
            return handleException(ex);
        }
    }
}
