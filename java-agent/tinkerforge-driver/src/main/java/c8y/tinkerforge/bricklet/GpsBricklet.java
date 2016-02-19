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

package c8y.tinkerforge.bricklet;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.Hardware;
import c8y.Position;
import c8y.lx.driver.DeviceManagedObject;
import c8y.lx.driver.OperationExecutor;
import c8y.lx.driver.PollingDriver;
import c8y.tinkerforge.TFIds;

import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.tinkerforge.BrickletGPS;
import com.tinkerforge.BrickletGPS.Coordinates;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class GpsBricklet extends PollingDriver {

	public static final String TYPE = "Gps";
	public static final String LU_EVENT_TYPE = "c8y_LocationUpdate";
	public static final BigDecimal COOR_DIVISOR = new BigDecimal("1000000");
	public static final BigDecimal ALT_DIVISOR = new BigDecimal("100");

	private static final Logger logger = LoggerFactory
			.getLogger(GpsBricklet.class);

	private ManagedObjectRepresentation gpsMo = new ManagedObjectRepresentation();
	private EventRepresentation locationUpdate = new EventRepresentation();
	private Position position = new Position();

	private String id;
	private BrickletGPS gps;

	public GpsBricklet(String id, BrickletGPS gps) {
		super("c8y_" + TYPE, TFIds.getPropertyName(TYPE),
				BaseSensorBricklet.DEFAULT_INTERVAL);
		this.id = id;
		this.gps = gps;
	}
	
    @Override
    public void initialize() throws Exception {
        // Nothing to do here.
    }

	@Override
	public OperationExecutor[] getSupportedOperations() {
		return new OperationExecutor[0];
	}

	@Override
	public void discoverChildren(ManagedObjectRepresentation parent) {
		try {
			gpsMo.set(TFIds.getHardware(gps, TYPE));
		} catch (TimeoutException | NotConnectedException e) {
			logger.warn("Cannot read hardware parameters", e);
		}

		gpsMo.setType(TFIds.getType(TYPE));
		gpsMo.setName(TFIds.getDefaultName(parent.getName(), TYPE, id));

		try {
			DeviceManagedObject dmo = new DeviceManagedObject(getPlatform());
			dmo.createOrUpdate(gpsMo, TFIds.getXtId(id, parent.get(Hardware.class).getSerialNumber()), parent.getId());

			locationUpdate.setSource(gpsMo);
			locationUpdate.setType(LU_EVENT_TYPE);
			locationUpdate.setText("Location updated");
		} catch (SDKException e) {
			logger.warn("Cannot create sensor", e);
		}
	}

	@Override
	public void run() {
		// TODO Change this to use a listener ... now that I know how it works.
		Position newPos = new Position();

		try {
			Coordinates coor = gps.getCoordinates();

			long latitude = coor.latitude;
			char latDir = coor.ns;
			BigDecimal lat = new BigDecimal(latitude).divide(COOR_DIVISOR);
			if (latDir == 'S') { lat = lat.negate(); }
			newPos.setLat(lat);

			long longitude = coor.longitude;
			char longDir = coor.ew;
			BigDecimal lng = new BigDecimal(longitude).divide(COOR_DIVISOR);
			if (longDir == 'W') { lng = lng.negate(); }
			newPos.setLng(lng);
		} catch (TimeoutException | NotConnectedException e) {
			logger.warn("Cannot retrieve coordinates, ignoring", e);
		}

		try {
			long altitude = gps.getAltitude().altitude;
			BigDecimal alt = new BigDecimal(altitude).divide(ALT_DIVISOR);
			newPos.setAlt(alt);
		} catch (TimeoutException | NotConnectedException e) {
			logger.warn("Cannot retrieve altitude, ignoring", e);
		}

		if (!position.equals(newPos)) {
			position = newPos;

			try {
				ManagedObjectRepresentation mo = new ManagedObjectRepresentation();
				mo.set(position);
				mo.setId(gpsMo.getId());
				getPlatform().getInventoryApi().update(mo);

				locationUpdate.setTime(new Date());
				locationUpdate.set(position);
				getPlatform().getEventApi().create(locationUpdate);
			} catch (SDKException e) {
				logger.warn("Cannot send location update", e);
			}
		}
	}
}
