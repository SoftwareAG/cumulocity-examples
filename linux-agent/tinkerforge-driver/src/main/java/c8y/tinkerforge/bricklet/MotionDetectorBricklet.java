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
import com.tinkerforge.BrickletMotionDetector;

public class MotionDetectorBricklet implements Driver{
	public static final String TYPE = "Motion";
	public static final String MOTION_DETECTED_EVENT_TYPE = "c8y_MotionEvent";
	public static final String MOTION_ENDED_EVENT_TYPE = "c8y_MotionEndedEvent";
	private static final Logger logger = LoggerFactory
			.getLogger(MotionDetectorBricklet.class);
	
	private Platform platform;
	private String id;
	private BrickletMotionDetector motionSensor;
	private ManagedObjectRepresentation motionSensorMo = new ManagedObjectRepresentation();
	private EventRepresentation eventMotion = new EventRepresentation();

	public MotionDetectorBricklet(String id, BrickletMotionDetector motionSensor) {
		this.motionSensor=motionSensor;
		this.id=id;
	}
	
	@Override
	public void initialize() throws Exception {
		// Nothing to be done here.
	}

	@Override
	public void initialize(Platform platform) throws Exception {
		this.platform = platform;
	}

	@Override
	public OperationExecutor[] getSupportedOperations() {
		return new OperationExecutor[0];
	}

	@Override
	public void initializeInventory(ManagedObjectRepresentation mo) {
		// Nothing to be done here.
	}

	@Override
	public void discoverChildren(ManagedObjectRepresentation parent) {
		try{
			motionSensorMo.set(TFIds.getHardware(motionSensor, TYPE));
		} catch (Exception e){
			logger.warn("Cannot read hardware parameters",e);
		}
		
		motionSensorMo.setType(TFIds.getType(TYPE));
		motionSensorMo.setName(TFIds.getDefaultName(parent.getName(), TYPE, id));
		
		try{
			DeviceManagedObject dmo = new DeviceManagedObject(platform);
			dmo.createOrUpdate(motionSensorMo, TFIds.getXtId(id), parent.getId());

			eventMotion.setSource(motionSensorMo);
			eventMotion.setType(MOTION_DETECTED_EVENT_TYPE);
		} catch (Exception e){
			logger.warn("Cannot create motion sensor", e);
		}
	}

	@Override
	public void start() {
	motionSensor.addMotionDetectedListener(new BrickletMotionDetector.MotionDetectedListener() {
			
			@Override
			public void motionDetected() {
				logger.debug("Motion event");
				try{
					eventMotion.setTime(new Date());
					eventMotion.setText("Motion detected.");
					platform.getEventApi().create(eventMotion);
				} catch (SDKException e) {
					logger.warn("Cannot send motion event", e);
				}
				
			}
		});
		
		motionSensor.addDetectionCycleEndedListener(new BrickletMotionDetector.DetectionCycleEndedListener() {
			
			@Override
			public void detectionCycleEnded() {
			logger.debug("Motion event ended");
				try{
					eventMotion.setTime(new Date());
					eventMotion.setText("Motion ended.");
					platform.getEventApi().create(eventMotion);
				} catch (SDKException e) {
					logger.warn("Cannot send motion ended event", e);
				}
				
			}
		});
		
	}

}