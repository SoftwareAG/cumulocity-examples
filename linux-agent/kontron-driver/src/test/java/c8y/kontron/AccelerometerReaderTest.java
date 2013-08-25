package c8y.kontron;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class AccelerometerReaderTest {
	public static final double THRESHOLD = 2;
	public static final String SCALE_FILE = "src/test/resources/accel_full_scale";
	public static final String RESTING_FILE = "src/test/resources/accel_xyz_resting";
	public static final String SHAKING_FILE = "src/test/resources/accel_xyz_shaking";
	
	private AccelerometerReader ar = new AccelerometerReader(THRESHOLD);
	
	@Test
	public void scaleConversionLegalValues() {
		assertEquals(4096, ar.convertScale("8"), 0.1);
		assertEquals(8192, ar.convertScale("4"), 0.1);
		assertEquals(16384, ar.convertScale("2"), 0.1);
	}

	@Test(expected=IllegalArgumentException.class)
	public void scaleConversionIllegalValues1() {
		ar.convertScale("1");
	}

	@Test(expected=IllegalArgumentException.class)
	public void scaleConversionIllegalValues2() {
		ar.convertScale(null);
	}
	
	@Test
	public void scaleConversionfromFile() throws IOException {
		ar.initializeScale(SCALE_FILE);
		assertEquals(4096, ar.getScale(), 0.1);
	}
	
	@Test
	public void pollResting() throws IOException {
		ar.initializeScale(SCALE_FILE);
		assertFalse(ar.poll(RESTING_FILE));
	}
	
	@Test
	public void pollShaking() throws IOException {
		ar.initializeScale(SCALE_FILE);
		assertTrue(ar.poll(SHAKING_FILE));		
		
		ar.setThreshold(1000);
		assertFalse(ar.poll(SHAKING_FILE));
	}
}
