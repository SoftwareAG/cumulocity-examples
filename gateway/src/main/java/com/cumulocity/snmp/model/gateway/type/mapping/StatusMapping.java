package com.cumulocity.snmp.model.gateway.type.mapping;

import com.cumulocity.snmp.model.gateway.type.Status;
import com.cumulocity.snmp.model.gateway.type.core.Mapping;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatusMapping implements Mapping {
    @JsonProperty("status")
    private Status type;
}
