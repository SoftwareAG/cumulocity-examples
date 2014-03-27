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

import c8y.MotionTracking;

import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;

/**
 * <p>
 * Device Motion State indication protocol of GL200 tracker. Samples below show:
 * getting device motion state indication report. This event is triggered when a
 * certain event occur.
 * </p>
 * 
 * <pre>
 * +RESP:GTSTT,02010B,135790246811220,,41,0,4.3,92,70.0,121.354335,31.222073,2009021,4013254,0460,0000,18d8,6141,00,20100214093254,11F0$
 * </pre>
 */
public class GL200DeviceMotionState extends GL200Parser implements Translator {

    /**
     * Type of report: Device Motion State Indication.
     */
    public static final String MOTION_REPORT = "+RESP:GTSTT";

    public static final String MOTION_DETECTED = "42";

    /**
     * Change the event mask to include motion tracking.
     */
    public static final String MOTION_TEMPLATE = "AT+GTCFG=%s,,,,,,,,,,%d,,,,,,,,,,,%04x$";

    /**
     * Events to set: Power on/off, external power on/off, battery low are
     * always on. Device motion is added depending on configuration from
     * platform.
     */
    public static final int MOTION_OFF = 1 + 2 + 4 + 8 + 32;
    public static final int MOTION_ON = 1 + 2 + 4 + 8 + 32 + 256;

    public static final String MOTION_ACK = "+ACK:GTCFG";

    private final TrackerAgent trackerAgent;
    private final String password;
    private short corrId = 0;
    private boolean lastState;
    private OperationRepresentation lastOperation;


    public GL200DeviceMotionState(TrackerAgent trackerAgent, String password) {
        this.trackerAgent = trackerAgent;
        this.password = password;
    }

    @Override
    public boolean onParsed(String[] report, String imei) throws SDKException {
        String reportType = report[0];
        if (MOTION_ACK.equals(reportType)) {
            return onParsedAck(report, imei);
        } else if (MOTION_REPORT.equals(reportType)) {
            return onParsedMotion(report, imei);
        } else {
            return false;
        }
    }

    private boolean onParsedMotion(String[] report, String imei) throws SDKException {
        boolean motion = MOTION_DETECTED.equals(report[4]);
        TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(imei);
        device.motionAlarm(motion);
        return true;
    }

    private boolean onParsedAck(String[] report, String imei) throws SDKException {
        short returnedCorr = Short.parseShort(report[4], 16);
        boolean ackedState;
        OperationRepresentation ackOp;

        synchronized (this) {
            if (returnedCorr != corrId) {
                return false;
            }
            ackedState = lastState;
            ackOp = lastOperation;
        }

        try {
            trackerAgent.getOrCreateTrackerDevice(imei).setMotionTracking(ackedState);
            trackerAgent.finish(imei, ackOp);
        } catch (SDKException x) {
            trackerAgent.fail(imei, ackOp, "Error setting motion tracking", x);
        }
        return true;
    }

    @Override
    public String translate(OperationRepresentation operation) {
        MotionTracking mTrack = operation.get(MotionTracking.class);

        if (mTrack == null) {
            return null;
        }

        synchronized (this) {
            corrId++;
            lastState = mTrack.isActive();
            lastOperation = operation;
        }

        return String.format(MOTION_TEMPLATE, password, mTrack.isActive() ? MOTION_ON : MOTION_OFF, corrId);
    }
}
