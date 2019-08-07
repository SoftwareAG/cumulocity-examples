package com.cumulocity.snmp.model.gateway.type.mapping;

import com.cumulocity.snmp.model.gateway.type.core.Mapping;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeasurementMapping implements Mapping {
    private String type;

    private String series;

    @JsonIgnore
    private Map<String, Map> staticFragmentsMap;

    public Map<String, Map> getStaticFragmentsMap() {
        return staticFragmentsMap;
    }

    @JsonSetter("staticFragments")
    public void setStaticFragments(String[] staticFragments) {
        if (staticFragments != null && staticFragments.length > 0) {
            staticFragmentsMap = new HashMap<>(staticFragments.length);
            for (String oneStaticFragment : staticFragments) {
                staticFragmentsMap.put(oneStaticFragment, Collections.EMPTY_MAP);
            }
        }
    }
}
