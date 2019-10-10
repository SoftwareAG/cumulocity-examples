package com.cumulocity.agent.snmp.platform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlarmMapping {

	@NotNull
	private String type;

	@NotNull
	private String text;

	@NotNull
	private String severity;
}
