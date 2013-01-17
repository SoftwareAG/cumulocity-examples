/*
 * Copyright (C) 2013 Cumulocity GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
/*
 * Copyright 2012 Nokia Siemens Networks 
 */
package com.cumulocity.agents.mps.model.measurement;

import org.svenson.AbstractDynamicProperties;
import org.svenson.JSONProperty;

/**
 * Represents electric measurement data.
 */
public class ElectricMeasurement extends AbstractDynamicProperties {

	private String type;
	
	private String phase1;
	
	private String phase2;
	
	private String phase3;
	
	private String total;
	
	private ElectricMeasurement capacitive;
	
	private ElectricMeasurement inductive;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@JSONProperty(value = "1", ignoreIfNull = true)
	public String getPhase1() {
		return phase1;
	}

	public void setPhase1(String phase1) {
		this.phase1 = phase1;
	}

	@JSONProperty(value = "2", ignoreIfNull = true)
	public String getPhase2() {
		return phase2;
	}

	public void setPhase2(String phase2) {
		this.phase2 = phase2;
	}

	@JSONProperty(value = "3", ignoreIfNull = true)
	public String getPhase3() {
		return phase3;
	}

	public void setPhase3(String phase3) {
		this.phase3 = phase3;
	}

	@JSONProperty(ignoreIfNull = true)
	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	@JSONProperty(ignoreIfNull = true)
	public ElectricMeasurement getCapacitive() {
		return capacitive;
	}

	public void setCapacitive(ElectricMeasurement capacitive) {
		this.capacitive = capacitive;
	}

	@JSONProperty(ignoreIfNull = true)
	public ElectricMeasurement getInductive() {
		return inductive;
	}

	public void setInductive(ElectricMeasurement inductive) {
		this.inductive = inductive;
	}
}
