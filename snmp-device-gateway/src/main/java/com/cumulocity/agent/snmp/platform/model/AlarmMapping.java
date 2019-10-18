package com.cumulocity.agent.snmp.platform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlarmMapping {
	public static final String c8y_ValidationError = "c8y_ValidationError-";
	public static final String c8y_TRAPReceivedFromUnknownDevice = "c8y_TRAPReceivedFromUnknownDevice-";
	public static final String c8y_DeviceNotResponding = "c8y_DeviceNotResponding-";
	public static final String c8y_DeviceSnmpNotEnabled = "c8y_DeviceSnmpNotEnabled-";

	@NotNull
	private String type;

	@NotNull
	private String text;

	@NotNull
	private String severity;
}
