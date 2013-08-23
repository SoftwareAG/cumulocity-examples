package c8y.kontron;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccelerometerReader {
	public static final String ACCEL_SCALE = "/sys/class/iio/device0/accel_full_scale";
	public static final String ACCEL_DATA = "/sys/class/iio/device0/accel_xyz";
	
	private static Logger logger = LoggerFactory.getLogger(AccelerometerReader.class);
	
	private double threshold;
	private double gscale = -1;

	public AccelerometerReader(double thresholdDefault) {
		this.threshold = thresholdDefault;
	}

	public void setThreshold(double newThreshold) {
		this.threshold = newThreshold;
	}

	public boolean poll() {
		initializeScale();
		
		String line = readLineFromFile(ACCEL_DATA);
		if (line != null && gscale > 0) {
			String[] values = line.split(" ") ;
			double x = Double.parseDouble(values[1].trim()) / gscale;
			double y = Double.parseDouble(values[2].trim()) / gscale;
			double z = Double.parseDouble(values[3].trim()) / gscale;			
			logger.debug("Accelerometer reading is " + x + ", " + y + ", " + z);
			
			return Math.abs(x) > threshold || Math.abs(y) > threshold || Math.abs(z) > threshold;
		}
		
		return false;
	}
	
	private void initializeScale() {
		if (gscale > 0) {
			return;
		}
		
		String line = readLineFromFile(ACCEL_SCALE);
		
		if (line != null) {
			int scaleRaw = Integer.parseInt(line);
			
			switch (scaleRaw)
			{
				default :
				case 8 : gscale = 4096 ; break ;
				case 4 : gscale = 8192 ; break ;
				case 2 : gscale = 16384 ; break ;
			}
			logger.debug("Scale is " + gscale);
		}
	}

	private String readLineFromFile(String fileName) {
		try (FileReader fin = new FileReader(fileName);
				BufferedReader reader = new BufferedReader(fin)) {
			return reader.readLine();
		} catch (IOException e) {
			logger.warn("Cannot read file " + fileName, e);
			return null;
		}
	}
}
