/*
 * Copyright (C) 2013 Cumulocity GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package c8y.tinkerforge.bricklet;

import java.math.BigDecimal;
import java.util.Properties;

import com.cumulocity.model.measurement.MeasurementValue;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.tinkerforge.BrickletDistanceUS;
import com.tinkerforge.Device;

import c8y.DistanceMeasurement;
import c8y.DistanceSensor;
import c8y.tinkerforge.TFIds;
import org.joda.time.DateTime;

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
	
	BrickletDistanceUS distanceBricklet = (BrickletDistanceUS)getDevice();
	
	private DateTime lastTriggered = new DateTime();
	private EventRepresentation entranceEvent = new EventRepresentation();
	
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
				DateTime currentTime = new DateTime();
				if(currentTime.getMillis() >= lastTriggered.getMillis() + actualSlackTime) {
					logger.debug("Sending distance event");
					entranceEvent.setDateTime(currentTime);
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
