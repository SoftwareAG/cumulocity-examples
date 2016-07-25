package c8y.trackeragent.nioserver;

public class SocketChannelState {

    private final ReaderWorkerExecutor reportExecutor;
    private final DataBuffer dataBuffer;
    private boolean processing;

    public SocketChannelState(ReaderWorkerExecutor reportExecutor, DataBuffer dataBuffer) {
        this.reportExecutor = reportExecutor;
        this.dataBuffer = dataBuffer;
        this.processing = false;
    }

    public void execute(String report) {
        reportExecutor.execute(report);
    }

    public DataBuffer getDataBuffer() {
        return dataBuffer;
    }

    public boolean isProcessing() {
        return processing;
    }

    public void setProcessing(boolean processing) {
        this.processing = processing;
    }
    
    
}