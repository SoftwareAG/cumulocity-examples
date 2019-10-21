package com.cumulocity.agent.snmp.platform.model;

import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventMapping {

	@NotNull
	private String type;

	@NotNull
	private String text;

	public EventRepresentation buildEventRepresentation(ManagedObjectRepresentation source) {
		EventRepresentation newEvent = new EventRepresentation();
		newEvent.setSource(source);
		newEvent.setDateTime(DateTime.now());
		newEvent.setType(this.getType());
		newEvent.setText(this.getText());

		return newEvent;
	}
}
