package com.cumulocity.agent.server.repository;

import static com.cumulocity.tixi.server.model.ManagedObjects.asManagedObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.sdk.client.identity.IdentityApi;

@Component
public class IdentityRepository {

    private final IdentityApi identityApi;

    @Autowired
    public IdentityRepository(IdentityApi identityApi) {
        this.identityApi = identityApi;
    }

    public GId find(ID id) {
        return identityApi.getExternalId(id).getManagedObject().getId();
    }

    public ExternalIDRepresentation save(GId id, ID externalId) {
        ExternalIDRepresentation externalIDRepresentation = new ExternalIDRepresentation();
        externalIDRepresentation.setExternalId(externalId.getValue());
        externalIDRepresentation.setType(externalId.getType());
        externalIDRepresentation.setManagedObject(asManagedObject(id));
        return identityApi.create(externalIDRepresentation);
    }
}
