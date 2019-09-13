package c8y.trackeragent.server;

import c8y.trackeragent.utils.ByteHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;

public class ReaderWorker implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ReaderWorker.class);

    @NotNull
    private final IncomingMessage incomingMessage;

    public ReaderWorker(IncomingMessage incomingMessage) {
        this.incomingMessage = incomingMessage;
    }

    @Override
    public void run() {
        logger.debug("Processing {}.", incomingMessage);
        try {
            incomingMessage.process();
        } catch (Exception ex) {
            logger.error("Error processing {} !", incomingMessage, ex);
        }
    }

}