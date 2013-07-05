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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import c8y.Position;

import com.cumulocity.model.ID;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;

public class TrackerManagerTest {
	@Before
	public void setup() {
		when(platform.getIdentityApi()).thenReturn(registry);
		when(platform.getInventoryApi()).thenReturn(inventory);
		when(platform.getEventApi()).thenReturn(events);
		extId.setType(TrackerDevice.XTID_TYPE);

		ManagedObjectRepresentation mo = new ManagedObjectRepresentation();
		mo.setId(new GId("123"));
		eir.setExternalId(IMEI);
		eir.setType(TrackerDevice.XTID_TYPE);
		eir.setManagedObject(mo);
	}

	@Test
	public void testNotExistingDevice() throws SDKException {
		// Identity API does not know the device
		SDKException sdkx = new SDKException(404, "Not found");
		when (registry.getExternalId(extId)).thenThrow(sdkx);

		// This device is returned after creation
		ManagedObjectRepresentation returnedMo = new ManagedObjectRepresentation();
		returnedMo.setId(new GId("123"));
		returnedMo.setName("Heinz");
		returnedMo.setSelf("http://link.to.my/self");
		
		when(inventory.create(any(ManagedObjectRepresentation.class))).thenReturn(returnedMo);
		
		trackerMgr.locationUpdate(IMEI, LATITUDE, LONGITUDE, ALTITUDE);
		
		// Check if device is correctly created in the inventory
		ArgumentCaptor<ManagedObjectRepresentation> moArg = ArgumentCaptor.forClass(ManagedObjectRepresentation.class);
		verify(inventory).create(moArg.capture());
		assertEquals(LATITUDE, moArg.getValue().get(Position.class).getLatitude());
		assertEquals(LONGITUDE, moArg.getValue().get(Position.class).getLongitude());
		assertEquals(ALTITUDE, moArg.getValue().get(Position.class).getAltitude());
		
		// Check if IMEI is correctly registered in the identity service
		ArgumentCaptor<ExternalIDRepresentation> idArg = ArgumentCaptor.forClass(ExternalIDRepresentation.class);
		verify(registry).create(idArg.capture());
		assertEquals(IMEI, idArg.getValue().getExternalId());
	}
	
	@Test 
	public void testExistingDevice() throws SDKException {
		// Identity API knows the device
		when (registry.getExternalId(extId)).thenReturn(eir);
		
		trackerMgr.locationUpdate(IMEI, LATITUDE, LONGITUDE, ALTITUDE);
		
		// Check if device is correctly created in the inventory
		ArgumentCaptor<ManagedObjectRepresentation> moArg = ArgumentCaptor.forClass(ManagedObjectRepresentation.class);
		verify(inventory).update(moArg.capture());
		
		
	}

	Platform platform = mock(Platform.class);
	IdentityApi registry = mock(IdentityApi.class);
	InventoryApi inventory = mock(InventoryApi.class);
	EventApi events = mock (EventApi.class);
	TrackerManager trackerMgr = new TrackerManager(platform);
	
	public static final String IMEI = "0123456789";
	public static final ID extId = new ID(IMEI);
	public static final ExternalIDRepresentation eir = new ExternalIDRepresentation();
	public static final BigDecimal LATITUDE = new BigDecimal(37.0625);
	public static final BigDecimal LONGITUDE =  new BigDecimal(-95.677068);
	public static final BigDecimal ALTITUDE = new BigDecimal(1);	
}
