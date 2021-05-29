/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.context;

import java.math.BigDecimal;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.server.ConnectionDetails;

public class ReportContext extends ConnectionContext {

    private static Logger logger = LoggerFactory.getLogger(ReportContext.class);

    private final String[] report;

    public ReportContext(ConnectionDetails connectionDetails, String[] report) {
        super(connectionDetails);
        this.report = report;
    }

    public String[] getReport() {
        return report;
    }

    public String getEntry(int index) {
        if (index < report.length) {
            return report[index];
        } else {
            logger.debug("There is no entry at index " + index + " for array " + Arrays.toString(report));
            return null;
        }
    }

    public BigDecimal getEntryAsNumber(int index) {
        String entry = getEntry(index);
        if (StringUtils.isBlank(entry)) {
            return null;
        }
        try {
            return new BigDecimal(entry.trim());
        } catch (Exception e) {
            logger.info("Cannot parse to BigDecimal value: " + entry);
        }
        return null;
    }

    public Integer getEntryAsInt(int index) {
        String entry = getEntry(index);
        return StringUtils.isBlank(entry) ? null : Integer.parseInt(entry.trim());
    }

    public Double getEntryAsDouble(int index) {
        String entry = getEntry(index);
        if (StringUtils.isEmpty(entry)) {
            return null;
        }
        try {
            return Double.valueOf(entry.trim());
        } catch (Exception e) {
            logger.info("Cannot parse to double value: " + entry);
        }
        return null;
    }

    public int getNumberOfEntries() {
        return report.length;
    }

    public String getReportMessage() {
        return StringUtils.join(report, getTrackingProtocol().getFieldSeparator());
    }

    @Override
    public String toString() {
        return Arrays.toString(report);
    }

}
