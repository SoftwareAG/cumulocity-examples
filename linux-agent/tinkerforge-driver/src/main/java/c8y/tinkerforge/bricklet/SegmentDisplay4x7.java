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

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;
import com.tinkerforge.BrickletSegmentDisplay4x7;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import c8y.Message;
import c8y.lx.driver.Configurable;
import c8y.lx.driver.DeviceManagedObject;
import c8y.lx.driver.Driver;
import c8y.lx.driver.OperationExecutor;
import c8y.lx.driver.OpsUtil;
import c8y.tinkerforge.TFIds;

public class SegmentDisplay4x7 implements Driver, Configurable{
	
	private static final String MESSAGE_OP_TYPE="c8y_Message";
	private static final String TYPE="4x7SegmentDisplay";
	private static final short DEFAULT_BRIGHTNESS = 4;
	private static final String BRIGHTNESS_PROP = ".brightness";
	
	private static final Logger logger = LoggerFactory
			.getLogger(SegmentDisplay4x7.class);

	private BrickletSegmentDisplay4x7 display;
	private ManagedObjectRepresentation displayMo = new ManagedObjectRepresentation();
	private Platform platform;
	private String id;
	private short actualBrightness;
	private Message msg = new Message();
	
	public SegmentDisplay4x7(String id, BrickletSegmentDisplay4x7 display) {
		actualBrightness=DEFAULT_BRIGHTNESS;
		this.id=id;
		this.display=display;
	}
	
	@Override
	public void addDefaults(Properties props) {
		props.setProperty(TFIds.getPropertyName(TYPE)+BRIGHTNESS_PROP, Short.toString(DEFAULT_BRIGHTNESS));
	}

	@Override
	public void configurationChanged(Properties props) {
		actualBrightness=Short.parseShort(props.getProperty(TFIds.getPropertyName(TYPE)+BRIGHTNESS_PROP, Short.toString(DEFAULT_BRIGHTNESS)));
		if(msg!=null&&msg.getText()!=null)
			submitText(msg.getText());
	}
	
	@Override
	public void initialize() throws Exception {
		// Nothing to be done.
	}

	@Override
	public void initialize(Platform platform) throws Exception {
		this.platform=platform;
	}

	@Override
	public OperationExecutor[] getSupportedOperations() {
		return new OperationExecutor[]{new MessageOperationExecutor()};
	}

	@Override
	public void initializeInventory(ManagedObjectRepresentation mo) {
		// Nothing to be done.
	}

	@Override
	public void discoverChildren(ManagedObjectRepresentation parent) {
		try {
			displayMo.set(TFIds.getHardware(display, TYPE));
		} catch (TimeoutException | NotConnectedException e) {
			logger.warn("Cannot read hardware parameters", e);
		}
		
		displayMo.setType(TFIds.getType(TYPE));
		displayMo.setName(TFIds.getDefaultName(parent.getName(), TYPE, id));
		
		for(OperationExecutor operation:getSupportedOperations())
			OpsUtil.addSupportedOperation(displayMo, operation.supportedOperationType());
		
		try {
			DeviceManagedObject dmo = new DeviceManagedObject(platform);
			dmo.createOrUpdate(displayMo, TFIds.getXtId(id), parent.getId());
		} catch (SDKException e) {
			logger.warn("Cannot create remote switch object", e);
		}
	}

	@Override
	public void start() {
		msg = displayMo.get(Message.class);
		if(msg!=null)
			submitText(msg.getText());
	}
	
	class MessageOperationExecutor implements OperationExecutor{

		@Override
		public String supportedOperationType() {
			return MESSAGE_OP_TYPE;
		}

		@Override
		public void execute(OperationRepresentation operation, boolean cleanup)
				throws Exception {
			if (cleanup)
				operation.setStatus(OperationStatus.FAILED.toString());
			
			msg = operation.get(Message.class);
			if(msg!=null){
				submitText(msg.getText());
				try {
					displayMo.set(msg);
					displayMo=platform.getInventoryApi().update(displayMo);
					System.err.println("INVENTORY UPDATED!");
				} catch (SDKException x) {
					logger.warn("Cannot update bricklet", x);
				}
				operation.setStatus(OperationStatus.SUCCESSFUL.toString());
			}
			else operation.setStatus(OperationStatus.FAILED.toString());
			
			
		}
		
	} 
	
	private void submitText(String text) {
		
		final short SYMBOL_0 = 0b00111111; 
		final short SYMBOL_1 = 0b00000110; 
		final short SYMBOL_2 = 0b01011011; 
		final short SYMBOL_3 = 0b01001111; 
		final short SYMBOL_4 = 0b01100110; 
		final short SYMBOL_5 = 0b01101101; 
		final short SYMBOL_6 = 0b01111101; 
		final short SYMBOL_7 = 0b00000111; 
		final short SYMBOL_8 = 0b01111111; 
		final short SYMBOL_9 = 0b01101111; 
		final short SYMBOL_A = 0b01110111; 
		final short SYMBOL_B = 0b01111100; 
		final short SYMBOL_C = 0b00111001; 
		final short SYMBOL_D = 0b01011110; 
		final short SYMBOL_E = 0b01111001; 
		final short SYMBOL_F = 0b01110001;
		final short SYMBOL_UNKNOWN = 0b01000000;
		final short SYMBOL_EMPTY = 0b00000000;
		
		char msgArray[] = text.toCharArray();
		short segments[] = new short[] {SYMBOL_EMPTY, SYMBOL_EMPTY, SYMBOL_EMPTY, SYMBOL_EMPTY};
		for(int i=0;i<msgArray.length&&i<segments.length;i++){
			switch(msgArray[i]){
				case '0':
					segments[i]=SYMBOL_0;
					break;
				case '1':
					segments[i]=SYMBOL_1;
					break;
				case '2':
					segments[i]=SYMBOL_2;
					break;
				case '3':
					segments[i]=SYMBOL_3;
					break;
				case '4':
					segments[i]=SYMBOL_4;
					break;
				case '5':
					segments[i]=SYMBOL_5;
					break;
				case '6':
					segments[i]=SYMBOL_6;
					break;
				case '7':
					segments[i]=SYMBOL_7;
					break;
				case '8':
					segments[i]=SYMBOL_8;
					break;
				case '9':
					segments[i]=SYMBOL_9;
					break;
				case 'A':
				case 'a':
					segments[i]=SYMBOL_A;
					break;
				case 'B':
				case 'b':
					segments[i]=SYMBOL_B;
					break;
				case 'C':
				case 'c':
					segments[i]=SYMBOL_C;
					break;
				case 'D':
				case 'd':
					segments[i]=SYMBOL_D;
					break;
				case 'E':
				case 'e':
					segments[i]=SYMBOL_E;
					break;
				case 'F':
				case 'f':
					segments[i]=SYMBOL_F;
					break;
				case ' ':
					segments[i]=SYMBOL_EMPTY;
					break;
				default:
					segments[i]=SYMBOL_UNKNOWN;
			}
		}
		try {
			display.setSegments(segments, actualBrightness, false);
		} catch (TimeoutException | NotConnectedException x) {
			logger.warn("Error writing text to display.", x);
		}
	}

}
