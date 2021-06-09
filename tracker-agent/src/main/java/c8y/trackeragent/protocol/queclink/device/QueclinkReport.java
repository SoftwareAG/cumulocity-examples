/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.queclink.device;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import c8y.trackeragent.context.ReportContext;

public class QueclinkReport {
    
    public DateTime getReportDateTime(ReportContext reportCtx) {
        return getReportDateTime(reportCtx.getReport());
    }
    
    public DateTime getReportDateTime(String[] report) {
        return convertEntryToDateTime(report[report.length - 2]);
    }
    
    public DateTime convertEntryToDateTime(String reportDate) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");
        DateTime dateTime = formatter.parseDateTime(reportDate);
        return dateTime;
    }
}
