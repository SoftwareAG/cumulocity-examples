package c8y.trackeragent.nioserver;

import c8y.trackeragent.nioserver.NioServerEvent.ReadDataEvent;

public interface ReaderWorkerExecutorFactory {
    
    ReaderWorkerExecutor create(ReadDataEvent readData) throws Exception;
    
}
