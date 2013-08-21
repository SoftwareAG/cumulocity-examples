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

package c8y.rpi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

import c8y.Firmware;
import c8y.lx.driver.Driver;
import c8y.lx.driver.Executer;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.Platform;

public class PiFirmwareDriver implements Driver, Executer {

	private static final String FILE = "raspberrypi-bootloader";
	private static final String FWQUERY = "dpkg-query -l " + FILE;
	private static final int STARTOFVERSION = 42;
	private static final String FWUPDATE = "rpi-update";
	private static final String[] SHUTDOWN = { "shutdown", "-r", "now" };

	private Platform platform;
	private Firmware firmware = new Firmware(FILE, "Unknown version", null);
	private GId gid;

	@Override
	public void initialize(Platform platform) throws Exception {
		this.platform = platform;
		
		String version = "Unknown";

		Process process = Runtime.getRuntime().exec(FWQUERY);
		try (InputStream is = process.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader reader = new BufferedReader(isr)) {

			String line = null, lastLine = null;
			while ((line = reader.readLine()) != null) {
				lastLine = line;
			}

			int endOfVersion = lastLine.indexOf(' ', STARTOFVERSION);
			version = lastLine.substring(STARTOFVERSION, endOfVersion);
		}

		firmware = new Firmware(FILE, version, null);
	}

	@Override
	public Executer[] getSupportedOperations() {
		return new Executer[] { this };
	}

	@Override
	public void initializeInventory(ManagedObjectRepresentation mo) {
		mo.set(firmware);
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
		return "c8y_Firmware";
	}

	@Override
	public void execute(OperationRepresentation operation, boolean cleanup) throws Exception {
		if (!gid.equals(operation.getDeviceId())) {
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
		String shouldBeInstalled = operation.get(Firmware.class).getVersion();
		String isInstalled = firmware.getVersion();
		
		if (isInstalled.equals(shouldBeInstalled)) {
			operation.setStatus(OperationStatus.SUCCESSFUL.toString());
		} else {
			operation.setStatus(OperationStatus.FAILED.toString());
		}
	}

	private void executePending(OperationRepresentation operation) throws Exception {
		Firmware newFirmware = operation.get(Firmware.class);
		String newVersion = newFirmware.getVersion();
		String currentVersion = firmware.getVersion();
		
		if (!newVersion.equals(currentVersion)) {

			ProcessBuilder pb = new ProcessBuilder(FWUPDATE, newFirmware.getUrl());
			pb.redirectErrorStream(true);
			Process p = pb.start();
			
			try (InputStream is = p.getInputStream();
					StringWriter sw = new StringWriter()) {
				IOUtils.copy(is, sw);
				operation.setFailureReason(sw.getBuffer().toString());
				platform.getDeviceControlApi().update(operation);
			}
			
			new ProcessBuilder(SHUTDOWN).start().waitFor();
		}
		operation.setStatus(OperationStatus.SUCCESSFUL.toString());
	}
}
