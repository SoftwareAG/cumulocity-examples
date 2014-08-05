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
