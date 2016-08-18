package c8y.trackeragent.server.writer;

import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.server.TrackerServer;

public class OutWriterImpl implements OutWriter {
    
    private static Logger logger = LoggerFactory.getLogger(OutWriter.class);

    private TrackerServer server;
    private SocketChannel channel;

    public OutWriterImpl(TrackerServer server, SocketChannel channel) {
        this.server = server;
        this.channel = channel;
    }

    @Override
    public void write(String text) {
        logger.debug("Write to device: {}.", text);
        server.send(channel, text);
    }

}
