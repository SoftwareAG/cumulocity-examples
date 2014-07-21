package c8y.win;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.Test;

import c8y.Hardware;

public class WindowsHardwareDriverTest {
	public static final String REFERENCE_HWFILE = "/hardware.txt";
	
	@Test
	public void InitializeFromReaderTest(){
		try (InputStream is = getClass().getResourceAsStream(REFERENCE_HWFILE);
			Reader reader = new InputStreamReader(is)) {
			driver.initializeFromReader(reader);
		} catch (IOException e){
			fail(e.getMessage());
		}
		assertEquals(referenceHw, driver.getHardware());
	}

	private Hardware referenceHw = new Hardware("Dell System XPS L502X", "1CMCZP1", "unknown");
	private WindowsHardwareDriver driver = new WindowsHardwareDriver();
}
