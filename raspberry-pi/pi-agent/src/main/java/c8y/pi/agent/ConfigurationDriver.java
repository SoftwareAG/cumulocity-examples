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
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import c8y.pi.driver.ConfigurationListener;
import c8y.pi.driver.Driver;
import c8y.pi.driver.Executer;

import com.cumulocity.model.dm.Configuration;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.Platform;

/**
 * This class implements driver configuration in the form of a properties file
 * that can be remotely updated from the platform. At startup and when a change
 * in configuration is requested, the current configuration is passed to all
 * drivers that implement the ConfigurationListener interface.
 */
public class ConfigurationDriver implements Driver, Executer {
	public static final String CONFIGFILE = "/etc/cumulocity-configuration.properties";

	@Override
	public void initialize(Platform platform) throws Exception {
		try (FileReader reader = new FileReader(CONFIGFILE)) {
			props.load(reader);
		}
	}

	@Override
	public Executer[] getSupportedOperations() {
		return new Executer[] { this };
	}

	@Override
	public void initializeInventory(ManagedObjectRepresentation mo) {
		String config = "";
		try (StringWriter configWriter = new StringWriter()) {
			props.store(configWriter, null);
			config = configWriter.getBuffer().toString();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Configuration configuration = new Configuration(config);
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

	public void addConfigurationListener(ConfigurationListener listener) {
		configListeners.add(listener);
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

		String config = configuration.getConfig();
		try (FileWriter fw = new FileWriter(CONFIGFILE); 
				PrintWriter writer = new PrintWriter(fw)) {
			writer.println(configuration.getConfig());
		}

		props = new Properties();
		try (StringReader reader = new StringReader(config)) {
			props.load(reader);
		}

		for (ConfigurationListener listener : configListeners) {
			listener.configurationChanged(props);
		}
	}

	private Properties props = new Properties();
	private List<ConfigurationListener> configListeners = new ArrayList<ConfigurationListener>();
	private GId gid;
}
