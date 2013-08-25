package c8y.kontron;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccelerometerReader {
	public static final String ACCEL_SCALE = "/sys/class/iio/device0/accel_full_scale";
	public static final String ACCEL_DATA = "/sys/class/iio/device0/accel_xyz";

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
		double scale = getScale();

		String line = readLineFromFile(file);
		if (line != null && scale > 0) {
			String[] values = line.split(" ");
			double x = Double.parseDouble(values[1].trim()) / scale;
			double y = Double.parseDouble(values[2].trim()) / scale;
			double z = Double.parseDouble(values[3].trim()) / scale;
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
			case 8:
				return 4096;
			case 4:
				return 8192;
			case 2:
				return 16384;
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
