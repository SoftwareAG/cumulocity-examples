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

package c8y.tinkerforge;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import c8y.lx.driver.Configurable;
import c8y.lx.driver.Driver;
import c8y.lx.driver.OperationExecutor;
import c8y.tinkerforge.Discoverer.DiscoveryFinishedListener;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;

/**
 * Driver for bricklet drivers. Bricklet drivers cannot be registered directly
 * with the main agent, since they are enumerated at runtime and multiple
 * instances of the same type may exist.
 */
public class TFDriver implements Driver, DiscoveryFinishedListener, Configurable {

	private Iterable<Driver> drivers;

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
    public void initialize() throws Exception {
        new BrickConnector(this);
        
        synchronized (this) {
            if (drivers == null) {
                wait();
            }
        }
        
        for (Driver driver : drivers) {
            driver.initialize();
        }
    }

	@Override
	public void initialize(Platform platform) throws Exception {
		for (Driver driver : drivers) {
			driver.initialize(platform);
		}
	}

	@Override
	public OperationExecutor[] getSupportedOperations() {
		List<OperationExecutor> execs = new ArrayList<>();
		for (Driver driver : drivers) {
			for (OperationExecutor exec : driver.getSupportedOperations()) {
				execs.add(exec);
			}
		}

		return execs.toArray(new OperationExecutor[execs.size()]);
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

	@Override
	public void discoveredDevices(Iterable<Driver> drivers) {
		synchronized (this) {
			this.drivers = drivers;
			notify();
		}
	}
}
