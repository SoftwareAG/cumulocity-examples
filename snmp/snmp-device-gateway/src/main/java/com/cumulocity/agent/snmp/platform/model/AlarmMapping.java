/*
 * Copyright © 2012 - 2017 Cumulocity GmbH.
 * Copyright © 2017 - 2020 Software AG, Darmstadt, Germany and/or its licensors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
