package c8y.trackeragent.tracker;

import java.util.List;

public interface ReportSplitter {
    
    List<String> split(byte[] data);

}
