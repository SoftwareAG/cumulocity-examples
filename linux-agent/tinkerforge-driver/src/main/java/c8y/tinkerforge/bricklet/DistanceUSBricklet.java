package c8y.tinkerforge.bricklet;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Properties;

import com.cumulocity.model.measurement.MeasurementValue;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.tinkerforge.BrickletDistanceUS;
import com.tinkerforge.Device;

import c8y.DistanceMeasurement;
import c8y.DistanceSensor;
import c8y.tinkerforge.TFIds;

public class DistanceUSBricklet extends BaseSensorBricklet{

	private static final String TYPE = "DistanceUS";
	private static final String EVENT_TYPE = "c8y_DistanceEvent";
	private static final String DISTANCE_UNIT="%";
	
	private static final String SLACK_PROP = ".eventSlackTime";
	private static final String TRESHOLD_PROP = ".eventTreshold";
	
	private static long DEFAULT_EVENT_SLACKTIME = 10000;
	private static double DEFAULT_EVENT_TRESHOLD = 25;
	
	private long actualSlackTime;
	private double actualEventTreshold;
	
	private MeasurementValue measurementValue = new MeasurementValue(DISTANCE_UNIT);
	
	BrickletDistanceUS distanceBricklet=(BrickletDistanceUS)getDevice();
	
	private Date lastTriggered=new Date();
	private EventRepresentation entranceEvent=new EventRepresentation();
	
	public DistanceUSBricklet(String id, Device device) {
		super(id, device, TYPE, new DistanceSensor());
		actualSlackTime=DEFAULT_EVENT_SLACKTIME;
		actualEventTreshold=DEFAULT_EVENT_TRESHOLD;
	}
	
	@Override
	public void addDefaults(Properties props) {
		props.setProperty(TFIds.getPropertyName(TYPE)+SLACK_PROP, Long.toString(DEFAULT_EVENT_SLACKTIME));
		props.setProperty(TFIds.getPropertyName(TYPE)+TRESHOLD_PROP, Double.toString(DEFAULT_EVENT_TRESHOLD));
		super.addDefaults(props);
	}
	
	@Override
	public void configurationChanged(Properties props) {
		actualSlackTime=Long.parseLong(props.getProperty(TFIds.getPropertyName(TYPE)+SLACK_PROP, Long.toString(DEFAULT_EVENT_SLACKTIME)));
		actualEventTreshold=Double.parseDouble(props.getProperty(TFIds.getPropertyName(TYPE)+TRESHOLD_PROP, Double.toString(DEFAULT_EVENT_TRESHOLD)));
		super.configurationChanged(props);
	}
	
	@Override
	public void initialize() throws Exception {
		entranceEvent.setSource(getSensorMo());
		entranceEvent.setType(EVENT_TYPE);
		entranceEvent.setText("Distance event triggered.");
	}
	
	@Override
	public void run() {
		try {
			double distancePercentage = (double)100*distanceBricklet.getDistanceValue()/4095;
			
			if(distancePercentage<actualEventTreshold){
				Date currentTime = new Date();
				if(currentTime.getTime()>=lastTriggered.getTime()+actualSlackTime) {
					logger.debug("Sending distance event");
					entranceEvent.setTime(currentTime);
					entranceEvent.setProperty("distance", distancePercentage);
					try {
						getPlatform().getEventApi().create(entranceEvent);
						lastTriggered = currentTime;
					} catch (SDKException e) {
						logger.warn("Cannot send distance event", e);
					}
				} else logger.debug("Event not sent: slacking...");
			} else logger.debug("Event not sent: distance above treshhold...");
			
			measurementValue.setValue(new BigDecimal(distancePercentage));
			super.sendMeasurement(new DistanceMeasurement(measurementValue));
		} catch(Exception x){
			logger.warn("Cannot read measurments from bricklet", x);
		}
		
	}

}
