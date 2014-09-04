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

package c8y.rpi;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import c8y.Hardware;
import c8y.rpi.PiHardwareDriver;

public class PiHardwareDriverTest {
    
	public static final String REFERENCE_HWFILE = "/hardware.txt";

	@Test
	public void hardwareReadingSuccessful() throws IOException {
		try {
			driver.initializeFromFile(getClass().getResource(REFERENCE_HWFILE).getPath());
		} catch (IOException e) {
			fail(e.toString());
		}
		
		assertEquals(referenceHw, driver.getHardware());
	}

	private Hardware referenceHw = new Hardware("RaspPi BCM2708", "0000000017b769d5", "000e");
	private PiHardwareDriver driver = new PiHardwareDriver();
}
