package c8y.pi.agent;

import java.io.BufferedReader;
import java.io.FileReader;

import c8y.pi.driver.Driver;
import c8y.pi.driver.Executer;

import com.cumulocity.model.dm.Hardware;
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
public class LinuxHardwareDriver implements Driver, Executer {
	public static final String CPUINFO = "/proc/cpuinfo";
	public static final String PATTERN = "\\s+:\\s+";

	@Override
	public void initialize(Platform platform) throws Exception {
		String name = "Unknown hardware", serialNumber = "Unknown serial", revision = "Unknown revision";

		try (FileReader fr = new FileReader(CPUINFO);
				BufferedReader reader = new BufferedReader(fr)) {
			String line = null;

			while ((line = reader.readLine()) != null) {
				String[] keyval = line.split(PATTERN);

				if ("Hardware".equals(keyval[0])) {
					name = keyval[1];
				}
				if ("Revision".equals(keyval[0])) {
					revision = keyval[1];
				}
				if ("Serial".equals(keyval[0])) {
					serialNumber = keyval[1];
				}
			}
		}

		this.hardware = new Hardware(name, serialNumber, revision);
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
	public void execute(OperationRepresentation operation) throws Exception {
		if (!gid.equals(operation.getDeviceId())) {
			// Silently ignore the operation if it is not targeted to us,
			// another driver will (hopefully) care.
			return;
		}

		if (OperationStatus.EXECUTING.toString().equals(operation.getStatus())) {
			operation.setStatus(OperationStatus.SUCCESSFUL.toString());
		} else {
			new ProcessBuilder("shutdown", "-r", "now").start().waitFor();
		}
	}

	private GId gid;
	private Hardware hardware;
}
