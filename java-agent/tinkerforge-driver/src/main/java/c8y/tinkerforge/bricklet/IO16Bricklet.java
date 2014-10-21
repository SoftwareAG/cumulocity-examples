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
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;
import com.tinkerforge.BrickletIO16;
import com.tinkerforge.BrickletIO16.PortConfiguration;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import c8y.RelayArray;
import c8y.lx.driver.Configurable;
import c8y.lx.driver.DeviceManagedObject;
import c8y.lx.driver.Driver;
import c8y.lx.driver.OperationExecutor;
import c8y.lx.driver.OpsUtil;
import c8y.tinkerforge.TFIds;

public class IO16Bricklet implements Driver, Configurable {

	class Port{
		private final static String DIRECTION_PROP = ".direction";
		private final static String VALUE_PROP= ".value";
		private char name;
		private PortConfiguration defaultConfiguration = io16.new PortConfiguration();
		private PortConfiguration actualConfiguration = io16.new PortConfiguration();
		
		public Port(char name, short defaultDirection, short defaultValue) {
			this.name=name;
			this.defaultConfiguration.directionMask = defaultDirection;
			this.defaultConfiguration.valueMask = defaultValue;
		}
	}
	
	private static final String TYPE = "IO16";
	private static final String EVENT_TYPE = "c8y_InputChangeEvent";
	private static final String DEBOUNCE_PERIOD_PROP = ".debouncePeriod";
	
	private final long defaultDebouncePeriod = 1000;
	private long actualDeboncePeriod;
	private static final Logger logger = LoggerFactory
			.getLogger(IO16Bricklet.class);
	
	private String id;
	private BrickletIO16 io16;
	private ManagedObjectRepresentation io16Mo = new ManagedObjectRepresentation();
	private RelayArray state = new RelayArray();
	private EventRepresentation inputChangeEvent = new EventRepresentation();
	private Platform platform;
	
	private Port[] ports;
	
	/*
	 * direction: 0-Output 1-input
	 * value: OUTPUT: 0,1-logical values INPUT: 0-default 1-pull up
	 * 
	 * With this in mind the configuration in the constructor below
	 * does the following: Configures all pins on portA as input with 
	 * pull ups and all pins on portB as output set to 0 logical level.
	 */
	public IO16Bricklet(String uid, BrickletIO16 io16) {
		this.id = uid;
		this.io16 = io16;
		ports = new Port[]{	new Port('a', (short)0b11111111, (short)0b11111111), 
					new Port('b', (short)0b00000000, (short)0b00000000)  };
	}
	
	@Override
	public void addDefaults(Properties props) {
		props.setProperty(TFIds.getPropertyName(TYPE)+DEBOUNCE_PERIOD_PROP, Long.toString(defaultDebouncePeriod));
		for(Port port:ports){
			props.setProperty(TFIds.getPropertyName(TYPE)+".port"+port.name+Port.DIRECTION_PROP, 
					Short.toString(port.defaultConfiguration.directionMask));
			props.setProperty(TFIds.getPropertyName(TYPE)+".port"+port.name+Port.VALUE_PROP, 
					Short.toString(port.defaultConfiguration.valueMask));
		}
	}
	
	@Override
	public void configurationChanged(Properties props) {
		actualDeboncePeriod = Long.parseLong(props.getProperty(TFIds.getPropertyName(TYPE)+DEBOUNCE_PERIOD_PROP, 
											Long.toString(defaultDebouncePeriod)));
		try {
			io16.setDebouncePeriod(actualDeboncePeriod);
		} catch (TimeoutException | NotConnectedException e) {
			logger.warn("Error setting debounce period", e);
		}
		for(Port port:ports){
			port.actualConfiguration.directionMask=Short.parseShort( props.getProperty(
					TFIds.getPropertyName(TYPE)+".port"+port.name+Port.DIRECTION_PROP, 
					Short.toString(port.defaultConfiguration.directionMask )));
			port.actualConfiguration.valueMask=Short.parseShort( props.getProperty(
					TFIds.getPropertyName(TYPE)+".port"+port.name+Port.VALUE_PROP, 
					Short.toString(port.defaultConfiguration.valueMask )));
			setPort(port);
		}
		/*
		 * Due to the nature of it's modeling, it is possible to change the state of the io16 bricklet through c8y_Configuration.
		 * To avoid persisting old c8y_RelayArray states after a configuration change an update of the c8y_RelayArray 
		 * configuration is required.
		 */
		if(io16Mo.getId()!=null) {
			initStateFromConfig();
			io16Mo.set(state);
			try {
				//Needed in order to reduce overhead
				ManagedObjectRepresentation updateMo = new ManagedObjectRepresentation();
				updateMo.setId(io16Mo.getId());
				updateMo.set(state);
				platform.getInventoryApi().update(updateMo);
			} catch(SDKException e) {
				logger.warn("Couldn't update device state on the platform", e);
			}
		}
	}
	
	@Override
	public void initialize() throws Exception {
		inputChangeEvent.setSource(io16Mo);
		inputChangeEvent.setType(EVENT_TYPE);
	}

	@Override
	public void initialize(Platform platform) throws Exception {
		this.platform = platform;
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
			io16Mo.set(TFIds.getHardware(io16, TYPE));
		} catch (TimeoutException | NotConnectedException e) {
			logger.warn("Cannot read hardware parameters", e);
		}
		
		io16Mo.setType(TFIds.getType(TYPE));
		io16Mo.setName(TFIds.getDefaultName(parent.getName(), TYPE, id));
		
		for(OperationExecutor operation:getSupportedOperations())
			OpsUtil.addSupportedOperation(io16Mo, operation.supportedOperationType());
		
		try {
			DeviceManagedObject dmo = new DeviceManagedObject(platform);
			dmo.createOrUpdate(io16Mo, TFIds.getXtId(id), parent.getId());
		} catch (SDKException e) {
			logger.warn("Cannot create or update MO", e);
		}
	}

	@Override
	public void start() {
		io16.addInterruptListener(new BrickletIO16.InterruptListener() {
			
			@Override
			public void interrupt(char port, short interruptMask, short valueMask) {
				inputChangeEvent.setTime(new Date());
				inputChangeEvent.setProperty("port", Character.toString(port));
				for(int i=0;i<8;i++){
					if(interruptMask%2==1){
						inputChangeEvent.setText("Pin "+Character.toString(port).toUpperCase()+Integer.toString(i)+" changed to "+(valueMask%2==1?"logical 1":"logical 0"));
						inputChangeEvent.setProperty("pin", i);
						inputChangeEvent.setProperty("value", valueMask%2);
						platform.getEventApi().create(inputChangeEvent);
					}
					interruptMask=(short)(interruptMask>>1);
					valueMask=(short)(valueMask>>1);
				}
				
			}
		});
		
		/*
		 * The state is updated.
		 * If there is no state in the inventory, it's initialized first.
		 */
		state = io16Mo.get(RelayArray.class);
		if(state==null){
			state = new RelayArray();
			initStateFromConfig();
			io16Mo.set(state);
			try {
				//Needed in order to reduce overhead
				ManagedObjectRepresentation updateMo = new ManagedObjectRepresentation();
				updateMo.setId(io16Mo.getId());
				updateMo.set(state);
				platform.getInventoryApi().update(updateMo);
			} catch(SDKException e) {
				logger.warn("Couldn't update device state on the platform", e);
			}
		}
		updatePorts();
		
	}
	
	class RelayArrayOperationExecutor implements OperationExecutor{

		@Override
		public String supportedOperationType() {
			return "c8y_RelayArray";
		}

		@Override
		public void execute(OperationRepresentation operation, boolean cleanup)
				throws Exception {
			if (!io16Mo.getId().equals(operation.getDeviceId())) {
				// Silently ignore the operation if it is not targeted to us,
				// another driver will (hopefully) care.
				return;
			}
			if (cleanup)
				operation.setStatus(OperationStatus.FAILED.toString());
			
			state = operation.get(RelayArray.class);
			updatePorts();
			
			/*
			 * State is persisted after each change.
			 */
			io16Mo.set(state);
			//Needed in order to reduce overhead
			ManagedObjectRepresentation updateMo = new ManagedObjectRepresentation();
			updateMo.setId(io16Mo.getId());
			updateMo.set(state);
			platform.getInventoryApi().update(updateMo);
			
			operation.setStatus(OperationStatus.SUCCESSFUL.toString());
		}
		
	}
	
	private void setPort(Port port){
		try{
			io16.setPortConfiguration(port.name, (short)(port.actualConfiguration.directionMask&port.actualConfiguration.valueMask), BrickletIO16.DIRECTION_IN, true);
			io16.setPortConfiguration(port.name, (short)(port.actualConfiguration.directionMask&negate(port.actualConfiguration.valueMask)), BrickletIO16.DIRECTION_IN, false);
			io16.setPortConfiguration(port.name, (short)(negate(port.actualConfiguration.directionMask)&port.actualConfiguration.valueMask), BrickletIO16.DIRECTION_OUT, true);
			io16.setPortConfiguration(port.name, (short)(negate(port.actualConfiguration.directionMask)&negate(port.actualConfiguration.valueMask)), BrickletIO16.DIRECTION_OUT, false);
			io16.setPortInterrupt(port.name, port.actualConfiguration.directionMask);
		} catch (Exception x){
			logger.warn("Error setting port", x);
		}
	}
	
	private short negate(short s){
		return (short)((short)0b11111111-s);
	}
	/*
	 * The idea is to update the values of output pins only(starting from pin A0 up to pin B7) using the 
	 * relay values(CLOSED - logical true).
	 */
	private void updatePorts(){
		boolean direction[] = createBooleanArray(ports[0].actualConfiguration.directionMask, ports[1].actualConfiguration.directionMask);
		boolean value[] = createBooleanArray(ports[0].actualConfiguration.valueMask, ports[1].actualConfiguration.valueMask);
		/*
		 * TODO: This feels like a naughty hack. Maybe update Relay.RelayState to make it castable to String somehow. 
		 * Or modify how it is parsed. 
		 */
		int relayCounter=0;
		for(int i=0; i<direction.length; i++){
			if(!direction[i]&&relayCounter<state.size()) {
				value[i]="CLOSED".equalsIgnoreCase(state.get(relayCounter));
				relayCounter++;
			}
		}
		
		int newValueMasks[]=getValueMasks(value);
		
		ports[0].actualConfiguration.valueMask=(short) newValueMasks[0];
		ports[1].actualConfiguration.valueMask=(short) newValueMasks[1];
		
		for(Port p:ports)
			setPort(p);
	}
	
	private void initStateFromConfig(){
		boolean direction[] = createBooleanArray(ports[0].actualConfiguration.directionMask, ports[1].actualConfiguration.directionMask);
		boolean value[] = createBooleanArray(ports[0].actualConfiguration.valueMask, ports[1].actualConfiguration.valueMask);
		state.clear();
		for(int i=0; i<direction.length; i++)
			if(!direction[i])
				state.add(value[i]?"CLOSED":"OPEN");
	}

	public static boolean[] createBooleanArray(int a, int b){
		boolean result[] = new boolean[16];
		
		for(int i=0;i<8;i++){
			result[i] = a%2==1;
			a=a>>1;
		}
		for(int i=8;i<16;i++){
			result[i] = b%2==1;
			b=b>>1;
		}
		
		return result;
	}
	
	public static int[] getValueMasks(boolean[] value) {
		int result[] = {0,0};
		
		for(int i = 7;i>=0; i--){
			result[0]=result[0]<<1;
			result[0]+=value[i]?1:0;
		}
		
		for(int i = 15;i>=8; i--){
			result[1]=result[1]<<1;
			result[1]+=value[i]?1:0;
		}
		
		return result;
	}

}
