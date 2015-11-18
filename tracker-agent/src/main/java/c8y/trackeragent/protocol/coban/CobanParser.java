package c8y.trackeragent.protocol.coban;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.Parser;
import c8y.trackeragent.ReportContext;

import com.cumulocity.sdk.client.SDKException;

public class CobanParser implements Parser {
    
    protected static Logger logger = LoggerFactory.getLogger(CobanParser.class);

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
        String reportType = getReportType(reportCtx);
        if ("A".equals(reportType)) {
            return onParsedDeviceLogonToServer(reportCtx);
        }
        return false;
    }
    
    private boolean onParsedDeviceLogonToServer(ReportContext reportCtx) {
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
