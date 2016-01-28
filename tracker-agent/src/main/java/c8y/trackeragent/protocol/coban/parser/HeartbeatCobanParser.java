package c8y.trackeragent.protocol.coban.parser;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.sdk.client.SDKException;

import c8y.trackeragent.ReportContext;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.protocol.coban.message.CobanServerMessages;

@Component
public class HeartbeatCobanParser extends CobanParser {
    
    private static Logger logger = LoggerFactory.getLogger(HeartbeatCobanParser.class);
    
    private CobanServerMessages serverMessages;

    @Autowired
    public HeartbeatCobanParser(TrackerAgent trackerAgent, CobanServerMessages serverMessages) {
        super(trackerAgent);
        this.serverMessages = serverMessages;
    }

    @Override
    protected boolean accept(String[] report) {
    	System.out.println("Accept of " + Arrays.toString(report) + " = " + (report.length == 1));
        return report.length == 1;
    }

    @Override
    protected String doParse(String[] report) {
    	System.out.println("doParse = " + report);
        return report[0];
    }

    @Override
    public boolean onParsed(ReportContext reportCtx) throws SDKException {
    	System.out.println("onParses = " + reportCtx);
        logger.debug("Heartbeat for imei {}.", reportCtx.getImei());
        try {
            TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(reportCtx.getImei());
            device.ping();
            reportCtx.writeOut(serverMessages.on());
        } catch (Exception ex) {
            logger.error("Error processing heartbeat on imei " +  reportCtx.getImei(), ex);
        }
        return true;
    }
}
