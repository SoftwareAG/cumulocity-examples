package com.cumulocity.agent.snmp.platform.model;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.joda.time.DateTime;

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

	public AlarmRepresentation buildAlarmRepresentation(ManagedObjectRepresentation source) {
		AlarmRepresentation newAlarm = new AlarmRepresentation();
		newAlarm.setSource(source);
		newAlarm.setDateTime(DateTime.now());
		newAlarm.setType(this.getType());
		newAlarm.setText(this.getText());
		newAlarm.setSeverity(this.getSeverity());

		return newAlarm;
	}
}
