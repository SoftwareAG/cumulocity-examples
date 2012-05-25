/*
 * Copyright 2012 Nokia Siemens Networks 
 */
package com.cumulocity.agents.mps.model.measurement;

import java.util.Date;

import org.svenson.AbstractDynamicProperties;
import org.svenson.converter.JSONConverter;

import com.cumulocity.agents.mps.model.measurement.converter.MpsDateTypeConverter;

/**
 * Represents a single measurement result in MPS meter response.
 */
public class MpsMeasurement extends AbstractDynamicProperties {

	private Date meterDateTime;
	
	private BipolarElectricMeasurement active;
	
	private BipolarElectricMeasurement reactive;

	@JSONConverter(type = MpsDateTypeConverter.class)
	public Date getMeterDateTime() {
		return meterDateTime;
	}

	public void setMeterDateTime(Date meterDateTime) {
		this.meterDateTime = meterDateTime;
	}

	public BipolarElectricMeasurement getActive() {
		return active;
	}

	public void setActive(BipolarElectricMeasurement active) {
		this.active = active;
	}

	public BipolarElectricMeasurement getReactive() {
		return reactive;
	}

	public void setReactive(BipolarElectricMeasurement reactive) {
		this.reactive = reactive;
	}
}
