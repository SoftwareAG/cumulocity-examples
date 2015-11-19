package c8y.trackeragent.protocol.coban;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.TrackerAgent;

public class CobanReport {
    
    private static Logger logger = LoggerFactory.getLogger(CobanReport.class);
    
    protected final TrackerAgent trackerAgent;

    public CobanReport(TrackerAgent trackerAgent) {
        this.trackerAgent = trackerAgent;
    }

}
