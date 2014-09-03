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

package c8y.linux;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.Hardware;
import c8y.lx.driver.HardwareProvider;
import c8y.lx.driver.Driver;
import c8y.lx.driver.OperationExecutor;
import c8y.lx.driver.OpsUtil;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.Platform;

public class LinuxGenericHardwareDriver implements Driver, OperationExecutor, HardwareProvider{
	public static final String GETINTERFACES = "ifconfig";
	public static final String PATTERN = "\\s+";

	private static Logger logger = LoggerFactory
			.getLogger(LinuxGenericHardwareDriver.class);

	private GId gid;
	private final Hardware hardware = new Hardware("Linux MAC", UNKNOWN, UNKNOWN);
	
    @Override
    public void initialize() throws Exception {
        initializeFromProcess(GETINTERFACES);
    }

	@Override
	public void initialize(Platform platform) throws Exception {
	    // Nothing to do here.
	}

	private void initializeFromProcess(String process) throws Exception {
		Process p = Runtime.getRuntime().exec(process);
		try (InputStream is = p.getInputStream();
				InputStreamReader ir = new InputStreamReader(is)) {
			initializeFromReader(ir);
		}
	}

	void initializeFromReader(Reader r) throws IOException {
		/*
		 * Eclipse prints a warning here, however the construct with return
		 * should be legal according to http: //
		 * docs.oracle.com/javase/tutorial/
		 * essential/exceptions/tryResourceClose.html
		 */
		try (@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(r)) {
			String line = null;

			while ((line = reader.readLine()) != null) {
				String[] fields = line.trim().split(PATTERN);

				for (int i = 0; i < fields.length; i++) {
					if ("HWaddr".equals(fields[i]) && i + 1 < fields.length) {
						hardware.setSerialNumber(fields[i + 1].replace(":", ""));
						return;
					}
				}
			}
		}
	}

	@Override
	public OperationExecutor[] getSupportedOperations() {
		return new OperationExecutor[] { this };
	}

	@Override
	public void initializeInventory(ManagedObjectRepresentation mo) {
		mo.set(hardware);
		OpsUtil.addSupportedOperation(mo, supportedOperationType());
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
		return "c8y_Restart";
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
			operation.setStatus(OperationStatus.SUCCESSFUL.toString());
		} else {
			logger.info("Shutting down");
			new ProcessBuilder("shutdown", "-r", "now").start().waitFor();
		}
	}

	@Override
	public Hardware getHardware() {
		return hardware;
	}
}
