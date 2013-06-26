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

package c8y.pi.tinkerforge;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import c8y.pi.driver.Configurable;
import c8y.pi.driver.Driver;
import c8y.pi.driver.Executer;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;

/**
 * Driver for bricklet drivers. Bricklet drivers cannot be registered with the
 * main agent, since they are enumerated at runtime and multiple instances of
 * the same type may exist.
 */
public class TinkerForgeDriver implements Driver, BrickletController, Configurable {
	@Override
	public void addDefaults(Properties props) {
		for (Driver driver : drivers) {
			if (driver instanceof Configurable) {
				Configurable cfg = (Configurable) driver;
				cfg.addDefaults(props);
			}
		}
	}

	@Override
	public void configurationChanged(Properties props) {
		for (Driver driver : drivers) {
			if (driver instanceof Configurable) {
				Configurable cfg = (Configurable) driver;
				cfg.configurationChanged(props);
			}
		}		
	}

	@Override
	public void initialize(Platform platform) throws Exception {

		// Start enumeration -- pass also platform somehow into this!
		// We need to have some way of determining the end of the enumeration
		// still here, otherwise we don't collect all supported operations
	}

	@Override
	public void add(Driver driver) {
		drivers.add(driver);
	}

	@Override
	public Executer[] getSupportedOperations() {
		List<Executer> execs = new ArrayList<Executer>();
		for (Driver driver : drivers) {
			for (Executer exec : driver.getSupportedOperations()) {
				execs.add(exec);
			}
		}

		return execs.toArray(new Executer[execs.size()]);
	}

	@Override
	public void initializeInventory(ManagedObjectRepresentation mo) {
		// Nothing to do here, all bricklets are children.
	}

	@Override
	public void discoverChildren(ManagedObjectRepresentation mo) {
		for (Driver driver : drivers) {
			driver.discoverChildren(mo);
		}
	}

	@Override
	public void start() {
		for (Driver driver : drivers) {
			driver.start();
		}
	}

	private List<Driver> drivers;
}
