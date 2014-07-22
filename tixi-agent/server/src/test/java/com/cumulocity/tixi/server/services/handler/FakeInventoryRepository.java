package com.cumulocity.tixi.server.services.handler;

import static com.cumulocity.model.idtype.GId.asGId;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.tixi.server.model.SerialNumber;

public class FakeInventoryRepository extends InventoryRepository {

    private AtomicInteger seq = new AtomicInteger();

    private Map<ID, ManagedObjectRepresentation> idToMo = new HashMap<>();

    private Map<ID, ManagedObjectRepresentation> extIdToMo = new HashMap<>();

    private Map<ID, ID> idToAgentId = new HashMap<>();

    public FakeInventoryRepository() {
        super(null, null);
    }

    @Override
    public ManagedObjectRepresentation findByExternalId(ID externalID) {
        return checkNotNull(extIdToMo.get(externalID));
    }

    @Override
    public ManagedObjectRepresentation findById(GId id) {
        return checkNotNull(idToMo.get(id));
    }

    @Override
    public ManagedObjectRepresentation save(ManagedObjectRepresentation rep) {
        if (rep.getId() != null) {
            idToMo.put(rep.getId(), rep);
        } else {
            GId id = asGId(seq.getAndIncrement());
            rep.setId(id);
            idToMo.put(id, rep);
        }
        return rep;
    }

    @Override
    public void bindToParent(GId agentId, GId deviceId) {
        idToAgentId.put(deviceId, agentId);
    }

    @Override
    public ManagedObjectRepresentation save(ManagedObjectRepresentation rep, ID... serial) {
        rep = save(rep);
        extIdToMo.put(serial[0], rep);
        return rep;
    }

    private static <K> K checkNotNull(K obj) {
        if (obj == null) {
            throw new SDKException("NO!");
        } else {
            return obj;
        }
    }

    public Collection<ManagedObjectRepresentation> getAllManagedObjects() {
        return idToMo.values();
    }

    public Collection<ID> getAllExternalIds() {
        return extIdToMo.keySet();
    }
}
