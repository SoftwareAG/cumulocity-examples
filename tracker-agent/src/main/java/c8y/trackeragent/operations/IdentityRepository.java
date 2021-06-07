package c8y.trackeragent.operations;

import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjects;
import com.cumulocity.sdk.client.identity.IdentityApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
class IdentityRepository {
    private final IdentityApi identityApi;

    @Autowired
    public IdentityRepository(IdentityApi identityApi) {
        this.identityApi = identityApi;
    }

    public GId find(ID id) {
        return this.identityApi.getExternalId(id).getManagedObject().getId();
    }

    public ExternalIDRepresentation save(GId id, ID externalId) {
        ExternalIDRepresentation externalIDRepresentation = new ExternalIDRepresentation();
        externalIDRepresentation.setExternalId(externalId.getValue());
        externalIDRepresentation.setType(externalId.getType());
        externalIDRepresentation.setManagedObject(ManagedObjects.asManagedObject(id));
        return this.identityApi.create(externalIDRepresentation);
    }
}
