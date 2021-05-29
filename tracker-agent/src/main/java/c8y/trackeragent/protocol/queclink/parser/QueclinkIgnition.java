/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.queclink.parser;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.sdk.client.SDKException;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.context.ReportContext;
import c8y.trackeragent.device.TrackerDevice;

@Component
public class QueclinkIgnition extends QueclinkParser {
    
    public static final String IGNITION_ON = "+RESP:GTIGN";
    public static final String IGNITION_OFF = "+RESP:GTIGF";
    
    public static final String IGNITION_LOCATIONR = "+RESP:GTIGL";
    
    private final TrackerAgent trackerAgent;
    
    @Autowired
    public QueclinkIgnition(TrackerAgent trackerAgent) {
        this.trackerAgent = trackerAgent;
    }
    
    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        String reportType = reportCtx.getReport()[0];
        if (reportType.equals(IGNITION_ON)) {
            createIgnitionOnEvent(reportCtx, reportCtx.getImei());
            return true;
        } else if (reportType.equals(IGNITION_OFF)) {
            createIgnitionOffEvent(reportCtx, reportCtx.getImei());
            return true;
        }
        return false;
    }
    
    public void createIgnitionOffEvent(ReportContext reportCtx, String imei) {
        TrackerDevice trackerDevice = trackerAgent.getOrCreateTrackerDevice(imei);
        DateTime dateTime = queclinkReport.getReportDateTime(reportCtx);
        trackerDevice.ignitionOffEvent(dateTime); 
    }
    
    public void createIgnitionOnEvent(ReportContext reportCtx, String imei) {
        TrackerDevice trackerDevice = trackerAgent.getOrCreateTrackerDevice(imei);
        DateTime dateTime = queclinkReport.getReportDateTime(reportCtx);
        trackerDevice.ignitionOnEvent(dateTime);
    }

    public void createEventFromReport(ReportContext reportCtx) {

        if (reportCtx.getEntry(0).equals(IGNITION_LOCATIONR)) {
            
            int reportTypeIndex = 5;
            String ignitionOnType = "00";
            String ignitionOffType = "01";
            
            String reportType = reportCtx.getEntry(reportTypeIndex);
            if (reportType.equals(ignitionOnType)) {
                createIgnitionOnEvent(reportCtx, reportCtx.getImei());
            } else if (reportType.equals(ignitionOffType)) {
                createIgnitionOffEvent(reportCtx, reportCtx.getImei());
            }
        }
        
    }
    
}