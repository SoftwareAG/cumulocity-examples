package c8y.trackeragent.utils;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static c8y.trackeragent.utils.ReportReader.Result;

import c8y.trackeragent.protocol.gl200.GL200Constants;

public class ReportReaderTest {

    private static final int LOCAL_PORT = 5123;
    private static final Charset CHARSET = Charset.forName("US-ASCII");
    public static final String REPORT1 = "field1|field2";
    public static final String REPORT2 = "field3|field4";

    private ServerSocket serverSocket;
    private Socket socket;
    private SocketWriter socketWriter;

    @Before
    public void before() throws Exception {
        serverSocket = new ServerSocket(LOCAL_PORT);
        socket = new Socket("localhost", LOCAL_PORT);
        socketWriter = new SocketWriter();
        Executors.newFixedThreadPool(1).execute(socketWriter);
    }

    @After
    public void after() throws IOException {
        if (serverSocket != null) {
            serverSocket.close();
        }
        if (socket != null) {
            socket.close();
        }
    }

    @Test
    public void shouldReadGL200Report() throws Exception {
        ReportReader reportReader = new ReportReader(serverSocket.accept().getInputStream(), GL200Constants.REPORT_SEP);
        socketWriter.push(REPORT1 + GL200Constants.REPORT_SEP + REPORT2 + GL200Constants.REPORT_SEP);
        
        assertThat(reportReader.readReport().getText()).isEqualTo(REPORT1);
        assertThat(reportReader.readReport().getText()).isEqualTo(REPORT2);
        assertThat(reportReader.readReport().getText()).isNull();
    }

    @Test
    public void shouldReadReport() throws Exception {
        ReportReader reportReader = new ReportReader(serverSocket.accept().getInputStream(), ';');

        socketWriter.push("hello;");

        assertThat(reportReader.readReport().getText()).isEqualTo("hello");
    }
    
    @Test
    public void shouldReadNullForEmptyReport() throws Exception {
        ReportReader reportReader = new ReportReader(serverSocket.accept().getInputStream(), ';');
        
        assertThat(reportReader.readReport().getText()).isNull();
    }
    
    @Test
    public void shouldReadNullForNoCompletedReport() throws Exception {
        ReportReader reportReader = new ReportReader(serverSocket.accept().getInputStream(), ';');
        
        socketWriter.push("hel");
        assertThat(reportReader.readReport().getText()).isNull();
        
        socketWriter.push("hello;");
        assertThat(reportReader.readReport().getText()).isEqualTo("hello");
    }
    
    private class SocketWriter implements Runnable {

        volatile Deque<String> toW = new ArrayDeque<String>();

        void push(String text) throws InterruptedException {
            toW.addLast(text);
            Thread.sleep(100);
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
                    socket.getOutputStream().write(b);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
