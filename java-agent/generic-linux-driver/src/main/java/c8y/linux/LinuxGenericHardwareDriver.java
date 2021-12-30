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
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Objects;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
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
	public static final String GET_INTERFACES = "ifconfig";
	public static final String PATTERN = "\\s+";

	private static Logger logger = LoggerFactory
			.getLogger(LinuxGenericHardwareDriver.class);

	private GId gid;
	private final Hardware hardware = new Hardware("Linux MAC", UNKNOWN, UNKNOWN);

    @Override
    public void initialize() throws Exception {
        if(!initializeFromProcess(GET_INTERFACES)) {
        	initializeUsingNetworkInterface();
		}
    }

	@Override
	public void initialize(Platform platform) {
	    // Nothing to do here.
	}

	private boolean initializeFromProcess(String process) {
		InputStream is = null;
		InputStreamReader ir = null;
    	try {
			Process p = Runtime.getRuntime().exec(process);
			is = p.getInputStream();
			ir = new InputStreamReader(is);
			return initializeFromReader(ir);
		} catch (Exception e) {
    		logger.warn(e.getMessage());
		} finally {
    		IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(ir);
		}
		return false;
	}

	boolean initializeFromReader(Reader r) throws IOException {
		/*
		 * Eclipse prints a warning here, however the construct with return
		 * should be legal according to http: //
		 * docs.oracle.com/javase/tutorial/
		 * essential/exceptions/tryResourceClose.html
		 */
		try (@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(r)) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] fields = line.trim().split(PATTERN);

				for (int i = 0; i < fields.length; i++) {
					if ("HWaddr".equals(fields[i]) && i + 1 < fields.length) {
						hardware.setSerialNumber(fields[i + 1].replace(":", ""));
						return true;
					}
				}
			}
		}
		return false;
	}

	private void initializeUsingNetworkInterface() throws SocketException {
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		if (interfaces.hasMoreElements()) {
			NetworkInterface networkInterface = interfaces.nextElement();
			byte[] mac = networkInterface.getHardwareAddress();
			if (Objects.nonNull(mac)) {
				String address = Hex.encodeHexString(mac).toUpperCase();
				hardware.setSerialNumber(address);
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
