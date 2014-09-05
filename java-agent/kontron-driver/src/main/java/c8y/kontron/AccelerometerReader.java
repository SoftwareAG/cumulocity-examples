package c8y.kontron;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccelerometerReader {
	public static final String ACCEL_SCALE = "/sys/class/iio/device0/accel_full_scale";
	public static final String ACCEL_DATA = "/sys/class/iio/device0/accel_xyz";
	
	private static final int X = 1;
	private static final int Y = 2;
	private static final int Z = 3;

	private static final int G2_SCALE = 16384;
	private static final int G2 = 2;
	private static final int G4_SCALE = 8192;
	private static final int G4 = 4;
	private static final int G8_SCALE = 4096;
	private static final int G8 = 8;

	private static Logger logger = LoggerFactory
			.getLogger(AccelerometerReader.class);

	private double threshold;
	private double scale = -1;

	public AccelerometerReader(double thresholdDefault) {
		this.threshold = thresholdDefault;
	}

	public void setThreshold(double newThreshold) {
		this.threshold = newThreshold;
	}

	public boolean poll() throws IOException {
		return poll(ACCEL_DATA);
	}
	
	public boolean poll(String file) throws IOException {
		String line = readLineFromFile(file);
		if (line != null && scale > 0) {
			String[] values = line.split(" ");
			double x = Double.parseDouble(values[X].trim()) / getScale();
			double y = Double.parseDouble(values[Y].trim()) / getScale();
			double z = Double.parseDouble(values[Z].trim()) / getScale();
			logger.debug("Accelerometer reading is " + x + ", " + y + ", " + z);

			return Math.abs(x) > threshold || Math.abs(y) > threshold
					|| Math.abs(z) > threshold;
		}

		return false;		
	}

	public double getScale() throws IOException {
		if (scale <= 0) {
			initializeScale(ACCEL_SCALE);
		}
		return scale;
	}

	void initializeScale(String scaleFile) throws IOException {
		String line = readLineFromFile(scaleFile);
		scale = convertScale(line.trim());
		logger.debug("Scale is " + scale);
	}

	double convertScale(String line) {
		if (line != null && line.length() > 0) {
			int scaleRaw = Integer.parseInt(line);

			switch (scaleRaw) {
			case G8:
				return G8_SCALE;
			case G4:
				return G4_SCALE;
			case G2:
				return G2_SCALE;
			default:
				throw new IllegalArgumentException("Unknown scale value");
			}
		} else {
			throw new IllegalArgumentException("Could not read scale value");
		}
	}

	private String readLineFromFile(String fileName) throws IOException {
		try (FileReader fin = new FileReader(fileName);
				BufferedReader reader = new BufferedReader(fin)) {
			return reader.readLine();
		}
	}
}
