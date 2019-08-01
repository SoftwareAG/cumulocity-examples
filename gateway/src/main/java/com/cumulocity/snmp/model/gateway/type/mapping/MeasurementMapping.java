package com.cumulocity.snmp.model.gateway.type.mapping;

import com.cumulocity.snmp.model.gateway.type.core.Mapping;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeasurementMapping implements Mapping {
    private String type;
    private String series;
    private Map staticFragment;
}
