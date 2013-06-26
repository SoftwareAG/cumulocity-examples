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

package c8y.pi.agent;

import java.math.BigDecimal;
import java.util.Map;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.pi.driver.PollingDriver;

import com.cumulocity.model.dm.Mobile;
import com.cumulocity.model.measurement.MeasurementValue;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;

public class LinuxModemDriver extends PollingDriver {
	public static final String PORT = "/dev/ttyUSB0";

	public static final String GET_IMEI = "AT+GSN";
	public static final String GET_CELLID = "AT+CSCI";
	public static final String GET_ICCID = "AT^SCID";
	public static final String GET_SIGNAL = "AT+CSQ";

	public static final double[] BER_TABLE = { 0.14, 0.28, 0.57, 1.13, 2.26,
			4.53, 9.05, 18.10 };
	
	public LinuxModemDriver() {
		super("c8y_SignalStrength", "c8y.modem.signalPolling", 5000L);
	}

	@Override
	public void initialize(Platform platform) throws Exception {
		super.initialize(platform);
		
		this.port = new SerialPort(PORT);
		try {
			port.openPort();
			mobile = new Mobile();
			port.addEventListener(new ResultListener(), SerialPort.MASK_RXCHAR);
			run(GET_IMEI);
			run(GET_CELLID);
			run(GET_ICCID);
		} catch (SerialPortException ex) {
			logger.info("Cannot connect to modem.");
			logger.trace("Cannot open modem port", ex);
		}
	}

	@Override
	public void initializeInventory(ManagedObjectRepresentation mo) {		
		super.setSource(mo);
		if (mobile != null) {
			synchronized (mobile) {
				try {
					while (receivedCommands < 3) {
						mobile.wait();
					}
				} catch (InterruptedException e) {
					logger.warn("Spurious exception", e);
				}
			}
			mo.set(mobile);
		}
	}

	@Override
	protected void createMeasurementTemplate(Map<String,MeasurementValue> measurement) {
		rssi.setUnit("dB");
		ber.setUnit("%");
		measurement.put("rssi", rssi);
		measurement.put("ber", ber);
	}
	
	@Override
	public void run() {
		if (mobile == null) {
			return;
		}

		logger.debug("Getting modem signal measurements");
		run(GET_SIGNAL);
	}

	public void run(String command) {
		try {
			port.writeString(command + "\r\n");
		} catch (SerialPortException e) {
			logger.warn("Error sending command {} to modem", command, e);
		}
	}

	class ResultListener implements SerialPortEventListener {
		@Override
		public void serialEvent(SerialPortEvent event) {
			try {
				String[] lines = port.readString().split("\\n+");
				for (String line : lines) {
					digest(line);
				}
			} catch (SerialPortException e) {
				logger.warn("Reading from serial port failed", e);
			}
		}

		private void digest(String line) {
			if ("".equals(line) || "OK".equals(line)) {
				return;
			}

			if (line.startsWith("AT")) {
				command = line;
				receivedCommands++;
				return;
			}

			if (!line.startsWith("ERROR") && !line.startsWith("COMMAND NOT")
					&& !line.startsWith("+CME ")) {
				if (GET_IMEI.equals(command)) {
					mobile.setImei(line);
				} else if (GET_CELLID.equals(command)) {
					mobile.setCellId(line);
				} else if (GET_ICCID.equals(command)) {
					String iccid = line.split(" ")[1];
					mobile.setIccid(iccid);
				} else if (GET_SIGNAL.equals(command)) {
					sendSignalMeasurement(line);
				}
			}
			command = null;
			if (receivedCommands >= 3) {
				synchronized (mobile) {
					mobile.notify();
				}
			}
		}

		private String command = null;
	}

	public void sendSignalMeasurement(String line) {
		int startOfRssi = line.indexOf(' ') + 1;
		int startOfBer = line.indexOf(',') + 1;

		String rssiStr = line.substring(startOfRssi, startOfBer - 1);
		String berStr = line.substring(startOfBer);

		int rssiVal = -53 - (30 - Integer.parseInt(rssiStr)) * 2;
		rssi.setValue(new BigDecimal(rssiVal));

		double berVal = BER_TABLE[Integer.parseInt(berStr)];
		ber.setValue(new BigDecimal(berVal));

		super.sendMeasurement();
	}

	private static Logger logger = LoggerFactory
			.getLogger(LinuxModemDriver.class);

	private SerialPort port;
	private Mobile mobile;
	private int receivedCommands = 0;
	private MeasurementValue rssi = new MeasurementValue();
	private MeasurementValue ber = new MeasurementValue();
}
