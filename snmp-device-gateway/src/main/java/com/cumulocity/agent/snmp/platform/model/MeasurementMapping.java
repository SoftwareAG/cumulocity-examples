package com.cumulocity.agent.snmp.platform.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.Data;

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

	@JsonSetter("type")
	public void setType(String type) {
		if (type != null) {
			this.type = type.replace(" ", "_");
		}
	}

	@JsonSetter("series")
	public void setSeries(String series) {
		if (series != null) {
			this.series = series.replace(" ", "_");
		}
	}

}