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
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.pi.driver.Configurable;
import c8y.pi.driver.Driver;
import c8y.pi.driver.Executer;

import com.cumulocity.model.dm.Mobile;
import com.cumulocity.model.dm.Signal;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.measurement.MeasurementValue;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.measurement.MeasurementApi;

public class LinuxModemDriver extends TimerTask implements Driver, Configurable {
	public static final String PORT = "/dev/ttyUSB0";

	public static final String GET_IMEI = "AT+GSN";
	public static final String GET_CELLID = "AT+CSCI";
	public static final String GET_ICCID = "AT^SCID";
	public static final String GET_SIGNAL = "AT+CSQ";

	public static final String POLLING_PROP = "c8y.modem.signalPolling";
	public static final long POLLING_INTERVAL = 1000;

	public static final double[] BER_TABLE = { 0.14, 0.28, 0.57, 1.13, 2.26,
			4.53, 9.05, 18.10 };

	@Override
	public void addDefaults(Properties props) {
		props.setProperty(POLLING_PROP, Long.toString(POLLING_INTERVAL));
	}

	@Override
	public void configurationChanged(Properties props) {
		try {
			String intervalStr = props.getProperty(POLLING_PROP);
			pollingInterval = Long.parseLong(intervalStr);
			if (timer != null) {
				timer.cancel();
				scheduleMeasurements();
			}
		} catch (NumberFormatException x) {
			this.pollingInterval = POLLING_INTERVAL;
		}
	}

	@Override
	public void initialize(Platform platform) throws Exception {
		this.measurements = platform.getMeasurementApi();
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
	public Executer[] getSupportedOperations() {
		// TODO Enable modem configuration, i.e., APN, user name, password
		// setting
		return new Executer[] {};
	}

	@Override
	public void initializeInventory(ManagedObjectRepresentation mo) {
		if (mobile != null) {
			mo.set(mobile);
		}
	}

	@Override
	public void discoverChildren(ManagedObjectRepresentation mo) {
		this.gid = mo.getId();
	}

	@Override
	public void start() {
		if (mobile == null) {
			return;
		}

		createMeasurementTemplate();
		scheduleMeasurements();
	}

	private void createMeasurementTemplate() {
		measurement = new MeasurementRepresentation();
		measurement.setType("c8y_SignalStrength");

		ManagedObjectRepresentation mo = new ManagedObjectRepresentation();
		mo.setId(gid);
		measurement.setSource(mo);

		rssi = new MeasurementValue();
		rssi.setUnit("dB");

		ber = new MeasurementValue();
		ber.setUnit("%");

		Signal signalMeasurement = new Signal();
		signalMeasurement.put("rssi", rssi);
		signalMeasurement.put("ber", ber);

		measurement.set(signalMeasurement);
	}

	private void scheduleMeasurements() {
		long now = new Date().getTime();
		Date firstPolling = computeFirstPolling(now, pollingInterval / 1000);

		timer = new Timer("SignalStrengthPoller");
		timer.scheduleAtFixedRate(this, firstPolling, pollingInterval);
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private void digest(String line) {
			if ("OK".equals(line)) {
				return;
			}

			if (command == null) {
				command = line;
				return;
			}

			if (!line.startsWith("ERROR") && !line.startsWith("COMMAND NOT")
					&& !line.startsWith("+CME ")) {
				if (GET_IMEI.equals(command)) {
					mobile.setImei(line);
				}
				if (GET_CELLID.equals(command)) {
					mobile.setCellId(line);
				}
				if (GET_ICCID.equals(command)) {
					mobile.setIccid(line);
				}
				if (GET_SIGNAL.equals(command)) {
					sendSignalMeasurement(line);
				}
			}
			command = null;
		}

		private String command = null;
	}

	@Override
	public void run() {
		logger.debug("Getting modem signal measurements");
		run(GET_SIGNAL);
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

		measurement.setTime(new Date());
		try {
			measurements.create(measurement);
		} catch (SDKException e) {
			logger.warn("Cannot create signal measurement", e);
		}
	}

	static Date computeFirstPolling(long time, long pollingInterval) {
		return new Date(time / pollingInterval * pollingInterval
				+ pollingInterval);
	}

	private static Logger logger = LoggerFactory
			.getLogger(LinuxModemDriver.class);

	private MeasurementApi measurements;
	private SerialPort port;
	private Mobile mobile;
	private GId gid;
	private long pollingInterval = POLLING_INTERVAL;
	private Timer timer;
	private MeasurementRepresentation measurement;
	private MeasurementValue rssi, ber;
}
