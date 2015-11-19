package c8y.trackeragent.protocol.coban;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.Parser;
import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;

import com.cumulocity.sdk.client.SDKException;

public class CobanParser extends CobanReport implements Parser {
    
    private static Logger logger = LoggerFactory.getLogger(CobanParser.class);
    
    public CobanParser(TrackerAgent trackerAgent) {
        super(trackerAgent);
    }

    @Override
    public String parse(String[] report) throws SDKException {
        if (report.length < 2) {
            return null;
        }
        String imeiPart = report[1];
        logger.debug("Imei part = '{}'", imeiPart);
        String imei = imeiPart.substring(5);
        logger.debug("Imei = '{}'", imei);
        return imei;
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
        if(isHeartbeat(reportCtx)) {
            return onParsedHeartbeatToServer(reportCtx);
        }
        String reportType = getReportType(reportCtx);
        if ("A".equals(reportType)) {
            return onParsedDeviceLogonToServer(reportCtx);
        }
        return false;
    }

    private boolean isHeartbeat(ReportContext reportCtx) {
        return reportCtx.getNumberOfEntries() == 2;
    }
    
    private boolean onParsedHeartbeatToServer(ReportContext reportCtx) {
        logger.debug("Heartbeat for imei {}.", reportCtx.getImei());
        try {
            TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
            device.ping();
            writeOut(reportCtx, "ON");
        } catch (Exception ex) {
            logger.error("Error processing heartbeat on imei " +  reportCtx.getImei(), ex);
        }
        return true;
    }

    private boolean onParsedDeviceLogonToServer(ReportContext reportCtx) {
        logger.debug("Success logon for imei {}.", reportCtx.getImei());
        writeOut(reportCtx, "LOAD"); 
        return true;
    }

    private void writeOut(ReportContext reportCtx, String string) {
        try {
            reportCtx.getOut().write(string.getBytes("US-ASCII"));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private String getReportType(ReportContext reportCtx) {
        return reportCtx.getReportEntry(2);
    }
}
