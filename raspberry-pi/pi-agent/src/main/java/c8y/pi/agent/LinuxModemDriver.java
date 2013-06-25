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

public class LinuxModemDriver extends TimerTask implements Driver,
		Configurable {
	public static final String PORT = "/dev/ttyUSB0";

	public static final String GET_IMEI = "AT+GSN";
	public static final String GET_CELLID = "AT+CSCI";
	public static final String GET_ICCID = "AT^SCID";
	public static final String GET_SIGNAL = "AT+CSQ";

	public static final String POLLING_PROP = "c8y.pi.agent.SignalStrengthPolling";
	public static final long POLLING_INTERVAL = 1000;
	
	public static final double[] BER_TABLE = { 0.14, 0.28, 0.57, 1.13, 2.26, 4.53, 9.05, 18.10 };			

	@Override
	public void addDefaults(Properties props) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configurationChanged(Properties props) {
		try {
			String intervalStr = props.getProperty(POLLING_PROP);
			this.pollingInterval = Long.parseLong(intervalStr);
		} catch (NumberFormatException x) {
			this.pollingInterval = POLLING_INTERVAL;
		}
	}

	@Override
	public void initialize(Platform platform) throws Exception {
		this.measurements = platform.getMeasurementApi();
		this.port = new SerialPort(PORT);
		this.pollingInterval = POLLING_INTERVAL;

		try {
			port.openPort();

			String imei = command(GET_IMEI);
			String cellId = command(GET_CELLID);
			String iccid = command(GET_ICCID);
			mobile = new Mobile(imei, cellId, iccid);

			port.closePort();
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

		long now = new Date().getTime();
		Date firstPolling = computeFirstPolling(now, pollingInterval / 1000);

		Timer timer = new Timer("SignalStrengthPoller");
		timer.scheduleAtFixedRate(this, firstPolling, pollingInterval);
	}

	private String command(String command) throws SerialPortException {
		port.writeString(command + "\r\n");
		readLine(); // Consume echo
		port.readBytes(1); // Consume one additional lf from echo
		
		String answer = readLine();
		if ("ERROR".equals(answer)) {
			return null;
		}
		if (answer.startsWith("COMMAND NOT SUPPORT")) {
			return null;
		}
		
		readLine(); // Consume blank line
		readLine(); // Consume "OK"

		return answer;
	}

	private String readLine() throws SerialPortException {
		byte[] input;
		StringBuffer line = new StringBuffer();

		while ((char)(input = port.readBytes(1))[0] != '\r') {
			line.append((char)input[0]);
		}
		char cr = (char)port.readBytes(1)[0]; // Consume "\n"

		return line.toString();
	}

	@Override
	public void run() {
		try {
			logger.debug("Getting modem signal measurements");
			String csq = command(GET_SIGNAL);
			int startOfRssi = csq.indexOf(' ') + 1;
			int startOfBer = csq.indexOf(',') + 1;

			String rssiStr = csq.substring(startOfRssi, startOfBer - 1);
			String berStr = csq.substring(startOfBer);
			
			int rssiVal = - 53 - (30 - Integer.parseInt(rssiStr)) * 2;
			rssi.setValue(new BigDecimal(rssiVal));
			
			double berVal = BER_TABLE[Integer.parseInt(berStr)];
			ber.setValue(new BigDecimal(berVal));
			
			measurement.setTime(new Date());
			measurements.create(measurement);
		} catch (SDKException | SerialPortException e) {
			e.printStackTrace();
		}
	}

	static Date computeFirstPolling(long time, long pollingInterval) {
		return new Date(time / pollingInterval * pollingInterval
				+ pollingInterval);
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
	
	private static Logger logger = LoggerFactory.getLogger(LinuxModemDriver.class);

	private MeasurementApi measurements;
	private SerialPort port;
	private Mobile mobile;
	private GId gid;
	private long pollingInterval;
	private MeasurementRepresentation measurement;
	private MeasurementValue rssi, ber;
}
