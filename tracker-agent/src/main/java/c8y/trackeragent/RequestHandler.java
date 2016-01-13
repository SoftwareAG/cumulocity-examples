package c8y.trackeragent;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.protocol.mapping.TrackerFactory;

public class RequestHandler implements Runnable {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    
    private final ExecutorService reportsExecutor;
    private final Socket client;
    private final TrackerFactory trackerFactory;

    public RequestHandler( ExecutorService reportsExecutor, Socket client, TrackerFactory trackerFactory) {
        this.client = client;
        this.reportsExecutor = reportsExecutor;
        this.trackerFactory = trackerFactory;
    }

    @Override
    public void run() {
        try {
            ConnectedTracker peekTracker = trackerFactory.getTracker(client);
            logger.debug("Tracker poke {} for connection from {}.", peekTracker.getClass().getSimpleName(), client.getReuseAddress());
            reportsExecutor.execute(peekTracker);
        } catch(Exception ex) {
            logger.error("Error handling request:", ex);
            if(!client.isClosed()) {
                try {
                    client.close();
                } catch (IOException e) {
                    logger.error("Error closing connection:", e);
                }
            }
        }
    }
}
