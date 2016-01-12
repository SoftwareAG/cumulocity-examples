package c8y.trackeragent.protocol.telic;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.agent.server.context.DeviceContextService;

import c8y.trackeragent.ConnectedTracker;
import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.protocol.telic.parser.TelicLocationReport;

public class ConnectedTelicTracker extends ConnectedTracker {
    
    protected static Logger logger = LoggerFactory.getLogger(ConnectedTelicTracker.class);
    
    public static final int HEADER_LENGTH = 28;
    public static final int REPORT_SKIP = 4;

    public ConnectedTelicTracker(Socket client, InputStream bis, TrackerAgent trackerAgent, DeviceContextService contextService) throws IOException {
        super(client, eat(bis, HEADER_LENGTH), TelicConstants.REPORT_SEP, TelicConstants.FIELD_SEP, trackerAgent, contextService);
        addFragment(new TelicLocationReport(trackerAgent));
    }

    private static InputStream eat(InputStream bis, int bytesToRead) throws IOException {
        byte[] dummy = new byte[bytesToRead];
        int bytesRead = bis.read(dummy, 0, bytesToRead);
        if (bytesRead < bytesToRead) {
            logger.warn("{} bytes read from header but expected at least {}, skip this report!", bytesRead, bytesToRead);
            return null;
        }
        return bis;
    }

    @Override
    public String readReport(InputStream is) throws IOException {
        logger.debug("Start reading telic report");
        if (eat(is, REPORT_SKIP) == null) {
            return null;
        }
        return super.readReport(is);
    }
}
