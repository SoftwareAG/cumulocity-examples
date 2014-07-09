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
	    return extIdToMo.get(externalID);
    }

	@Override
    public ManagedObjectRepresentation findById(GId id) {
	    return idToMo.get(id);
    }

	@Override
    public ManagedObjectRepresentation save(ManagedObjectRepresentation rep) {
		GId id = asGId(seq.getAndIncrement());
		rep.setId(id);
		idToMo.put(id, rep);
		return rep;
    }

	@Override
    public void bindToAgent(GId agentId, GId deviceId) {
		idToAgentId.put(deviceId, agentId);
    }
	
	public Collection<ManagedObjectRepresentation> getSaved() {
		return idToMo.values();
	}
}
