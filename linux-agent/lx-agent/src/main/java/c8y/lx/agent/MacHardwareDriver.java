package c8y.lx.agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.Hardware;
import c8y.lx.driver.Driver;
import c8y.lx.driver.Executer;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.Platform;

/**
 * A driver that simply reports the currently installed hardware information to
 * the platform. It also enables restarting a device.
 * 
 * TODO The restart functionality could probably better go into an OS driver
 * that also reports syslog alerts and enables updating the OS software.
 */
public class MacHardwareDriver implements Driver, Executer {
	public static final String MACINFO = "system_profiler SPHardwareDataType";
	public static final String PATTERN = ":\\s+";

	private static Logger logger = LoggerFactory
			.getLogger(MacHardwareDriver.class);

	private GId gid;
	private Hardware hardware = new Hardware("Unknown model", "Unknown serial",
			"Unknown revision");

	@Override
	public void initialize(Platform platform) throws Exception {
		initializeFromProcess(MACINFO);
	}

	private void initializeFromProcess(String process) throws Exception {
		Process p = Runtime.getRuntime().exec(process);
		try (InputStream is = p.getInputStream();
				InputStreamReader ir = new InputStreamReader(is)) {
			initializeFromReader(ir);
		}
	}

	void initializeFromReader(Reader r) throws IOException {
		try (BufferedReader reader = new BufferedReader(r)) {
			String line = null;

			while ((line = reader.readLine()) != null) {
				String[] keyval = line.trim().split(PATTERN);

				if ("Model Name".equals(keyval[0])) {
					hardware.setModel(keyval[1]);
				}
				if ("Model Identifier".equals(keyval[0])) {
					hardware.setRevision(keyval[1]);
				}
				if ("Serial Number (system)".equals(keyval[0])) {
					hardware.setSerialNumber(keyval[1]);
				}
			}
		}
	}

	@Override
	public Executer[] getSupportedOperations() {
		return new Executer[] { this };
	}

	@Override
	public void initializeInventory(ManagedObjectRepresentation mo) {
		mo.set(hardware);
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

	public Hardware getHardware() {
		return hardware;
	}
}
