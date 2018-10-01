package c8y.tinkerforge.bricklet;

import java.math.BigDecimal;

import com.tinkerforge.BrickletLoadCell;
import com.tinkerforge.Device;

import c8y.WeightMeasurement;
import c8y.WeightSensor;

public class LoadCellBricklet extends BaseSensorBricklet  {

	private static final String TYPE = "Load";
	
	private WeightMeasurement weight = new WeightMeasurement();
	
	
	public LoadCellBricklet(String id, Device device) {
		super(id, device, TYPE, new WeightSensor());
	}

	@Override
	public void initialize() throws Exception {

	}

	@Override
	public void run() {
		try {
			BrickletLoadCell loadCell = (BrickletLoadCell) getDevice();
			weight.setWeightValue(new BigDecimal(loadCell.getWeight()));
			super.sendMeasurement(weight);
		} catch (Exception x) {
			logger.warn("Cannot read weight from bricklet", x);
		}
	}

}