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
import java.util.Collections;
import java.util.Comparator;
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

import c8y.Hardware;
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
	private RelayArray state;
	
	public RemoteSwitchBricklet(String id, BrickletRemoteSwitch remoteSwitch) {
		this.id=id;
		this.remoteSwitch = remoteSwitch;
	}
	
	@Override
	public void initialize() throws Exception {
		// Nothing to be done here.
	}
	
	@Override
	public void addDefaults(Properties props) {
		
	}

	@Override
	public void configurationChanged(Properties props) {
		devices.clear();
		Set<String> keys = props.stringPropertyNames();
		ArrayList<String> deviceNames = new ArrayList<String>();
		for(String key:keys){
			String keyArray[] = key.split("\\.");
			if(keyArray.length==4&&"c8y".equalsIgnoreCase(keyArray[0])&&TYPE.equalsIgnoreCase(keyArray[1]))
				if(!deviceNames.contains(keyArray[2]))
					deviceNames.add(keyArray[2]);
		}
		
		state = new RelayArray();
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
				state.add("OPEN");
			} catch (NumberFormatException x) {
				logger.warn("Error reading device configuration for device "+deviceName, x);
			}
			//TODO: At first startup will this correctly set the state fragment? Test. 
			remoteSwitchMo.set(state);
			if(remoteSwitchMo.getId()!=null)
				persistState();
		}
		Collections.sort(devices, new Comparator<RemoteDevice>() {

			@Override
			public int compare(RemoteDevice o1, RemoteDevice o2) {
				return o1.name.compareTo(o2.name);
			}
		});
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
			dmo.createOrUpdate(remoteSwitchMo, TFIds.getXtId(id, parent.get(Hardware.class).getSerialNumber()), parent.getId());
		} catch (SDKException e) {
			logger.warn("Cannot create remote switch object", e);
		}
		
	}

	@Override
	public void start() {
		/*
		 * The state is updated.
		 * If there is no state in the inventory, it's initialized first.
		 * TODO: Maybe doing double the work here and configurationChanged. Investigate.
		 */
		state = remoteSwitchMo.get(RelayArray.class);
		if(state == null){
			state = new RelayArray();
			for(RemoteDevice d:devices)
				state.add("OPEN");
			remoteSwitchMo.set(state);
		}
		try {
			switchDevices();
		} catch (NotConnectedException | TimeoutException x) {
			logger.warn("State initialization failed.");
		}
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
			
			if(remoteSwitch.getSwitchingState()==BrickletRemoteSwitch.SWITCHING_STATE_READY){
				state = operation.get(RelayArray.class);
				switchDevices();
				persistState();
				operation.setStatus(OperationStatus.SUCCESSFUL.toString());
				
			} else {
				operation.setFailureReason("Bricklet is busy.");
				operation.setStatus(OperationStatus.FAILED.toString());
			}
		}
		
		
	}
	
	private void switchDevices() throws TimeoutException, NotConnectedException{
		for(int i=0;i<devices.size()&&i<state.size();i++){
			System.err.println("devices.get(i).switchDevice("+("CLOSED".equals(state.get(i)) ? BrickletRemoteSwitch.SWITCH_TO_ON : BrickletRemoteSwitch.SWITCH_TO_OFF)+");");
			devices.get(i).switchDevice( ("CLOSED".equals(state.get(i)) ? BrickletRemoteSwitch.SWITCH_TO_ON : BrickletRemoteSwitch.SWITCH_TO_OFF) );
		}
	}
	
	void persistState(){
		try {
			//Needed in order to reduce overhead
			ManagedObjectRepresentation updateMo = new ManagedObjectRepresentation();
			updateMo.setId(remoteSwitchMo.getId());
			updateMo.set(state);
			platform.getInventoryApi().update(updateMo);
		} catch(SDKException e) {
			logger.warn("Couldn't persist device state on the platform", e);
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
			remoteSwitch.switchSocketB(address, unit, switchTo);
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
