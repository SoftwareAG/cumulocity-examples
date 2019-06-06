package com.cumulocity.snmp.model.core;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.snmp.annotation.core.PersistableType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.cumulocity.snmp.utils.PlatformRepositoryUtils.handleSuccess;

@Component
@PersistableType(value = "EventUnit", runWithinContext = TenantProvider.class, inMemory = true)
public class EventUnit implements IdProvider {
    @Autowired
    @JsonIgnore
    private EventApi eventApi;
    private String key;
    private EventRepresentation representation;

    public void syncRepresentation(final EventRepresentation representation) {
        this.representation = representation;
        this.key = new HashCodeBuilder()
                .append(representation.getType())
                .append(representation.getAttrs())
                .append(representation.getDateTime())
                .append(representation.getSource())
                .build().toString();
    }

    public GId getId() {
        return GId.asGId(key);
    }

    public Optional<EventRepresentation> execute() {
        return handleSuccess(eventApi.create(representation));
    }
}
