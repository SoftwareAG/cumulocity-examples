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

package c8y.trackeragent.protocol.queclink.parser;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.sdk.client.SDKException;

import c8y.Mobile;
import c8y.Position;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.protocol.queclink.QueclinkConstants;
import c8y.trackeragent.service.MeasurementService;

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
@Component
public class QueclinkLocationReport extends QueclinkParser {
    
    private Logger logger = LoggerFactory.getLogger(QueclinkLocationReport.class);
    /**
     * Online reports sent directly by the device when GPRS is available.
     */
    public static final String ONLINE_REP = "+RESP";

    /**
     * Reports that have been buffered due to GPRS unavailability. TODO Time
     * handling for such reports is incorrect.
     */
    public static final String BUFFER_REP = "+BUFF";

	/**
	 * Diverse Location reports sent by tracker.
	 */
    // @formatter:off
    public static final String[] LOCATION_REPORTS = {
        // Common commands
        "GTGEO", "GTRTL", "GTNMR",
        // GL200 and GL300 specific
        "GTFRI", "GTSPD", "GTSOS", "GTPNL", "GTDIS", "GTDOG", "GTIGL", "GTDOG",
        // GL500 and GL505 specific
        "GTCTN", "GTSTR", 
        // GV500-specific
        "GTTOW", "GTHBM"
    };
    // @formatter:on
	
    protected final TrackerAgent trackerAgent;
    protected final MeasurementService measurementService;

    @Autowired
    public QueclinkLocationReport(TrackerAgent trackerAgent, MeasurementService measurementService) {
        this.trackerAgent = trackerAgent;
        this.measurementService = measurementService;
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        String[] reportType = reportCtx.getReport()[0].split(":");
        if (ONLINE_REP.equals(reportType[0]) || BUFFER_REP.equals(reportType[0])) {
            for (String availableReps : LOCATION_REPORTS) {
                if (availableReps.equals(reportType[1])) {
                    return processLocationReportOnParsed(reportCtx);
                }
            }
        }
        return false;
    }
    
    private boolean processLocationReportOnParsed(ReportContext reportCtx) throws SDKException {
        String deviceType = reportCtx.getEntry(1).substring(0, 2);
        
        TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
        String vin = reportCtx.getEntry(3);
        device.registerVIN(vin);
        
        createMileageMeasurementIfAvailable(reportCtx, device);
        
        int reportStart = 7;
        int reportLength = 12;
        int reportEnd = reportStart + reportCtx.getEntryAsInt(6) * reportLength;
        int batteryInfoIndex = reportEnd;
        
        if (QueclinkConstants.GL500_ID.equals(deviceType) || 
                QueclinkConstants.GL505_ID.equals(deviceType)) {
            reportStart = 9;
            reportLength = 11;
            reportEnd = reportStart + reportLength; // Only one report.
            batteryInfoIndex = 8;
        }

        if (QueclinkConstants.GV500_ID.equals(deviceType)) {
            reportStart = 8;
            reportEnd = reportStart + reportCtx.getEntryAsInt(7) * reportLength;
        }

        for (; reportStart < reportEnd; reportStart += reportLength) {
            processLocationReportOnParsed(device, reportCtx, reportStart);
        }
        
        if (!QueclinkConstants.GV500_ID.equals(deviceType)) {
            createBatteryMeasurement(device, reportCtx, batteryInfoIndex);
        }
        

        return true;
    }

    private void createMileageMeasurementIfAvailable(ReportContext reportCtx, TrackerDevice device) throws NumberFormatException {
        if (reportCtx.getNumberOfEntries() <= 20) {
            return;
        }
        BigDecimal mileage = reportCtx.getEntryAsNumber(20);
        if (mileage != null) {
            measurementService.createMileageMeasurement(mileage, device, new DateTime());
        }
    }

	private void processLocationReportOnParsed(TrackerDevice device, ReportContext report,
			int reportStart) throws SDKException {
		if (report.getEntry(reportStart + 3).length() > 0
				&& report.getEntry(reportStart + 4).length() > 0
				&& report.getEntry(reportStart + 5).length() > 0) {
			Position pos = new Position();
			pos.setAlt(new BigDecimal(report.getEntry(reportStart + 3)));
			pos.setLng(new BigDecimal(report.getEntry(reportStart + 4)));
			pos.setLat(new BigDecimal(report.getEntry(reportStart + 5)));
			device.setPosition(pos);
		}
		
		if (report.getEntry(reportStart + 10).length() > 0) {
		    createMobileInfo(device, report, reportStart + 7);
		}
	}
	
	private void createBatteryMeasurement(TrackerDevice device, ReportContext reportCtx, int batteryInfoIndex) throws NumberFormatException {
	    
	    if (batteryInfoIndex > 0 && batteryInfoIndex < reportCtx.getReport().length) {
    	    BigDecimal batteryLevel = reportCtx.getEntryAsNumber(batteryInfoIndex);
    	    if (batteryLevel != null) {
    	        logger.info("Battery percentage: {}", reportCtx.getEntry(batteryInfoIndex));
    	        measurementService.createPercentageBatteryLevelMeasurement(batteryLevel, device, new DateTime());   	       
    	    }
	    }
	}
	
	private void createMobileInfo(TrackerDevice device, ReportContext reportCtx, int mobileInfoIndex) {
	    Mobile mobile = new Mobile();
	    mobile.setMcc(reportCtx.getEntry(mobileInfoIndex));
	    mobile.setMnc(reportCtx.getEntry(mobileInfoIndex + 1));
	    int lacDecimal = Integer.parseInt(reportCtx.getEntry(mobileInfoIndex + 2), 16);
	    mobile.setLac(String.valueOf(lacDecimal));
	    int cellDecimal = Integer.parseInt(reportCtx.getEntry(mobileInfoIndex + 3), 16);
        mobile.setCellId(String.valueOf(cellDecimal));
	    
        device.setMobile(mobile);
	}
}
