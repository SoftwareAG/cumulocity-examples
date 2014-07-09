package c8y.tinkerforge.bricklet;

import java.math.BigDecimal;

import c8y.CurrentMeasurement;
import c8y.CurrentSensor;

import com.tinkerforge.BrickletCurrent12;
import com.tinkerforge.Device;

public class Current12Bricklet extends BaseSensorBricklet {
	
	private CurrentMeasurement current = new CurrentMeasurement();

	public Current12Bricklet(String id, Device device) {
		super(id, device, "Current", new CurrentSensor());
	}

	@Override
	public void initialize() throws Exception {
		// Nothing to be done.
	}

	@Override
	public void run() {
		try {
			BrickletCurrent12 currentBricklet = (BrickletCurrent12)getDevice();
			BigDecimal currentValue = new BigDecimal( 
					(double)currentBricklet.getCurrent() / 1000 );
			current.setCurrentValue(currentValue);
			super.sendMeasurement(current);
		} catch (Exception x) {
			logger.warn("Cannot read current from bricklet", x);
		}
	}

}
