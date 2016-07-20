package c8y.trackeragent.connection;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import c8y.trackeragent.ConnectedTracker;

@Component
public class ConnectionReaderManager {

    protected static Logger logger = LoggerFactory.getLogger(ConnectionReaderManager.class);

    private static final int NO_OF_READER_THREADS = 5;

    private final ExecutorService executorService = Executors.newFixedThreadPool(NO_OF_READER_THREADS);

    private List<ConnectedTracker<?>> trackers = new ArrayList<ConnectedTracker<?>>();

   // @PostConstruct
    public void init() {
        logger.info("Initialize {} reader threads", NO_OF_READER_THREADS);
        for (int i = 0; i < NO_OF_READER_THREADS; i++) {
            executorService.execute(new ConnectionReader());
        }
    }

    private class ConnectionReader implements Runnable {

        @Override
        public void run() {
            while (true) {
                for (ConnectedTracker<?> tracker : trackers) {
                    try {
                        read(tracker);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

        }

        private void read(ConnectedTracker<?> tracker) throws IOException {
            byte[] bytes = new byte[1024];
            InputStream in = tracker.getIn();
            int available = in.available();
            if (available > 0) {
                in.read(bytes, 0, available);
            }
            
        }

    }

}
