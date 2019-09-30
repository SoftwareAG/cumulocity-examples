package com.cumulocity.agent.snmp.platform.model;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeasurementMapping {

	@NotNull
	private String type;

	@NotNull
	private String series;
}