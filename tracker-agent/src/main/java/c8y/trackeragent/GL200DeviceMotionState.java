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

import com.cumulocity.sdk.client.SDKException;

/**
 * <p>
 * Device Motion State indication protocol of GL200 tracker. Samples below show: getting device motion state indication report.
 * This event is triggered when a certain event occur.
 * </p>
 * 
 * <pre>
 * 
 * +RESP:GTSTT,02010B,135790246811220,,41,0,4.3,92,70.0,121.354335,31.222073,2009021,4013254,0460,0000,18d8,6141,00,20100214093254,11F0$
 */
public class GL200DeviceMotionState implements  Parser {
    
    /**
     * Type of report: Device Motion State Indication.
     */
    public static final String GTSTT_REPORT = "+RESP:GTSTT";
    

    public GL200DeviceMotionState(TrackerAgent trackerMgr) {
        this.trackerAgent = trackerMgr;
    }

    @Override
    public String parse(String[] report) throws SDKException {
        String reportType = report[0];

        if (GTSTT_REPORT.equals(reportType)) {
            return deviceMotionStateIndication(report);
        }  else {
            return null;
        }
    }

    private String deviceMotionStateIndication(String[] report) throws SDKException {
    	return deviceMotionAlarm(report, false);
    }
    
    private String deviceMotionAlarm(String[] report, boolean b) throws SDKException {
        String imei = report[2];
        TrackerDevice device = trackerAgent.getOrCreate(imei);
        device.motionAlarm(true);;
        return imei;
	}

	private TrackerAgent trackerAgent;
}
