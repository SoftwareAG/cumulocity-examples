package com.cumulocity.agent.server.repository;

import static com.cumulocity.tixi.server.model.ManagedObjects.asManagedObject;

import javax.inject.Inject;
import javax.inject.Named;

import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.tixi.server.model.SerialNumber;

@Named
public class IdentityRepository {

    private final IdentityApi identityApi;

    @Inject
    public IdentityRepository(IdentityApi identityApi) {
        this.identityApi = identityApi;
    }

    public GId find(ID id) {
        return identityApi.getExternalId(id).getManagedObject().getId();
    }

    public ExternalIDRepresentation createExternalId(GId id, SerialNumber serialNumber) {
        ExternalIDRepresentation externalIDRepresentation = new ExternalIDRepresentation();
        externalIDRepresentation.setExternalId(serialNumber.getValue());
        externalIDRepresentation.setType(serialNumber.getType());
        externalIDRepresentation.setManagedObject(asManagedObject(id));
        return identityApi.create(externalIDRepresentation);
    }
}
