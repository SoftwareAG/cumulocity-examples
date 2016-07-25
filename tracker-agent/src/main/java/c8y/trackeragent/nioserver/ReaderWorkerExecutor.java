package c8y.trackeragent.nioserver;

public interface ReaderWorkerExecutor {
    
    void execute(String report);
    
    String getReportSeparator();

}
