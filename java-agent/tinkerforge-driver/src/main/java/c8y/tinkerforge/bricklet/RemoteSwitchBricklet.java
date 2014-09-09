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

import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;
import com.tinkerforge.BrickletRemoteSwitch;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import c8y.RelayArray;
import c8y.lx.driver.Configurable;
import c8y.lx.driver.DeviceManagedObject;
import c8y.lx.driver.Driver;
import c8y.lx.driver.OperationExecutor;
import c8y.lx.driver.OpsUtil;
import c8y.tinkerforge.TFIds;

public class RemoteSwitchBricklet implements Driver, Configurable {
	
	private static final String TYPE = "RemoteSwitch";
	private static final String TYPE_PROP = ".type";
	//Type A addressing:
	private static final String HOUSE_CODE_PROP = ".houseCode";
	private static final String RECEIVER_CODE_PROP = ".receiverCode";
	//Type B addressing:
	private static final String ADDRESS_PROP = ".address";
	private static final String UNIT_PROP = ".unit"; 
	//Type C addressing:
	private static final String SYSTEM_CODE_PROP = ".systemCode";
	private static final String DEVICE_CODE_PROP = ".deviceCode";
	
	private static final Logger logger = LoggerFactory
			.getLogger(RemoteSwitchBricklet.class);
	
	private String id;
	private BrickletRemoteSwitch remoteSwitch;
	private ManagedObjectRepresentation remoteSwitchMo = 
			new ManagedObjectRepresentation();
	private Platform platform;
	
	private ArrayList<RemoteDevice> devices = new ArrayList<RemoteDevice>();
	private ArrayList<RemoteDevice> exampleDevices = new ArrayList<RemoteDevice>();
	
	public RemoteSwitchBricklet(String id, BrickletRemoteSwitch remoteSwitch) {
		this.id=id;
		this.remoteSwitch = remoteSwitch;
		//prepare some example devices to be added as defaults
		exampleDevices.add(new RemoteDeviceA("exampleDeviceA", (short)0b10001, (short)0b00100));
		exampleDevices.add(new RemoteDeviceB("exampleDeviceB", 108863, (short)0b0110));
		exampleDevices.add(new RemoteDeviceC("exampleDeviceC", 'E' , (short)0b1010));
	}
	
	@Override
	public void initialize() throws Exception {
		// Nothing to be done here.
	}
	
	@Override
	public void addDefaults(Properties props) {
		for(RemoteDevice device:exampleDevices){
			if(device instanceof RemoteDeviceA){
				props.setProperty(TFIds.getPropertyName(TYPE)+"."+device.name+TYPE_PROP, "A");
				props.setProperty(TFIds.getPropertyName(TYPE)+"."+device.name+HOUSE_CODE_PROP, Short.toString(((RemoteDeviceA)device).houseCode));
				props.setProperty(TFIds.getPropertyName(TYPE)+"."+device.name+RECEIVER_CODE_PROP, Short.toString(((RemoteDeviceA)device).receiverCode));
			}
			else if(device instanceof RemoteDeviceB){
				props.setProperty(TFIds.getPropertyName(TYPE)+"."+device.name+TYPE_PROP, "B");
				props.setProperty(TFIds.getPropertyName(TYPE)+"."+device.name+ADDRESS_PROP, Long.toString(((RemoteDeviceB)device).address));
				props.setProperty(TFIds.getPropertyName(TYPE)+"."+device.name+UNIT_PROP, Short.toString(((RemoteDeviceB)device).unit));
			}
			else if(device instanceof RemoteDeviceC){
				props.setProperty(TFIds.getPropertyName(TYPE)+"."+device.name+TYPE_PROP, "C");
				props.setProperty(TFIds.getPropertyName(TYPE)+"."+device.name+SYSTEM_CODE_PROP, Long.toString(((RemoteDeviceC)device).systemCode));
				props.setProperty(TFIds.getPropertyName(TYPE)+"."+device.name+DEVICE_CODE_PROP, Short.toString(((RemoteDeviceC)device).deviceCode));
			}
		}
	}

	@Override
	public void configurationChanged(Properties props) {
		devices.clear();
		Set<String> keys = props.stringPropertyNames();
		ArrayList<String> deviceNames = new ArrayList<String>();
		for(String key:keys){
			String keyArray[] = key.split(".");
			if(TYPE.equalsIgnoreCase(keyArray[1]))
				if(!deviceNames.contains(keyArray[2]))
					deviceNames.add(keyArray[2]);
		}
		
		for(String deviceName:deviceNames){
			try {
				switch(props.getProperty(TFIds.getPropertyName(TYPE)+"."+deviceName+TYPE_PROP)){
					case "A":
					case "a":
						devices.add(new RemoteDeviceA(deviceName, 
								Short.parseShort(props.getProperty(TFIds.getPropertyName(TYPE)+"."+deviceName+HOUSE_CODE_PROP)), 
								Short.parseShort(props.getProperty(TFIds.getPropertyName(TYPE)+"."+deviceName+RECEIVER_CODE_PROP))));
						break;
					case "B":
					case "b":
						devices.add(new RemoteDeviceB(deviceName, 
								Long.parseLong(props.getProperty(TFIds.getPropertyName(TYPE)+"."+deviceName+ADDRESS_PROP)), 
								Short.parseShort(props.getProperty(TFIds.getPropertyName(TYPE)+"."+deviceName+UNIT_PROP))));
						break;
					case "C":
					case "c":
						devices.add(new RemoteDeviceC(deviceName, 
								props.getProperty(TFIds.getPropertyName(TYPE)+"."+deviceName+SYSTEM_CODE_PROP).toCharArray()[0],
								Short.parseShort(props.getProperty(TFIds.getPropertyName(TYPE)+"."+deviceName+DEVICE_CODE_PROP))));
						break;
					default:
						logger.warn("Unknown type for device {}", deviceName);
				}
			} catch (NumberFormatException x) {
				logger.warn("Error reading device configuration for device "+deviceName, x);
			}
		}
		
	}

	@Override
	public void initialize(Platform platform) throws Exception {
		this.platform=platform;
	}

	@Override
	public OperationExecutor[] getSupportedOperations() {
		return new OperationExecutor[] { new RelayArrayOperationExecutor() };
	}

	@Override
	public void initializeInventory(ManagedObjectRepresentation mo) {
		// Nothing to be done here.

	}

	@Override
	public void discoverChildren(ManagedObjectRepresentation parent) {
		try {
			remoteSwitchMo.set(TFIds.getHardware(remoteSwitch, TYPE));
		} catch (TimeoutException | NotConnectedException e) {
			logger.warn("Cannot read hardware parameters", e);
		}
		
		remoteSwitchMo.setType(TFIds.getType(TYPE));
		remoteSwitchMo.setName(TFIds.getDefaultName(parent.getName(), TYPE, id));
		
		for(OperationExecutor operation:getSupportedOperations())
			OpsUtil.addSupportedOperation(remoteSwitchMo, operation.supportedOperationType());
		
		try {
			DeviceManagedObject dmo = new DeviceManagedObject(platform);
			dmo.createOrUpdate(remoteSwitchMo, TFIds.getXtId(id), parent.getId());
		} catch (SDKException e) {
			logger.warn("Cannot create remote switch object", e);
		}
		
	}

	@Override
	public void start() {
		// Nothing to be done here.
	}

	class RelayArrayOperationExecutor implements OperationExecutor {

		@Override
		public String supportedOperationType() {
			return "c8y_RelayArray";
		}

		@Override
		public void execute(OperationRepresentation operation, boolean cleanup)
				throws Exception {
			if (!remoteSwitchMo.getId().equals(operation.getDeviceId())) {
				// Silently ignore the operation if it is not targeted to us,
				// another driver will (hopefully) care.
				return;
			}
			if (cleanup)
				operation.setStatus(OperationStatus.FAILED.toString());

			RelayArray relayArray = operation.get(RelayArray.class);
			for(int i=0;i<devices.size()&&i<relayArray.size();i++){
				devices.get(i).switchDevice( (short)("CLOSED".equals(relayArray.get(i)) ? 1 : 0) );
			}
			
			operation.setStatus(OperationStatus.SUCCESSFUL.toString());
		}
		
		
	}
	
	abstract class RemoteDevice {
		private String name;
		public RemoteDevice(String name){
			this.name=name;
		}
		public abstract void switchDevice(short switchTo) throws TimeoutException, NotConnectedException;
	}
	
	interface Dimmable {
		public void dimDevice(short dimValue) throws TimeoutException, NotConnectedException;
	}
	
	class RemoteDeviceA extends RemoteDevice{
		private short houseCode;
		private short receiverCode;
		public RemoteDeviceA(String name, short houseCode, short receiverCode) {
			super(name);
			this.houseCode=houseCode;
			this.receiverCode=receiverCode;
		}
		@Override
		public void switchDevice(short switchTo) throws TimeoutException, NotConnectedException {
			remoteSwitch.switchSocketA(houseCode, receiverCode, switchTo);
		}
	}
	
	class RemoteDeviceB extends RemoteDevice implements Dimmable{
		private long address;
		private short unit;
		public RemoteDeviceB(String name, long address, short unit){
			super(name);
			this.address=address;
			this.unit=unit;
		}
		@Override
		public void switchDevice(short switchTo) throws TimeoutException, NotConnectedException {
			remoteSwitch.switchSocketB(switchTo, switchTo, switchTo);
		}
		@Override
		public void dimDevice(short dimValue) throws TimeoutException, NotConnectedException {
			remoteSwitch.dimSocketB(address, unit, dimValue);
		}
	}
	
	class RemoteDeviceC extends RemoteDevice{
		private char systemCode;
		private short deviceCode;
		public RemoteDeviceC(String name, char systemCode, short deviceCode){
			super(name);
			this.systemCode=Character.toUpperCase(systemCode);
			this.deviceCode=deviceCode;
		}
		@Override
		public void switchDevice(short switchTo) throws TimeoutException, NotConnectedException {
			remoteSwitch.switchSocketC(systemCode, deviceCode, switchTo);
		}
	}
	
}
