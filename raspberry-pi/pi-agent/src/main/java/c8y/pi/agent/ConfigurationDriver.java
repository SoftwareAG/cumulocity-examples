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

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.pi.driver.Configurable;
import c8y.pi.driver.Driver;
import c8y.pi.driver.Executer;

import com.cumulocity.model.dm.Configuration;
import com.cumulocity.model.idtype.GId;
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
		fromFile(CONFIGFILE, props);
		Configuration configuration = new Configuration(toString(props));
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
	public void execute(OperationRepresentation operation) throws Exception {
		if (!gid.equals(operation.getDeviceId())) {
			// Silently ignore the operation if it is not targeted to us,
			// another driver will (hopefully) care.
			return;
		}

		Configuration configuration = (Configuration) operation
				.get(Configuration.class);
		props = fromString(configuration.getConfig());

		for (Configurable configurable : configurables) {
			configurable.configurationChanged(props);
		}

		logger.info("Configuration set, updating inventory");
		ManagedObjectRepresentation mo = new ManagedObjectRepresentation();
		mo.setId(gid);
		mo.set(configuration);
		inventory.getManagedObject(gid).update(mo);

		String error = toFile(props, CONFIGFILE);
		operation.setFailureReason(error);
	}
	
	Properties getProperties() {
		return props;
	}

	public static Properties fromString(String propsStr) throws IOException {
		Properties result = new Properties();
		try (StringReader reader = new StringReader(propsStr)) {
			result.load(reader);
			logger.debug("Read configuration: " + result);
		}
		return result;
	}

	public static String toString(Properties props) {
		String result = "";
		try (StringWriter configWriter = new StringWriter()) {
			props.store(configWriter, null);
			result = configWriter.getBuffer().toString();
		} catch (IOException iox) {
			// Storing in a String shouldn't cause I/O exception
			logger.warn("Bogus IOException", iox);
		}
		return result;
	}

	public static void fromFile(String file, Properties props) {
		try (FileReader reader = new FileReader(file)) {
			props.load(reader);
			logger.debug("Read configuration file, current configuration: " + props);
		} catch (IOException iox) {
			logger.warn("Configuration file {} cannot be read, assuming empty configuration", file);
		}
	}
	
	public static String toFile(Properties props, String file) {
		try (FileWriter writer = new FileWriter(file)) {
			props.store(writer, null);
		} catch (IOException iox) {
			logger.warn("Configuration file {} cannot be written", file);
			return ErrorLog.toString(iox);
		}
		return null;
	}

	private static Logger logger = LoggerFactory.getLogger(ConfigurationDriver.class);
	
	private InventoryApi inventory;
	private Properties props = new Properties();
	private List<Configurable> configurables = new ArrayList<Configurable>();
	private GId gid;
}
