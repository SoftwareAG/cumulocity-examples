package c8y.tinkerforge.bricklet;

import java.math.BigDecimal;

import com.tinkerforge.BrickletLoadCell;
import com.tinkerforge.Device;

import c8y.CurrentMeasurement;
import c8y.CurrentSensor;

public class LoadCellBricklet extends BaseSensorBricklet  {

	private static final String TYPE = "Load";
	
	private CurrentMeasurement current = new CurrentMeasurement();
	
	
	public LoadCellBricklet(String id, Device device) {
		super(id, device, TYPE, new CurrentSensor());
	}

	@Override
	public void initialize() throws Exception {

	}

	@Override
	public void run() {
		try {
			BrickletLoadCell loadCell = (BrickletLoadCell) getDevice();
			current.setCurrentValue(new BigDecimal(loadCell.getWeight()));
			super.sendMeasurement(current);
		} catch (Exception x) {
			logger.warn("Cannot read weight from bricklet", x);
		}
	}

}