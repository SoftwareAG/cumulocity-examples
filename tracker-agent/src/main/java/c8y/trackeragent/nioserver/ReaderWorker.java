package c8y.trackeragent.nioserver;

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
            SocketChannelState state = taskProvider.next();
            if (state == null) {
                continue;
            }
            try {
                process(state);
            } finally {
                state.setProcessing(false);
            }
        }

    }

    private void process(SocketChannelState state) {
        String report = state.getDataBuffer().getReport();
        if (report != null) {
            state.getReportExecutor().execute(report);
        }
    }

}