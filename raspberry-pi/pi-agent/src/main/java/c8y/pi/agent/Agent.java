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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import c8y.pi.driver.ConfigurationListener;
import c8y.pi.driver.Driver;
import c8y.pi.driver.Executer;

import com.cumulocity.model.ID;
import com.cumulocity.model.dm.Hardware;
import com.cumulocity.model.dm.SupportedOperations;
import com.cumulocity.model.event.CumulocityAlarmStatuses;
import com.cumulocity.model.event.CumulocitySeverities;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.SDKException;

/**
 * <p>
 * Main executable agent class. Discovers installed drivers, manages the
 * drivers' life cycles and reports errors during startup as alarm (if
 * possible). Credentials for connecting to the platform are expected in a
 * configuration file, by default <code>/etc/cumulocity.properties</code>.
 * </p>
 * <p>
 * Working directory should be the directory with the installed jars for
 * software management to work.
 * </p>
 * 
 * @see {@link Driver}, {@link CredentialManager}
 */
public class Agent {
	public static final String TYPE = "c8y_RaspberryPi";
	public static final String XTIDTYPE = "c8y_Serial";
	public static final String ALARMTYPE = "c8y_AgentStartupError";

	public static void main(String[] args) throws IOException, SDKException {
		new Agent();
	}

	public Agent() throws IOException, SDKException {
		Credentials creds = new CredentialManager().getCredentials();
		platform = new PlatformImpl(creds.getHost(), creds.getTenant(),
				creds.getUser(), creds.getPassword(), creds.getKey());

		try {
			// See {@link Driver} for an explanation of the driver life cycle.
			initializeDrivers();
			initializeInventory();
			discoverChildren();
			startDrivers();
			new OperationDispatcher(platform, mo.getId(), dispatchMap);
		} catch (SDKException e) {
			errorLog.add(e);
		}

		if (!errorLog.isEmpty() && mo.getId() != null) {
			logError(errorLog.toString());
		}
	}

	private void initializeDrivers() {
		ConfigurationDriver cfgDriver = null;

		for (Driver driver : ServiceLoader.load(Driver.class)) {
			try {
				driver.initialize(platform);
				drivers.add(driver);

				if (driver instanceof ConfigurationDriver) {
					cfgDriver = (ConfigurationDriver) driver;
				}
			} catch (Exception e) {
				errorLog.add(e);
			}
		}

		/*
		 * ConfigurationDriver notifies other drivers of changes in
		 * configuration if they implement ConfigurationListener.
		 */
		if (cfgDriver != null) {
			for (Driver driver : drivers) {
				if (driver instanceof ConfigurationListener) {
					cfgDriver
							.addConfigurationListener((ConfigurationListener) driver);
				}
			}
		}
	}

	private void initializeInventory() throws SDKException {
		SupportedOperations supportedOps = new SupportedOperations();
		
		for (Driver driver : drivers) {
			driver.initializeInventory(mo);

			for (Executer exec : driver.getSupportedOperations()) {
				String supportedOp = exec.supportedOperationType();
				dispatchMap.put(supportedOp, exec);
				supportedOps.add(supportedOp);
			}
		}
		
		mo.set(supportedOps);

		String serial = mo.get(Hardware.class).getSerialNumber();
		String id = "raspberrypi-" + serial;
		String defaultName = "Raspberry Pi " + serial;

		ID extId = new ID(id);
		extId.setType(XTIDTYPE);
		DeviceManagedObject dmo = new DeviceManagedObject(platform, extId);
		dmo.createOrUpdate(mo, defaultName);
	}

	private void discoverChildren() {
		for (Driver driver : drivers) {
			driver.discoverChildren(mo);
		}
	}

	private void startDrivers() {
		for (Driver driver : drivers) {
			driver.start();
		}
	}

	private void logError(String error) throws SDKException {
		AlarmRepresentation alarm = new AlarmRepresentation();
		alarm.setType(ALARMTYPE);
		alarm.setSource(mo);
		alarm.setSeverity(CumulocitySeverities.MAJOR.toString());
		alarm.setStatus(CumulocityAlarmStatuses.ACTIVE.toString());
		alarm.setText(error);
		alarm.setTime(new Date());
		platform.getAlarmApi().create(alarm);
	}

	private List<Driver> drivers = new ArrayList<Driver>();
	private Platform platform;
	private ManagedObjectRepresentation mo = new ManagedObjectRepresentation();
	private Map<String, Executer> dispatchMap = new HashMap<String,Executer>();
	private ErrorLog errorLog = new ErrorLog();
}
