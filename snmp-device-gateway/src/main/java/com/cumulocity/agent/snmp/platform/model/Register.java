package com.cumulocity.agent.snmp.platform.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Register {

	private String id;

	private String oid;

	private String unit;

	private String name;

	private String parentOid;

	private String description;

	private boolean input;

	private List<String> childOids;

	private AlarmMapping alarmMapping;

	private EventMapping eventMapping;

	private MeasurementMapping measurementMapping;
}
