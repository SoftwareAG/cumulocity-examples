package c8y.trackeragent.nioserver;

import static java.util.Arrays.asList;
import static java.util.Collections.synchronizedList;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.nioserver.NioServerEvent.ReadDataEvent;

public class NioServerTestSupport {
    
    private static final Logger logger = LoggerFactory.getLogger(NioServerTestSupport.class);

    protected static final int PORT = 5100;
    protected static final Charset CHARSET = Charset.forName("US-ASCII");
    
    private NioServer server;
    private final ExecutorService executorService = newFixedThreadPool(100);
    protected CountDownLatch reportExecutorLatch;
    protected final List<ReaderWorkerExecutorImpl> executors = synchronizedList(new ArrayList<ReaderWorkerExecutorImpl>());
    
    @Before
    public void before() throws Exception {
        reportExecutorLatch = new CountDownLatch(0);
        NioServerEventHandler eventHandler = new NioServerEventHandler(new ReaderWorkerExecutorFactoryImpl());
        eventHandler.init();
        server = new NioServer(null, PORT, eventHandler);
        executorService.execute(server);
    }
    
    @After
    public void after() throws IOException {
        server.close();
    }
    
    protected SocketWriter newWriter() throws Exception {
        SocketWriter writer = new SocketWriter();
        executorService.execute(writer);
        return writer;
    }
    
    protected void assertThatReportsOnSocket(String... reports) {
        Object[] expected = new Object[reports.length];
        for (int index = 0; index < reports.length; index++) {
            expected[index] = new ReaderWorkerExecutorImpl(reports[index]);
        }
        assertThat(executors).contains(expected);
    }
    
    protected class SocketWriter implements Runnable {

        volatile Deque<String> toW = new ArrayDeque<String>();
        Socket client;

        public SocketWriter() throws Exception {
            client = new Socket("localhost", PORT);
        }

        void push(String text) throws Exception {
            toW.addLast(text);
            Thread.sleep(1);
        }
                
        void stop() throws Exception {
            client.close();
            Thread.sleep(1);
        }

        @Override
        public void run() {
            while (true) {
                if (!toW.isEmpty()) {
                    String toWrite = toW.removeFirst();
                    write(toWrite);
                }
            }
        }

        private void write(String toWrite) {
            for (byte b : toWrite.getBytes(CHARSET)) {
                try {
                    client.getOutputStream().write(b);
                    Thread.sleep(1);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }

    }
    
    protected class ReaderWorkerExecutorFactoryImpl implements ReaderWorkerExecutorFactory {
        
        @Override
        public ReaderWorkerExecutor create(ReadDataEvent readData) {
            ReaderWorkerExecutorImpl result = new ReaderWorkerExecutorImpl();
            logger.info("Created executor for data " + new String(readData.getData(), CHARSET));
            executors.add(result);
            logger.info("Total executors " + executors.size());
            return result;
        }
    }
    
    protected class ReaderWorkerExecutorImpl implements ReaderWorkerExecutor {
        
        private final List<String> processed;
        
        public ReaderWorkerExecutorImpl(String... processed) {
            this.processed = new ArrayList<String>();
            this.processed.addAll(asList(processed));
        }

        @Override
        public void execute(String report) {
            logger.info("Handled report: \'{}\'", report);
            processed.add(report);
            reportExecutorLatch.countDown();
        }
        
        @Override
        public String getReportSeparator() {
            return ";";
        }
        
        public List<String> getProcessed() {
            return processed;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((processed == null) ? 0 : processed.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ReaderWorkerExecutorImpl other = (ReaderWorkerExecutorImpl) obj;
            if (processed == null) {
                if (other.processed != null)
                    return false;
            } else if (!processed.equals(other.processed))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return processed.toString();
        }
    }

    
}
