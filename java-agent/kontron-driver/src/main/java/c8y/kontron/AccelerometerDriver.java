package c8y.kontron;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import c8y.lx.driver.PollingDriver;

import com.cumulocity.model.event.CumulocityAlarmStatuses;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.SDKException;

public class AccelerometerDriver extends PollingDriver {

	public static final String TYPE = "Accelerometer";
	public static final String PROP_PREF = "c8y.accelerometer";

	public static final String THRESHOLD = PROP_PREF + "threshold";
	public static final double THRESHOLD_DEFAULT = 2.5;

	public static final long INTERVAL_DEFAULT = 500;

	public static final long REARM_TIME = 10000;
	public static final String MOTION_TYPE = "c8y_MotionAlarm";

	private AccelerometerReader reader;
	private AlarmRepresentation motion = new AlarmRepresentation();
	private long lastAlarm;

	public AccelerometerDriver() {
		this(new AccelerometerReader(THRESHOLD_DEFAULT));
	}

	public AccelerometerDriver(AccelerometerReader reader) {
		this(reader, 0);
	}
	
	AccelerometerDriver(AccelerometerReader reader, long lastAlarm) {
		super(TYPE, PROP_PREF, INTERVAL_DEFAULT);
		this.reader = reader;
		this.lastAlarm = lastAlarm;
	}
	
    @Override
    public void initialize() throws Exception {
        // Nothing to do here.
    }

	@Override
	public void addDefaults(Properties props) {
		super.addDefaults(props);
		props.setProperty(THRESHOLD, Double.toString(THRESHOLD_DEFAULT));
	}

	@Override
	public void configurationChanged(Properties props) {
		super.configurationChanged(props);
		String newThresholdStr = props.getProperty(THRESHOLD);
		if (newThresholdStr != null) {
			reader.setThreshold(Double.parseDouble(newThresholdStr));
		}
	}

	@Override
	public void initializeInventory(ManagedObjectRepresentation mo) {
		motion.setSource(mo);
		motion.setType(MOTION_TYPE);
		motion.setText("Motion detected");
		motion.setStatus(CumulocityAlarmStatuses.ACTIVE.toString());
	}

	@Override
	public void run() {
		try {
			if (reader.poll()) {
				long now = new Date().getTime();
				if (lastAlarm == 0 || now >= lastAlarm + REARM_TIME) {
					logger.debug("Sending motion alarm from accelerometer.");
					getPlatform().getAlarmApi().create(motion);
					lastAlarm = now;
				}
			}
		} catch (SDKException e) {
			logger.warn("Cannot create alarm on platform", e);
		} catch (IOException e) {
			logger.warn("Cannot read accelerometer", e);
		}
	}
}
