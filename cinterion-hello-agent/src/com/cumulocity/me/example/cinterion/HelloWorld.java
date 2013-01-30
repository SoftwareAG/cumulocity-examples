package com.cumulocity.me.example.cinterion;

import java.util.Date;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import com.cinterion.io.ATCommand;
import com.cumulocity.me.model.idtype.GId;
import com.cumulocity.me.rest.representation.inventory.ManagedObjectReferenceRepresentation;
import com.cumulocity.me.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.me.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.me.rest.representation.operation.OperationRepresentation;
import com.cumulocity.me.sdk.client.Platform;
import com.cumulocity.me.sdk.client.PlatformImpl;
import com.cumulocity.me.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.me.sdk.client.inventory.InventoryApi;
import com.cumulocity.me.sdk.client.inventory.ManagedObject;

public class HelloWorld extends MIDlet {

	private ATCommand ATC;
	private Platform platform;
	private ManagedObjectRepresentation mo;
	private ManagedObjectRepresentation agent;
	private ManagedObjectRepresentation device;

	private InventoryApi inventoryApi;
	private DeviceControlApi deviceControlApi;

	protected void startApp() throws MIDletStateChangeException {
		initialize();
		
		createManagedObject();
		createMeasurement();
		
		createAgentAndDevice();
		createAndUpdateOperation();
		
		destroyApp(true);
	}

	private void initialize() {
		// establish GPRS connection
		sendCommand("AT+CMEE=2");
		sendCommand("AT^SJNET=\"gprs\",\"<<apn>>\",\"\",\"\"");

		// create Cumulocity Platform
		platform = new PlatformImpl("<<platform_url>>", "<<tenant_id>>", "<<username>>", "<<password>>", "<<application_key>>");
		inventoryApi = platform.getInventoryApi();
		deviceControlApi = platform.getDeviceControlApi();

		// register custom fragments
		SignalStrengthSensorConverter converter = new SignalStrengthSensorConverter();
		platform.getConversionService().register(converter);
		platform.getValidationService().register(converter);
	}

	private void createManagedObject() {
		mo = new ManagedObjectRepresentation();
		mo.setName("Hello World!");
		mo.setType("com.type");
		mo = inventoryApi.create(mo);
	}

	private void createMeasurement() {
		MeasurementRepresentation measurement = new MeasurementRepresentation();
		measurement.setSource(mo);
		measurement.setTime(new Date());
		measurement.setType("SignalStrengthSensor_measurement");
		
		SignalStrengthSensor signalStrengthSensor = new SignalStrengthSensor();
		signalStrengthSensor.setType("SignalStrengthSensor");
		signalStrengthSensor.setUnit("dBm");
		signalStrengthSensor.setReading(String.valueOf(getSignalStrength()));
		measurement.set(signalStrengthSensor);
		
		platform.getMeasurementApi().create(measurement);
	}
	
	private void createAgentAndDevice() {
		//create agent managedObject
		agent = new ManagedObjectRepresentation();
		agent.setName("Agent");
		agent.setType("com.type");
		agent.set("{}", "com_cumulocity_model_Agent");
		agent = inventoryApi.create(agent);

		//create device managedObject
		device = new ManagedObjectRepresentation();
		device.setName("Device");
		device.setType("com.type");
		device = inventoryApi.create(device);

		//create deviceReference and add childDevice
		ManagedObjectReferenceRepresentation deviceRef = new ManagedObjectReferenceRepresentation();
		deviceRef.setManagedObject(device);
		ManagedObject agentMo = inventoryApi.getManagedObject(agent.getId());
		agentMo.addChildDevice(deviceRef);
	}

	private void createAndUpdateOperation() {
		//create new operation
		GId deviceId = device.getId();
		OperationRepresentation operation = new OperationRepresentation();
		operation.setDeviceId(deviceId);
		operation.set("sample_value", "sample_operation_type");
		operation = deviceControlApi.create(operation);
		
		//get created operation from platform
		GId operationId = operation.getId();
		operation = deviceControlApi.getOperation(operationId);
		System.out.println("Operation status: "+operation.getStatus());

		//update and get updated operation
		operation.setStatus("SUCCESSFUL");
		operation = deviceControlApi.update(operation);
		operation = deviceControlApi.getOperation(operationId);
		System.out.println("New operation status: "+operation.getStatus());
	}

	private int getSignalStrength() {
		int ret = 0;
		String atResponse = sendCommand("AT+CSQ");
		int beg = atResponse.indexOf(": ");
		int end = atResponse.indexOf(",");
		if (beg > -1) {
			if (end > -1) {
				ret = Integer.parseInt(atResponse.substring(beg + 2, end));
			}
		}
		return ret * 2 - 113;
	}

	private String sendCommand(String command) {
		try {
			if (ATC == null) {
				ATC = new ATCommand(false);
			}
			String result = ATC.send(command + "\r\n");
			Thread.sleep(1000);
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	protected void pauseApp() {
	}

	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		notifyDestroyed();
	}
}
