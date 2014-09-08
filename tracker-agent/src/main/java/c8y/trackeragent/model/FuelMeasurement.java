package c8y.trackeragent.model;

import java.math.BigDecimal;

import org.svenson.JSONProperty;

import com.cumulocity.model.measurement.MeasurementValue;

public class FuelMeasurement {
	
	public static final String INSTANT_UNIT = "L/100km";
	public static final String AVERAGE_UNIT = "L/100km";
	
	private MeasurementValue instantFuelConsumption = new MeasurementValue(INSTANT_UNIT);
	private MeasurementValue averageFuelConsumption = new MeasurementValue(AVERAGE_UNIT);

	
	@JSONProperty("instant")
	public MeasurementValue getT() {
		return instantFuelConsumption;
	}
	
	public void setT(MeasurementValue t) {
		this.instantFuelConsumption = t;
	}

	@JSONProperty("average")
	public MeasurementValue getA() {
		return averageFuelConsumption;
	}
	
	public void setA(MeasurementValue t) {
		this.averageFuelConsumption = t;
	}
	
	@JSONProperty(ignore = true)
	public BigDecimal getInstantFuelConsumption() {
		return instantFuelConsumption.getValue();
	}

	public void setInstantFuelConsumption(BigDecimal fuelConsumption) {
		instantFuelConsumption = new MeasurementValue(INSTANT_UNIT);
		instantFuelConsumption.setValue(fuelConsumption);
	}
	
	@JSONProperty(ignore = true)
	public BigDecimal getAverageFuelConsumption() {
		return averageFuelConsumption.getValue();
	}

	public void setAverageFuelConsumption(BigDecimal fuelConsumption) {
		averageFuelConsumption = new MeasurementValue (AVERAGE_UNIT);
		averageFuelConsumption.setValue(fuelConsumption);
	}
	
	
	
	
}
