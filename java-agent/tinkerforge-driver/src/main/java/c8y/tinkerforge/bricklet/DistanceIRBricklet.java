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
import java.util.Date;
import java.util.Properties;

import c8y.DistanceMeasurement;
import c8y.DistanceSensor;
import c8y.tinkerforge.TFIds;

import com.cumulocity.model.measurement.MeasurementValue;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.tinkerforge.BrickletDistanceIR;
import com.tinkerforge.Device;

public class DistanceIRBricklet extends BaseSensorBricklet {

	private static final String TYPE = "DistanceIR";
	private static final String EVENT_TYPE = "c8y_EntranceEvent";
	private static final String DISTANCE_UNIT="mm";
	
	private static final String SLACK_PROP = ".eventSlacktime";
	private static final String TRESH_PROP = ".eventTreshhold";
	
	private static long DEFAULT_EVENT_SLACKTIME = 10000;
	private static short DEFAULT_EVENT_TRESHHOLD = 400;
	
	private long actualSlackTime;
	private short actualEventTreshhold;
	
	private MeasurementValue measurementValue = new MeasurementValue(DISTANCE_UNIT);
	
	BrickletDistanceIR distanceBricklet=(BrickletDistanceIR)getDevice();
	
	private Date lastTriggered=new Date();
	private EventRepresentation entranceEvent=new EventRepresentation();
	
	public DistanceIRBricklet(String id, Device device) {
		super(id, device, TYPE, new DistanceSensor());
		actualSlackTime=DEFAULT_EVENT_SLACKTIME;
		actualEventTreshhold=DEFAULT_EVENT_TRESHHOLD;
	}
	
	@Override
	public void addDefaults(Properties props) {
		props.setProperty(TFIds.getPropertyName(TYPE)+SLACK_PROP, Long.toString(DEFAULT_EVENT_SLACKTIME));
		props.setProperty(TFIds.getPropertyName(TYPE)+TRESH_PROP, Short.toString(DEFAULT_EVENT_TRESHHOLD));
		super.addDefaults(props);
	}
	
	@Override
	public void configurationChanged(Properties props) {
		actualSlackTime=Long.parseLong(props.getProperty(TFIds.getPropertyName(TYPE)+SLACK_PROP, Long.toString(DEFAULT_EVENT_SLACKTIME)));
		actualEventTreshhold=Short.parseShort(props.getProperty(TFIds.getPropertyName(TYPE)+TRESH_PROP, Short.toString(DEFAULT_EVENT_TRESHHOLD)));
		super.configurationChanged(props);
	}
	
	@Override
	public void initialize() throws Exception {
		entranceEvent.setSource(getSensorMo());
		entranceEvent.setType(EVENT_TYPE);
		entranceEvent.setText("Entrance event triggered.");
	}
	
	@Override
	public void run() {
		try {
			int distanceValue = distanceBricklet.getDistance();
			
			if(distanceValue<actualEventTreshhold){
				Date currentTime = new Date();
				if(currentTime.getTime()>=lastTriggered.getTime()+actualSlackTime) {
					logger.debug("Sending entrance event");
					entranceEvent.setTime(currentTime);
					entranceEvent.setProperty("distance", distanceValue);
					try {
						getPlatform().getEventApi().create(entranceEvent);
						lastTriggered = currentTime;
					} catch (SDKException e) {
						logger.warn("Cannot send entrance event", e);
					}
				} else logger.debug("Event not sent: slacking...");
			} else logger.debug("Event not sent: distance above treshhold...");
			
			measurementValue.setValue(new BigDecimal(distanceValue));
			super.sendMeasurement(new DistanceMeasurement(measurementValue));
		} catch(Exception x){
			logger.warn("Cannot read measurments from bricklet", x);
		}
		
	}

}
