package c8y.trackeragent.protocol.coban.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.sdk.client.SDKException;

import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;

public class HeartbeatCobanParser extends CobanParser {
    
    private static Logger logger = LoggerFactory.getLogger(HeartbeatCobanParser.class);

    public HeartbeatCobanParser(TrackerAgent trackerAgent) {
        super(trackerAgent);
    }

    @Override
    protected boolean accept(String[] report) {
        return report.length == 1;
    }

    @Override
    protected String doParse(String[] report) {
        return report[0];
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
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
}
