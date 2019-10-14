package com.cumulocity.agent.snmp.platform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.Data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeasurementMapping {

	@NotNull
	private String type;

	@NotNull
	private String series;

	private Map<String, Map<?, ?>> staticFragmentsMap;

	public Map<String, Map<?, ?>> getStaticFragmentsMap() {
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