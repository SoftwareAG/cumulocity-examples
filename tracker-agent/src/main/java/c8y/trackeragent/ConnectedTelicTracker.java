package c8y.trackeragent;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ConnectedTelicTracker extends ConnectedTracker {
	public static final int HEADER_LENGTH = 28;
	public static final int REPORT_SKIP = 4;

	public ConnectedTelicTracker(Socket client, InputStream bis,
			TrackerAgent trackerAgent) throws IOException {
		super(client, eat(bis, HEADER_LENGTH), TelicConstants.REPORT_SEP,
				TelicConstants.FIELD_SEP);
		addFragment(new TelicLocationReport(trackerAgent));
	}

	private static InputStream eat(InputStream bis, int bytesToRead)
			throws IOException {
		byte[] dummy = new byte[bytesToRead];
		int bytesRead = bis.read(dummy, 0, bytesToRead);
		if (bytesRead < bytesToRead) {
			return null;
		}
		return bis;
	}

	@Override
	String readReport(InputStream is) throws IOException {
		if (eat(is, REPORT_SKIP) == null) {
			return null;
		}
		return super.readReport(is);
	}
}
