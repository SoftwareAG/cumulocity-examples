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

import com.cumulocity.rest.representation.operation.OperationRepresentation;
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
 */
public class GL200Power implements  Parser {
    
    /**
     * Type of report: power on or power off.
     */
    public static final String POWERON_REPORT = "+RESP:GTPNA";
    public static final String POWEROFF_REPORT = "+RESP:GTPFA";

    public GL200Power(TrackerAgent trackerMgr) {
        this.trackerAgent = trackerMgr;
    }

    @Override
    public String parse(String[] report) throws SDKException {
        String reportType = report[0];

        if (POWERON_REPORT.equals(reportType)) {
            return parsePowerOn(report);
        } else if (POWEROFF_REPORT.equals(reportType)) {
            return parsePowerOff(report);
        } else {
            return null;
        }
    }

    private String parsePowerOff(String[] report) throws SDKException {
        String imei = report[2];
        String name = report[3];
        String time = report[4];
        
        TrackerDevice device = trackerAgent.getOrCreate(imei);
        device.powerOffAlarm(imei,name,time);
        return imei;
    }

    private String parsePowerOn(String[] report) throws SDKException {
        String imei = report[2];
        String name = report[3];
        String time = report[4];

        TrackerDevice device = trackerAgent.getOrCreate(imei);
        device.powerOnAlarm(imei,name,time);
        return imei;
    }
    
    private TrackerAgent trackerAgent;
}
