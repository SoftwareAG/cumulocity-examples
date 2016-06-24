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

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;

import com.cumulocity.sdk.client.SDKException;

/**
 * <p>
 * Power on/off protocol of GL200 tracker. Samples below show: getting power on/off report.
 * This event is triggered when a certain event occur.
 * </p>
 * 
 * <pre>
 * 
 * +RESP:GTPNA,02010B,135790246811220,,20100214093254,11F0$
 * or
 * +RESP:GTPFA,02010B,135790246811220,,20100214093254,11F0$
 * 
 * 
 * The report for connecting external power supply protocol of GL200 tracker. Samples below show: getting external power supply report.
 * This event is triggered when a certain event occur.
 * </p>
 * 
 * <pre>
 * 
 * +RESP:GTEPN,02010B,135790246811220,,0,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,20100214093254,11F0$
 * or
 * +RESP:GTEPF,02010B,135790246811220,0,,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,18d8,6141,00,20100214093254,11F0$
 * 
 */
@Component
public class GL200Power extends GL200Parser {
    
    /**
     * Type of report: power on or power off.
     */
    public static final String POWERON_REPORT = "+RESP:GTPNA";
    public static final String POWEROFF_REPORT = "+RESP:GTPFA";
    
    public static final String EXTERNALPOWERON_REPORT = "+RESP:GTEPN";
    public static final String EXTERNALPOWEROFF_REPORT = "+RESP:GTEPF";
    
    private final TrackerAgent trackerAgent;

    @Autowired
    public GL200Power(TrackerAgent trackerAgent) {
        this.trackerAgent = trackerAgent;
    }
    
    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        String reportType = reportCtx.getReport()[0];
        if (POWERON_REPORT.equals(reportType)) {
            return parsePowerOn(reportCtx.getReport(), reportCtx.getImei());
        } else if (POWEROFF_REPORT.equals(reportType)) {
            return parsePowerOff(reportCtx.getReport(), reportCtx.getImei());
        } else if (EXTERNALPOWERON_REPORT.equals(reportType)) {
            return parseExternalPowerOn(reportCtx.getReport(), reportCtx.getImei());
        } else if (EXTERNALPOWEROFF_REPORT.equals(reportType)) {
            return parseExternalPowerOff(reportCtx.getReport(), reportCtx.getImei());
        } else {
            return false;
        }
    }

    private boolean parsePowerOff(String[] report, String imei) throws SDKException {
    	return powerAlarm(report, imei, true, false);
    }

    private boolean parsePowerOn(String[] report, String imei) throws SDKException {
    	return powerAlarm(report, imei, false, false);
    }
    
    private boolean parseExternalPowerOff(String[] report, String imei) throws SDKException {
        return powerAlarm(report, imei, true, true);
    }

    private boolean parseExternalPowerOn(String[] report, String imei) throws SDKException {
        return powerAlarm(report, imei, false, true);
    }
    
    private boolean powerAlarm(String[] report, String imei, boolean powerLost, boolean external) throws SDKException {
        TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(imei);
        device.powerAlarm(powerLost, external);
        return true;
	}
}
