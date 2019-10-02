package com.cumulocity.agent.snmp.platform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventMapping {

	@NotNull
	private String type;

	@NotNull
	private String text;
}
