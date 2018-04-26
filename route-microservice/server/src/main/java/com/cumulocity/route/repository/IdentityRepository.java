package com.cumulocity.route.repository;

import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.google.common.base.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class IdentityRepository {
    public static final String MICROSERVICE_ID_TYPE = "c8y_Microservice";

    private final IdentityApi identityApi;

    public ExternalIDRepresentation create(GId sourceId, String type, String identifier) {
        final ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(sourceId);

        final ExternalIDRepresentation externalId = new ExternalIDRepresentation();
        externalId.setManagedObject(source);
        externalId.setExternalId(identifier);
        externalId.setType(type);
        return identityApi.create(externalId);
    }

    public Optional<GId> find(String type, String identifier) {
        try {
            final ExternalIDRepresentation externalId = identityApi.getExternalId(new ID(type, identifier));
            return Optional.of(externalId.getManagedObject().getId());
        } catch (final SDKException ex) {
            if (ex.getHttpStatus() != 404) {
                throw ex;
            }
        }
        return Optional.absent();
    }
}
