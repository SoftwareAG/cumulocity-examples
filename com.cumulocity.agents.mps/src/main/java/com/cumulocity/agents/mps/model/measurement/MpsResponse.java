/*
 * Copyright 2012 Nokia Siemens Networks 
 */
package com.cumulocity.agents.mps.model.measurement;

import java.util.List;

import org.svenson.JSONTypeHint;

/**
 * Represents MPS meter response.
 */
public class MpsResponse {

	private List<MpsMeasurement> result;

	@JSONTypeHint(MpsMeasurement.class)
	public List<MpsMeasurement> getResult() {
		return result;
	}

	public void setResult(List<MpsMeasurement> result) {
		this.result = result;
	}
}
