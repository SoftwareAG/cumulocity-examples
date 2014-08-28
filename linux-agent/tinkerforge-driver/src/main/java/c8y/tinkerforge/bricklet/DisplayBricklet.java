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

import c8y.Message;
import c8y.Relay;
import c8y.Relay.RelayState;
import c8y.lx.driver.DeviceManagedObject;
import c8y.lx.driver.Driver;
import c8y.lx.driver.OperationExecutor;
import c8y.lx.driver.OpsUtil;

import c8y.tinkerforge.TFIds;

import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.event.EventApi;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.BrickletLCD20x4.ButtonPressedListener;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class DisplayBricklet implements Driver {

	public static final short LINES = 4;
	public static final short WIDTH = 20;
	public static final String TYPE = "Display";

	private static final Logger logger = LoggerFactory
			.getLogger(DisplayBricklet.class);

	private Platform platform;
	private EventApi events;
	private ManagedObjectRepresentation displayMo = new ManagedObjectRepresentation();
	private EventRepresentation buttonEvent = new EventRepresentation();
	private String id;
	private Message msg=new Message();
	private BrickletLCD20x4 display;

	public DisplayBricklet(String id, BrickletLCD20x4 display) {
		this.id = id;
		this.display = display;
	}

    @Override
    public void initialize() throws Exception {
        // Nothing to do here.
    }

	@Override
	public void initialize(Platform platform) throws Exception {
		this.platform = platform;
		this.events = platform.getEventApi();
	}

	@Override
	public void initializeInventory(ManagedObjectRepresentation mo) {
		// Nothing to do here.
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

			buttonEvent.setSource(displayMo);
			buttonEvent.setType("c8y_ButtonPressedEvent");
		} catch (SDKException e) {
			logger.warn("Cannot create sensor", e);
		}
	}

	@Override
	public void start() {
		try {
			msg=displayMo.get(Message.class);
			if(msg!=null&&msg.getText()!=null)
				submitText(msg.getText());
		} catch (TimeoutException | NotConnectedException x) {
			logger.warn("Error initializing display msg.", x);
		}
		
		display.addButtonPressedListener(new ButtonPressedListener() {
			@Override
			public void buttonPressed(short button) {
				buttonEvent.setTime(new Date());
				buttonEvent.setText("Button pressed: " + Short.toString(button));
				try {
					events.create(buttonEvent);
				} catch (SDKException e) {
					logger.warn("Could not send event", e);
				}
			}
		});
	}

	@Override
	public OperationExecutor[] getSupportedOperations() {
		return new OperationExecutor[] { new MessageOperationExecutor(), new BacklightOperationExecutor() };
	}

	class MessageOperationExecutor implements OperationExecutor {
		@Override
		public String supportedOperationType() {
			return "c8y_Message";
		}

		@Override
		public void execute(OperationRepresentation operation, boolean cleanup)
				throws Exception {
			if (!displayMo.getId().equals(operation.getDeviceId())) {
				// Silently ignore the operation if it is not targeted to us,
				// another driver will (hopefully) care.
				return;
			}
			if (cleanup) {
				operation.setStatus(OperationStatus.FAILED.toString());
			}

			msg = operation.get(Message.class);
			submitText(msg.getText());
			displayMo.set(msg);
			platform.getInventoryApi().update(displayMo);

			operation.setStatus(OperationStatus.SUCCESSFUL.toString());
		}
	}

	class BacklightOperationExecutor implements OperationExecutor {
		@Override
		public String supportedOperationType() {
			return "c8y_Relay";
		}

		@Override
		public void execute(OperationRepresentation operation, boolean cleanup)
				throws Exception {
			if (!displayMo.getId().equals(operation.getDeviceId())) {
				// Silently ignore the operation if it is not targeted to us,
				// another driver will (hopefully) care.
				return;
			}
			if (cleanup) {
				operation.setStatus(OperationStatus.FAILED.toString());
			}

			RelayState state = operation.get(Relay.class).getRelayState();
			if (state == RelayState.CLOSED) {
				display.backlightOn();
			} else {
				display.backlightOff();
			}

			operation.setStatus(OperationStatus.SUCCESSFUL.toString());
		}
	}

	private void submitText(String text) throws TimeoutException,
			NotConnectedException {
		int length = text.length();

		display.clearDisplay();
		display.backlightOn();
		for (short idx = 0, start = 0; idx < LINES && start < length; idx++, start += WIDTH) {
			String line = text
					.substring(start, Math.min(length, start + WIDTH));
			display.writeLine(idx, (short) 0, line);
		}
	}
}
