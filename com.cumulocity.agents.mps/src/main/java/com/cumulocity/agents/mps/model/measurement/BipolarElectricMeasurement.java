/*
 * Copyright 2012 Nokia Siemens Networks 
 */
package com.cumulocity.agents.mps.model.measurement;

/**
 * Represents bipolar electric measurement data.
 */
public class BipolarElectricMeasurement {

	private ElectricMeasurement positive;
	
	private ElectricMeasurement negative;

	public ElectricMeasurement getPositive() {
		return positive;
	}

	public void setPositive(ElectricMeasurement positive) {
		this.positive = positive;
	}

	public ElectricMeasurement getNegative() {
		return negative;
	}

	public void setNegative(ElectricMeasurement negative) {
		this.negative = negative;
	}
}
