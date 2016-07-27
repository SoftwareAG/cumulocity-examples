package c8y.trackeragent.server;

import c8y.trackeragent.server.writer.OutWriter;

public class TestConnectionDetails extends ConnectionDetails {

    public TestConnectionDetails() {
        super(new TestOutWriterImpl(), null);
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
