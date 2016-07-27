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

package c8y.trackeragent.protocol.gl200.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;

import c8y.Geofence;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.OperationContext;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.service.MeasurementService;
import c8y.trackeragent.tracker.Translator;

/**
 * <p>
 * Geofence protocol of GL200 tracker. Samples below show: Set geofence on
 * device, acknowledge setting geofence, getting geofence report.
 * </p>
 * 
 * <pre>
 * AT+GTGEO=gl200,0,3,101.412248,21.187891,1000,600,,,,,,,,,0008$
 * +ACK:GTGEO,02010B,135790246811220,,0,0008,20100310172830,11F0$
 * +RESP:GTGEO,02010B,135790246811220,,0,0,1,1,4.3,92,70.0,121.354335,31.222073,2009 0214013254,0460,0000,18d8,6141,00,,20090214093254,11F0$
 */
@Component
public class GL200Geofence extends GL200LocationReport implements Translator {
    /**
     * Number of fence, device supports up to five fences.
     */
    public static final String GEO_ID = "0";

    /**
     * Mode for fence, triggers report when entering and leaving fence.
     */
    public static final String FENCE_MODE = "3";

    /**
     * Default checking interval.
     */
    public static final int DEFAULT_INTERVAL = 30;

    /**
     * Template for setting fence. Parameters are: Password, longitude,
     * latitude, radius, check interval, correlation ID
     */
    public static final String GEO_TEMPLATE = "AT+GTGEO=%s," + GEO_ID + "," + FENCE_MODE + ",%s,%s,%d,%d,,,,,,,,,%04x$";

    /**
     * Acknowledgement of fence setting from tracker.
     */
    public static final String GEOFENCE_ACKNOWLEDGE = "+ACK:GTGEO";

    /**
     * Fence enter/leave report from tracker.
     */
    public static final String GEOFENCE_REPORT = "+RESP:GTGEO";

    private String password;
    private short corrId = 0;
    private Geofence lastFence = new Geofence();
    private OperationRepresentation lastOperation;

    @Autowired
    public GL200Geofence(TrackerAgent trackerAgent, MeasurementService measurementService) {
        super(trackerAgent, measurementService);
        this.password = PASSWORD;
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        String reportType = reportCtx.getReport()[0];

        if (GEOFENCE_ACKNOWLEDGE.equals(reportType)) {
            return parseAcknowledgement(reportCtx.getReport(), reportCtx.getImei());
        } else if (GEOFENCE_REPORT.equals(reportType)) {
            return parseFenceReport(reportCtx);
        } else {
            return false;
        }
    }

    private boolean parseFenceReport(ReportContext reportCtx) throws SDKException {
        super.onParsed(reportCtx);
        String type = reportCtx.getReport()[5];
        TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
        device.geofenceAlarm("0".equals(type));
        return true;
    }

    private boolean parseAcknowledgement(String[] report, String imei) throws SDKException {
        String geoId = report[4];
        short returnedCorr = Short.parseShort(report[5], 16);
        Geofence ackedFence = null;
        OperationRepresentation ackOp;

        synchronized (this) {
            if (returnedCorr != corrId || !GEO_ID.equals(geoId) || lastFence == null) {
                return false;
            }
            ackedFence = lastFence;
            ackOp = lastOperation;
        }

        try {
            trackerAgent.getOrCreateTrackerDevice(imei).setGeofence(ackedFence);
            trackerAgent.finish(imei, ackOp);
        } catch (SDKException x) {
            trackerAgent.fail(imei, ackOp, "Error setting geofence", x);
        }
        return true;
    }

    /**
     * Convert the operation to set or disable a geofence to GL200 format.
     */
    @Override
    public String translate(OperationContext operationCtx) {
        OperationRepresentation operation = operationCtx.getOperation();
        Geofence geofence = operation.get(Geofence.class);

        if (geofence == null) {
            return null;
        }

        String lng = geofence.getLng().toString();
        if (lng.length() > 11) {
            lng = lng.substring(0, 11);
        }

        String lat = geofence.getLat().toString();
        if (lat.length() > 10) {
            lat = lat.substring(0, 10);
        }

        int radius = geofence.getRadius().intValue();
        if (radius < 50) {
            radius = 50;
        } else if (radius > 6000000) {
            radius = 6000000;
        }

        int checkInterval = DEFAULT_INTERVAL;
        if (!geofence.isActive()) {
            checkInterval = 0;
        }

        synchronized (this) {
            corrId++;
            lastFence = geofence;
            lastOperation = operation;
        }

        return String.format(GEO_TEMPLATE, password, lng, lat, radius, checkInterval, corrId);
    }
}
