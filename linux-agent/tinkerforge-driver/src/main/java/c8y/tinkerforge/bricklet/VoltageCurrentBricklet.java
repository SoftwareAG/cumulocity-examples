package c8y.tinkerforge.bricklet;

import java.math.BigDecimal;

import c8y.CurrentMeasurement;
import c8y.PowerMeasurement;
import c8y.PowerSensor;
import c8y.VoltageMeasurement;

import com.tinkerforge.BrickletVoltageCurrent;
import com.tinkerforge.Device;

public class VoltageCurrentBricklet extends BaseSensorBricklet{
	
	private VoltageMeasurement voltage = new VoltageMeasurement();
	private CurrentMeasurement current = new CurrentMeasurement();
	private PowerMeasurement power = new PowerMeasurement();

	public VoltageCurrentBricklet(String id, Device device) {
		super(id, device, "CurrentVoltage", new PowerSensor());
	}

	@Override
	public void initialize() throws Exception {
		// Nothing to be done here.
		
	}

	@Override
	public void run() {
		try {
			BrickletVoltageCurrent vcb = (BrickletVoltageCurrent) getDevice();
			voltage.setVoltageValue(new BigDecimal((double)vcb.getVoltage()/1000));
			super.sendMeasurement(voltage);
			current.setCurrentValue(new BigDecimal((double)vcb.getCurrent()/1000));
			super.sendMeasurement(current);
			power.setPowerValue(new BigDecimal((double)vcb.getPower()/1000));
			super.sendMeasurement(power);
		} catch (Exception x) {
			logger.warn("Cannot read measurments from bricklet", x);
		}
	}

}
