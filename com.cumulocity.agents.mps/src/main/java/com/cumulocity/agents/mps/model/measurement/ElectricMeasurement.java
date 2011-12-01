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
