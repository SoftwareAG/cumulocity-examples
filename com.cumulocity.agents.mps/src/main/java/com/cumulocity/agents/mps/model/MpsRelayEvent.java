package com.cumulocity.agents.mps.model;

import java.util.Date;

import com.cumulocity.model.control.Relay.RelayState;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

public class MpsRelayEvent extends EventRepresentation {
	
    private static final String MPS_RELAY_EVENT = "MpsRelayEvent";

	public MpsRelayEvent() {
	}

    public MpsRelayEvent(GId source, RelayState meterRelayStatus) {
        setType(MPS_RELAY_EVENT);
        setTime(new Date());
        ManagedObjectRepresentation sourceRepresentation = new ManagedObjectRepresentation();
        sourceRepresentation.setId(source);
        setSource(sourceRepresentation);
        setText("Relay state: " + meterRelayStatus.toString());
    }
}
