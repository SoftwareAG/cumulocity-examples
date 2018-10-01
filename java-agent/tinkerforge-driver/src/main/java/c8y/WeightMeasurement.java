package c8y;

import java.math.BigDecimal;

import org.svenson.AbstractDynamicProperties;
import org.svenson.JSONProperty;

import com.cumulocity.model.measurement.MeasurementValue;

public class WeightMeasurement extends AbstractDynamicProperties {
	public static final String WEIGHT_UNIT = "g";
	
	private MeasurementValue weight = new MeasurementValue(WEIGHT_UNIT);
	
	@JSONProperty("weight")
	public MeasurementValue getWeight() {
		return weight;
	}
	
	public void setWeight(MeasurementValue weight) {
		this.weight = weight;
	}
	
	@JSONProperty(ignore = true)
	public BigDecimal getWeightValue() {
		return weight == null ? null : weight.getValue();
	}
	
	public void setWeightValue(BigDecimal weightValue) {
		weight = new MeasurementValue(WEIGHT_UNIT);
		weight.setValue(weightValue);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof WeightMeasurement))
			return false;
		
		return weight == null ? 
			((WeightMeasurement)obj).weight == null : 
				weight.equals(((WeightMeasurement)obj).weight);
	}
	
	@Override
	public int hashCode() {
		return weight==null? 0 : weight.hashCode();
	}
	
}
