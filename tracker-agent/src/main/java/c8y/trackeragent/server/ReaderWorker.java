package c8y.trackeragent.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReaderWorker implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ReaderWorker.class);

    private final WorkerTaskProvider taskProvider;

    public ReaderWorker(WorkerTaskProvider taskProvider) {
        this.taskProvider = taskProvider;
    }

    @Override
    public void run() {
        while (true) {
            ActiveConnection connection = taskProvider.next();
            if (connection == null) {
                continue;
            }
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
        String report = connection.getDataBuffer().getReport();
        if (report != null) {
            connection.getConnectedTracker().executeReport(connection.getConnectionDetails(), report);
        }
    }

}