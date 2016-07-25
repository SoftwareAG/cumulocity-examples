package c8y.trackeragent.nioserver;

public interface ReaderWorkerExecutorFactory {
    
    ReaderWorkerExecutor create(byte[] data);
    
}
