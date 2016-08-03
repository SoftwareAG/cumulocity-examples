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

    private static final int TOTAL_WORKER_THREADS = 10;

    private final ExecutorService executorService = newFixedThreadPool(TOTAL_WORKER_THREADS);
    private final Queue<Task> tasks = new ConcurrentLinkedQueue<>();
    private final int remotePort;
    
    public WritersProvider(int remotePort) {
        this.remotePort = remotePort;
    }

    public void start() {
        for (int i = 0; i < TOTAL_WORKER_THREADS; i++) {
            executorService.submit(new Worker());
        }
    }
    
    public void stop() {
        executorService.shutdown();
    }

    public List<Writer> newWriters(int total) throws Exception {
        List<Writer> clients = new ArrayList<Writer>();
        for (int i = 0; i < total; i++) {
            clients.add(newWriter());
        }
        return clients;
    }

    public Writer newWriter() throws Exception {
        Socket socket = new Socket("localhost", remotePort);
        return new Writer(socket);
    }

    public class Writer {

        Socket socket;

        public Writer(Socket socket) {
            this.socket = socket;
        }

        public void write(String textToWrite) {
            tasks.add(new Task(socket, textToWrite));
        }
        
        public void stop() throws Exception {
            socket.close();
            Thread.sleep(1);
        }
    }

    private class Worker implements Runnable {

        @Override
        public void run() {
            while (true) {
                Task task = tasks.poll();
                if (task != null) {
                    execute(task);
                }
            }
        }

        private void execute(Task task) {
            try {
                task.socket.getOutputStream().write(getBytes(task.textToWrite));
                Thread.sleep(1);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    private static class Task {

        final Socket socket;
        final String textToWrite;

        Task(Socket socket, String textToWrite) {
            this.socket = socket;
            this.textToWrite = textToWrite;
        }

    }

}
