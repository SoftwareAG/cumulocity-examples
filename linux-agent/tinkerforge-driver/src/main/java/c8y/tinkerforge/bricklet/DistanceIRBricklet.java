package c8y.tinkerforge.bricklet;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.lx.driver.DeviceManagedObject;
import c8y.lx.driver.Driver;
import c8y.lx.driver.OperationExecutor;

import c8y.tinkerforge.TFIds;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;
import com.tinkerforge.BrickletDistanceIR;
import com.tinkerforge.BrickletDistanceIR.DistanceListener;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class DistanceIRBricklet implements Driver {

	public static final String TYPE = "DistanceIR";
	public static final long DIST_POLLING = 1000;
	public static final long SLACK_TIME = 10000;
	public static final String EVENT_TYPE = "c8y_EntranceEvent";

	private static final Logger logger = LoggerFactory
			.getLogger(DistanceIRBricklet.class);

	private Platform platform;
	private ManagedObjectRepresentation mo = new ManagedObjectRepresentation();
	private EventRepresentation event = new EventRepresentation();

	private String id;
	private BrickletDistanceIR distance;
	private Date lastTriggered = new Date();

	public DistanceIRBricklet(String id, BrickletDistanceIR distance) {
		this.id = id;
		this.distance = distance;
	}

	@Override
	public OperationExecutor[] getSupportedOperations() {
		return new OperationExecutor[0];
	}
	
    @Override
    public void initialize() throws Exception {
        // Nothing to do here.
    }

	@Override
	public void initialize(Platform platform) throws Exception {
		this.platform = platform;
	}

	@Override
	public void initializeInventory(ManagedObjectRepresentation mo) {
		// Nothing to do here.
	}

	@Override
	public void discoverChildren(ManagedObjectRepresentation parent) {
		try {
			mo.set(TFIds.getHardware(distance, TYPE));
		} catch (TimeoutException | NotConnectedException e) {
			logger.warn("Cannot read hardware parameters", e);
		}

		mo.setType(TFIds.getType(TYPE));
		mo.setName(TFIds.getDefaultName(parent.getName(), TYPE, id));

		try {
			DeviceManagedObject dmo = new DeviceManagedObject(platform);
			dmo.createOrUpdate(mo, TFIds.getXtId(id), parent.getId());

			event.setSource(mo);
			event.setType(EVENT_TYPE);
			event.setText("Entrance sensor triggered");
		} catch (SDKException e) {
			logger.warn("Cannot create sensor", e);
		}
	}

	@Override
	public void start() {
		distance.addDistanceListener(new DistanceListener() {
			@Override
			public void distance(int distance) {
				Date currentTime = new Date();

				logger.debug("Distance event " + distance);
				if (currentTime.getTime() >= lastTriggered.getTime()
						+ SLACK_TIME) {
					logger.debug("Sending distance event");
					event.setTime(currentTime);
					event.setProperty("Distance", distance);
					try {
						platform.getEventApi().create(event);
						lastTriggered = currentTime;
					} catch (SDKException e) {
						logger.warn("Cannot send entrance event", e);
					} 
				} else {
					logger.debug("Event not send: slacking...");
				}
			}
		});
		try {
			distance.setDistanceCallbackPeriod(DIST_POLLING);
		} catch (TimeoutException | NotConnectedException e) {
			logger.warn("Cannot start distance sensor polling", e);
		}
	}
}
