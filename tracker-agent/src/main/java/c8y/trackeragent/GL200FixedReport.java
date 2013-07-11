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

import java.math.BigDecimal;

import c8y.Position;

import com.cumulocity.sdk.client.SDKException;

/**
 * <p>
 * Location report of the GL200 tracker. For now, we assume that the report is
 * configured "elsewhere".
 * </p>
 * 
 * <pre>
 * +RESP:GTFRI,02010B,135790246811220,,0,0,2,1,4.3,92,70.0,121.354335,31.222073,20090 214013254,0460,0000,18d8,6141,00,0,4.3,92,70.0,121.354335,31.222073,20090101000000,04 60,0000,18d8,6141,00,,20090214093254,11F0$
 * </pre>
 */
public class GL200FixedReport implements Parser {
	/**
	 * Location report from tracker.
	 */
	public static final String FIXED_REPORT = "+RESP:GTFRI";

	public GL200FixedReport(TrackerAgent trackerAgent) {
		this.trackerAgent = trackerAgent;
	}

	@Override
	public String parse(String[] report) throws SDKException {
		String reportType = report[0];

		if (!FIXED_REPORT.equals(reportType)) {
			return null;
		}

		String imei = report[2];
		TrackerDevice device = trackerAgent.getOrCreate(imei);

		int reportStart = 7;
		final int reportLength = 12;
		int reportEnd = reportStart + Integer.parseInt(report[6])
				* reportLength;
		for (; reportStart < reportEnd; reportStart += reportLength) {
			parseLocationReport(device, report, reportStart);
		}
		return imei;
	}

	private void parseLocationReport(TrackerDevice device, String[] report,
			int reportStart) throws SDKException {
		Position pos = new Position();
		pos.setAlt(new BigDecimal(report[reportStart + 3]));
		pos.setLng(new BigDecimal(report[reportStart + 4]));
		pos.setLat(new BigDecimal(report[reportStart + 5]));
		device.setPosition(pos);
		
		device.setCellId(report[reportStart + 10]);
	}

	private TrackerAgent trackerAgent;
}
