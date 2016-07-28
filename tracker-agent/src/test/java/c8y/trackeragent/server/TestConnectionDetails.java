package c8y.trackeragent.server;

import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.server.writer.OutWriter;

public class TestConnectionDetails extends ConnectionDetails {

    public static final TrackingProtocol DEFAULT_PROTOCOL = TrackingProtocol.COBAN;

    public TestConnectionDetails() {
        super(DEFAULT_PROTOCOL, new TestOutWriterImpl(), null);
    }
    
    public TestConnectionDetails(String imei) {
        this();
        setImei(imei);
    }

    public String getOut() {
        return ((TestOutWriterImpl) getOutWriter()).getWritten();
    }

    public static class TestOutWriterImpl implements OutWriter {

        private String written = null;

        @Override
        public void write(String text) {
            written = written == null ? text : written + text;
        }

        public String getWritten() {
            return written;
        }

    }

}
