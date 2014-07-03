package com.cumulocity.agent.server.repository;

import javax.inject.Inject;

import com.cumulocity.agent.server.annotation.Named;
import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.sdk.client.identity.IdentityApi;

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

}
