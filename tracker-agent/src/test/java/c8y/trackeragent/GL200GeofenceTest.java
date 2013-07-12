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

package c8y.trackeragent;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import c8y.Geofence;
import c8y.Position;

import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;

public class GL200GeofenceTest {
	public static final String PASSWORD = "gl200";
	public static final String IMEI = "135790246811220";

	public static final String SETFENCESTR = "AT+GTGEO=gl200,0,3,101.412248,21.187891,1000,30,,,,,,,,,0001$";
	public static final String[] SETFENCE = SETFENCESTR
			.split(GL200Constants.FIELD_SEP);
	public static final String[] ACKFENCE = "+ACK:GTGEO,02010B,135790246811220,,0,0001,20100310172830,11F0$"
			.split(GL200Constants.FIELD_SEP);
	public static final String[] REPFENCE = "+RESP:GTGEO,02010B,135790246811220,,0,0,1,1,4.3,92,70.0,121.354335,31.222073,2009 0214013254,0460,0000,18d8,6141,00,,20090214093254,11F0$"
			.split(GL200Constants.FIELD_SEP);

	@Before
	public void setup() throws SDKException {
		fence = new Geofence();
		fence.setLng(new BigDecimal("101.412248"));
		;
		fence.setLat(new BigDecimal("21.187891"));
		fence.setRadius(new BigDecimal("1000"));
		fence.setActive(true);
		operation.set(fence);

		gl200gf = new GL200Geofence(trackerAgent, PASSWORD);

		List<Object> fragments = new ArrayList<Object>();
		fragments.add(gl200gf);

		when(trackerAgent.getOrCreate(anyString())).thenReturn(device);
	}

	@Test
	public void setGeofence() {
		String asciiOperation = gl200gf.translate(operation);
		assertEquals(SETFENCESTR, asciiOperation);
	}

	@Test
	public void acknowledgeGeofence() throws SDKException {
		gl200gf.translate(operation);
		String imei = gl200gf.parse(ACKFENCE);

		assertEquals(IMEI, imei);
		verify(trackerAgent).getOrCreate(IMEI);
		verify(device).setGeofence(fence);
	}

	@Test
	public void acknowledgeGeofenceWrongReply() throws SDKException {
		gl200gf.translate(operation);

		String[] wrongCorrelation = ACKFENCE;
		wrongCorrelation[5] = "0002";
		gl200gf.parse(wrongCorrelation);

		verifyZeroInteractions(trackerAgent);
	}

	@Test
	public void reportGeofence() throws SDKException {
		String imei = gl200gf.parse(REPFENCE);

		assertEquals(IMEI, imei);
		verify(trackerAgent, times(2)).getOrCreate(IMEI);

		Position position = new Position();
		position.setLat(new BigDecimal("31.222073"));
		position.setLng(new BigDecimal("121.354335"));
		position.setAlt(new BigDecimal("70.0"));
		verify(device).setPosition(position);

		verify(device).geofenceAlarm(false);
	}

	// TODO Verify the formatting of commands sent to the device (limits and lengths)

	private GL200Geofence gl200gf;
	private TrackerAgent trackerAgent = mock(TrackerAgent.class);
	private TrackerDevice device = mock(TrackerDevice.class);

	private OperationRepresentation operation = new OperationRepresentation();
	private Geofence fence;
}
