package c8y.trackeragent.server;

import static c8y.trackeragent.utils.ByteHelper.getBytes;
import static java.util.concurrent.Executors.newFixedThreadPool;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WritersProvider {

    private static final Logger logger = LoggerFactory.getLogger(WritersProvider.class);

    private ExecutorService executorService;

    private final int remotePort;

    private final List<Writer> writers = new ArrayList<Writer>();

    public WritersProvider(int remotePort) {
        this.remotePort = remotePort;
    }

    public void start(int totalWriters) throws Exception {
        this.executorService = newFixedThreadPool(totalWriters);
        for (int i = 0; i < totalWriters; i++) {
            Writer writer = new Writer();
            writers.add(writer);
            executorService.submit(writer);
        }
    }

    public void stop() {
        executorService.shutdown();
    }

    public List<Writer> getWriters() {
        return writers;
    }

    public Writer getWriter(int no) {
        return writers.get(no);
    }

    public class Writer implements Runnable {

        private final Queue<String> tasks = new ConcurrentLinkedQueue<String>();
        private final Socket socket;

        Writer() throws Exception {
            socket = new Socket("localhost", remotePort);
        }

        public void write(String text) {
            tasks.add(text);
        }

        @Override
        public void run() {
            while (true) {
                String text = tasks.poll();
                if (text != null) {
                    execute(text);
                }
            }
        }

        private void execute(String text) {
            try {
                socket.getOutputStream().write(getBytes(text));
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        public void stop() throws Exception {
            socket.close();
        }
    }

}
