package c8y.trackeragent;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final TrackerAgent trackerAgent;
    private final ExecutorService reportsExecutor;
    private final Socket client;

    public RequestHandler(TrackerAgent trackerAgent, ExecutorService reportsExecutor, Socket client) {
        this.client = client;
        this.reportsExecutor = reportsExecutor;
        this.trackerAgent = trackerAgent;
    }

    @Override
    public void run() {
        try {
            ConnectedTracker peekTracker = peekTracker();
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
    
    private ConnectedTracker peekTracker() throws IOException {
        logger.debug("peek tracker for new connection...");
        InputStream bis = asInput(client);
        bis.mark(1);
        int marker = bis.read();
        bis.reset();
        if (marker >= '0' && marker <= '9') {
            return new ConnectedTelicTracker(client, bis, trackerAgent);
        } else {
            return new ConnectedGL200Tracker(client, bis, trackerAgent);
        }
    }

    private static InputStream asInput(Socket client) throws IOException {
        InputStream is = client.getInputStream();
        return new BufferedInputStream(is);
    }
}
