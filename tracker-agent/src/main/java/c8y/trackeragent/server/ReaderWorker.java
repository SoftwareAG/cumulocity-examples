package c8y.trackeragent.server;

import c8y.trackeragent.utils.ByteHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReaderWorker implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ReaderWorker.class);

    private final IncomingMessageProvider incomingMessageProvider;

    public ReaderWorker(IncomingMessageProvider taskProvider) {
        this.incomingMessageProvider = taskProvider;
    }

    @Override
    public void run() {
        while (true) {
            IncomingMessage msg = incomingMessageProvider.next();
            if (msg == null) {
                logger.warn("Nothing left to do.");
                break;
            }
            logger.debug("Process next message from the queue.");
            try {
                msg.getConnectedTracker().executeReports(msg.getConnectionDetails(), msg.getMsg());
            } catch (Exception ex) {
                logger.error("Error processing connection 0x" + ByteHelper.toHexString(msg.getMsg()) + "!", ex);
            }
        }
    }

}