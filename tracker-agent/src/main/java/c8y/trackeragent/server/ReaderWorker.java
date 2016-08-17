package c8y.trackeragent.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReaderWorker implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ReaderWorker.class);

    private final ActiveConnectionProvider connectionProvider;

    public ReaderWorker(ActiveConnectionProvider taskProvider) {
        this.connectionProvider = taskProvider;
    }

    @Override
    public void run() {
        while (true) {
            ActiveConnection connection = connectionProvider.next();
            if (connection == null) {
                break;
            }
            logger.debug("Process next connection from the queue.");
            try {
                process(connection);
            } catch (Exception ex) {
                logger.error("Error processing connection " + connection + "!", ex);
            } finally {
                connection.setProcessing(false);
            }
        }
    }

    private void process(ActiveConnection connection) {
        byte[] data = connection.getReportBuffer().pollReport();
        if (data == null) {
            return;
        }        
        connection.getConnectedTracker().executeReports(connection.getConnectionDetails(), data);
    }

}