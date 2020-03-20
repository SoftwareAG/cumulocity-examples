package com.cumulocity.agent.snmp.platform.model;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.collect.Maps;
import lombok.Data;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

	public MeasurementRepresentation buildMeasurementRepresentation(ManagedObjectRepresentation source, Object value, String unit) {
		Map<String, Object> series = Maps.newHashMap();
		if(value != null) {
			series.put("value", value);
		}
		if(unit != null && !unit.isEmpty()) {
			series.put("unit", unit);
		}

		Map<String, Object> typeMap = Maps.newHashMap();
		typeMap.put(this.getSeries(), series);

		MeasurementRepresentation newMeasurement = new MeasurementRepresentation();
		newMeasurement.setSource(source);
		newMeasurement.setDateTime(DateTime.now());
		newMeasurement.setType(this.getType());
		newMeasurement.setProperty(this.getType(), typeMap);

		Map<String, Map<?, ?>> staticFragmentsMap = this.getStaticFragmentsMap();
		if (staticFragmentsMap != null && !staticFragmentsMap.isEmpty()) {
			newMeasurement.getAttrs().putAll(staticFragmentsMap);
		}

		return newMeasurement;
	}
}