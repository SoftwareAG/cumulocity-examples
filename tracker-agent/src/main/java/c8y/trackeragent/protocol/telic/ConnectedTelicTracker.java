package c8y.trackeragent.protocol.telic;

import static java.lang.String.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.protocol.telic.parser.TelicFragment;
import c8y.trackeragent.server.ConnectionDetails;
import c8y.trackeragent.tracker.BaseConnectedTracker;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectedTelicTracker extends BaseConnectedTracker<TelicFragment> {

    protected static Logger logger = LoggerFactory.getLogger(ConnectedTelicTracker.class);

    public static final int HEADER_LENGTH = 28;
    public static final int REPORT_SKIP = 4;

    private boolean firstReport = true;

    @Override
    public void executeReport(ConnectionDetails connectionDetails, String reportStr) {
        try {
            if (firstReport) {
                reportStr = eat(reportStr, HEADER_LENGTH);
            }
            reportStr = eat(reportStr, REPORT_SKIP);
            super.executeReport(connectionDetails, reportStr);
        } finally {
            this.firstReport = false;
        }
    }

    static String eat(String report, int bytesToRead) {
        if (bytesToRead <= report.length()) {
            return report.substring(bytesToRead);
        } else {
            String message = format("Report %s too short! Need at least %s bytes", report, bytesToRead);
            throw new RuntimeException(message);
        }
    }
    
    @Override
    public TrackingProtocol getTrackingProtocol() {
        return TrackingProtocol.TELIC;
    }

}
