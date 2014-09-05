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

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.Software;
import c8y.lx.driver.Driver;
import c8y.lx.driver.OperationExecutor;
import c8y.lx.driver.OpsUtil;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.Platform;

/**
 * A software manager that permits remote update of the agent software and
 * drivers from the platform. Works with the jar files installed in the current
 * directory. After a successful update, the driver terminates the agent process
 * and finishes the update at next startup of the agent (through a wrapper script).
 */
public class JavaSoftwareDriver implements Driver, OperationExecutor {

	private static final String DOWNLOADING = ".download";
	private static Logger logger = LoggerFactory.getLogger(JavaSoftwareDriver.class);

	private Software software = new Software();

	private GId deviceId;
	
    @Override
    public void initialize() throws Exception {
        new File(".").listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String file) {
                if (file.matches(".*[.]jar$")) {
                    String[] nameVer = file.split("-[0-9]+[.]");
                    software.put(nameVer[0], file);
                }
                return false;
            }
        });
    }


	@Override
	public void initialize(Platform platform) throws Exception {
	    // Nothing to do here.
	}

	@Override
	public OperationExecutor[] getSupportedOperations() {
		return new OperationExecutor[] { this };
	}

	@Override
	public void initializeInventory(ManagedObjectRepresentation mo) {
		mo.set(software);
		OpsUtil.addSupportedOperation(mo, supportedOperationType());
	}

	@Override
	public void discoverChildren(ManagedObjectRepresentation mo) {
		this.deviceId = mo.getId();
	}

	@Override
	public void start() {
		// Nothing to do here.
	}

	@Override
	public String supportedOperationType() {
		return "c8y_Software";
	}

	@Override
	public void execute(OperationRepresentation operation, boolean cleanup) throws Exception {
		if (!deviceId.equals(operation.getDeviceId())) {
			// Silently ignore the operation if it is not targeted to us,
			// another driver will (hopefully) care.
			return;
		}

		if (cleanup) {
			finishExecuting(operation);
		} else {
			executePending(operation);
		}
	}

	private void finishExecuting(OperationRepresentation operation) {
		logger.info("Checking installed software base after update");
		Software shouldBeInstalled = operation.get(Software.class);

		OperationStatus status = OperationStatus.SUCCESSFUL;

		if (shouldBeInstalled.size() != software.size()) {
			status = OperationStatus.FAILED;
		} else {
			for (Entry<String, String> pkg : software.entrySet()) {
				String isInstalledFile = pkg.getValue();
				String shouldBeInstalledFile = shouldBeInstalled.get(pkg
						.getKey());
				if (!equals(shouldBeInstalledFile, isInstalledFile)) {
					logger.warn("Software {} was not installed", shouldBeInstalledFile);
					status = OperationStatus.FAILED;
					break;
				}
			}
		}

		operation.setStatus(status.toString());
	}

	private void executePending(OperationRepresentation operation)
			throws IOException {
		Software toBeInstalled = (Software) operation.get(Software.class)
				.clone();
		Software toBeRemoved = new Software();

		for (Entry<String, String> currentPkg : software.entrySet()) {
			String software = currentPkg.getKey();
			String version = currentPkg.getValue();
			String newVersion = toBeInstalled.get(software);

			if (newVersion == null) {
				toBeRemoved.put(software, version);
			} else if (equals(newVersion, version)) {
				toBeInstalled.remove(version);
			}
		}

		download(toBeInstalled);
		rename(toBeInstalled);
		remove(toBeRemoved);
		System.exit(0); // Delete files and restart process through watcher

	}

	private boolean equals(String newVersion, String version) {
		try {
			return new URL(newVersion).getFile().equals(version);
		} catch (MalformedURLException e) {
			return false;
		}
	}

	private void download(Software toBeInstalled) throws MalformedURLException {
		for (String pkg : toBeInstalled.values()) {
			logger.debug("Downloading " + pkg);
			URL url = new URL(pkg);
			String file = url.getFile() + DOWNLOADING;
			try (InputStream os = url.openStream();
					ReadableByteChannel rbc = Channels.newChannel(os);
					FileOutputStream fos = new FileOutputStream(file);) {
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			} catch (IOException iox) {
				logger.warn("Failed downloading " + pkg);
			}
		}
	}

	private void rename(Software toBeInstalled) throws MalformedURLException {
		for (String pkg : toBeInstalled.values()) {
			URL url = new URL(pkg);
			File downloaded = new File(url.getFile() + DOWNLOADING);
			File target = new File(url.getFile());
			downloaded.renameTo(target);
		}
	}

	private void remove(Software toBeRemoved) {
		for (String pkg : toBeRemoved.values()) {
			logger.debug("Removing " + pkg);
			new File(pkg).deleteOnExit();
		}
	}
}
