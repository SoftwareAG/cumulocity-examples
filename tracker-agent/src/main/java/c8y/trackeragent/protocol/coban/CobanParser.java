package c8y.trackeragent.protocol.coban;

import static c8y.trackeragent.utils.SignedLocation.altitude;
import static c8y.trackeragent.utils.SignedLocation.latitude;
import static c8y.trackeragent.utils.SignedLocation.longitude;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.Position;
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
        if (report.length == 0) {
            return null;
        }
        String imeiPart = report[0];
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
        if ("001".equals(reportType)) {
            return onParsedPositionUpdateMessage(reportCtx);
        }
        return false;
    }

    private boolean isHeartbeat(ReportContext reportCtx) {
        return reportCtx.getNumberOfEntries() == 1;
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
    
    private boolean onParsedPositionUpdateMessage(ReportContext reportCtx) {
        if (reportCtx.getNumberOfEntries() < 12) {
            logger.error("Invalid report: {}", reportCtx);
            return true;
        }
        logger.debug("Update position for imei {}.", reportCtx.getImei());
        BigDecimal lat = latitude().withValue(reportCtx.getEntry(7), reportCtx.getEntry(8)).getValue();
        BigDecimal lng = longitude().withValue(reportCtx.getEntry(9), reportCtx.getEntry(10)).getValue();
        BigDecimal alt = altitude().withValue(reportCtx.getEntry(11)).getValue();
        Position position = new Position();
        position.setLat(lat);
        position.setLng(lng);
        position.setAlt(alt);
        TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
        device.setPosition(position);
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
        return reportCtx.getEntry(1);
    }
}
