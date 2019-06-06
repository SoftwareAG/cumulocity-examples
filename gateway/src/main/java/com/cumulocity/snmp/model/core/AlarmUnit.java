package com.cumulocity.snmp.model.core;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.snmp.annotation.core.PersistableType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.cumulocity.snmp.utils.PlatformRepositoryUtils.handleSuccess;

@Component
@PersistableType(value = "AlarmUnit", runWithinContext = TenantProvider.class, inMemory = true)
public class AlarmUnit  implements IdProvider {
    @Autowired
    @JsonIgnore
    private AlarmApi alarmApi;
    private String key;
    private AlarmRepresentation representation;

    public void syncRepresentation(final AlarmRepresentation representation) {
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

    public Optional<AlarmRepresentation> execute() {
        return handleSuccess(alarmApi.create(representation));
    }
}
