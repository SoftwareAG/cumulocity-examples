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

package c8y.lx.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.Configuration;
import c8y.lx.driver.Configurable;
import c8y.lx.driver.Driver;
import c8y.lx.driver.Executer;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.inventory.InventoryApi;

/**
 * This class implements driver configuration in the form of a properties file
 * that can be remotely updated from the platform. At startup and when a change
 * in configuration is requested, the current configuration is passed to all
 * drivers that implement the Configurable interface.
 */
public class ConfigurationDriver implements Driver, Executer {
	
	public static final String CONFIGFILE = "/etc/cumulocity-configuration.properties";

	private static Logger logger = LoggerFactory
			.getLogger(ConfigurationDriver.class);

	private InventoryApi inventory;
	private Properties props = new Properties();
	private List<Configurable> configurables = new ArrayList<Configurable>();
	private GId gid;

	@Override
	public void initialize(Platform platform) throws Exception {
		this.inventory = platform.getInventoryApi();
	}

	public void addConfigurable(Configurable configurable) {
		configurable.addDefaults(props);
		configurables.add(configurable);
	}

	@Override
	public Executer[] getSupportedOperations() {
		return new Executer[] { this };
	}

	@Override
	public void initializeInventory(ManagedObjectRepresentation mo) {
		/*
		 * At this point in time, props contains the defaults from the devices.
		 * Merge with the stored configuration, notify others of potential
		 * updates and send to inventory.
		 */
		PropUtils.fromFile(CONFIGFILE, props);
		notifyConfigurationUpdate();

		Configuration configuration = new Configuration(
				PropUtils.toString(props));
		mo.set(configuration);
	}

	@Override
	public void discoverChildren(ManagedObjectRepresentation mo) {
		this.gid = mo.getId();
	}

	@Override
	public void start() {
		// Nothing to do here.
	}

	@Override
	public String supportedOperationType() {
		return "c8y_Configuration";
	}

	@Override
	public void execute(OperationRepresentation operation, boolean cleanup)
			throws Exception {
		if (!gid.equals(operation.getDeviceId())) {
			// Silently ignore the operation if it is not targeted to us,
			// another driver will (hopefully) care.
			return;
		}

		if (cleanup) {
			// The operation was interrupted somehow.
			operation.setStatus(OperationStatus.FAILED.toString());
			return;
		}

		Configuration configuration = (Configuration) operation
				.get(Configuration.class);
		props = PropUtils.fromString(configuration.getConfig());

		notifyConfigurationUpdate();

		logger.info("Configuration set, updating inventory");
		ManagedObjectRepresentation mo = new ManagedObjectRepresentation();
		mo.set(configuration);
		inventory.getManagedObject(gid).update(mo);

		String error = PropUtils.toFile(props, CONFIGFILE);
		operation.setFailureReason(error);
		operation.setStatus(OperationStatus.SUCCESSFUL.toString());
	}

	private void notifyConfigurationUpdate() {
		for (Configurable configurable : configurables) {
			configurable.configurationChanged(props);
		}
	}

	Properties getProperties() {
		return props;
	}
}
