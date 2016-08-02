package c8y.trackeragent.server;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ReportBuffer {

    private Queue<byte[]> content = new ConcurrentLinkedQueue<byte[]>(); 
    private final Object monitor = new Object();
    
    public void append(byte[] data, int dataLength) {
        synchronized (monitor) {
            if (data == null) {
                return;
            }
            byte[] dataCopy = copy(data, dataLength);
            content.add(dataCopy);
        }
    }

    public byte[] getReport() {
        synchronized (monitor) {
            return content.poll();
        }
    }
    
    private static byte[] copy(byte[] data, int dataLength) {
        byte[] dataCopy = new byte[dataLength];
        System.arraycopy(data, 0, dataCopy, 0, dataLength);
        return dataCopy;
    }


}
