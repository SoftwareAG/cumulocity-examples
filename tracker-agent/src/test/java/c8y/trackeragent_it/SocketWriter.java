package c8y.trackeragent_it;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.configuration.TrackerConfiguration;
import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.utils.ByteHelper;
import c8y.trackeragent.utils.message.TrackerMessage;

public class SocketWriter {
    
    private static Logger logger = LoggerFactory.getLogger(SocketWriter.class);
    
    private final Collection<Socket> sockets = new HashSet<Socket>();
    private final TestConfiguration testConfig;
    private final TrackerConfiguration trackerAgentConfig;
    private final TrackingProtocol trackingProtocol;
    
    public SocketWriter(TrackerConfiguration trackerAgentConfig, TestConfiguration testConfig, TrackingProtocol trackingProtocol) {
        this.trackerAgentConfig = trackerAgentConfig;
        this.testConfig = testConfig;
        this.trackingProtocol = trackingProtocol;
    }

    public Socket writeInNewConnectionAndKeepOpen(byte[] bis) throws Exception {
        Socket socket = newSocket();
        OutputStream out = socket.getOutputStream();
        out.write(bis);
        out.flush();
        Thread.sleep(1000);
        return socket;
    }
    
    public String writeInNewConnection(TrackerMessage... deviceMessages) throws Exception {
        TrackerMessage sum = deviceMessages[0];
        for (int index = 1; index < deviceMessages.length; index++) {
            sum = sum.appendReport(deviceMessages[index]);
        }
        logger.info("Send message: {}", sum);
        Socket newSocket = newSocket();
        return writeInNewConnection(newSocket, sum.asBytes());
    }
    
    public void destroySockets() throws IOException {
        for (Socket socket : sockets) {
            if (!socket.isClosed()) {
                socket.close();
            }
        }
        sockets.clear();
    }
    
    private String writeInNewConnection(Socket socket, byte[] bis) throws Exception {
        OutputStream out = socket.getOutputStream();
        System.out.println("Write >> " + ByteHelper.getString(bis));
        out.write(bis);
        out.flush();
        Thread.sleep(1000);
        String response = readSocketResponse(socket);
        socket.close();
        return response;
    }

    private String readSocketResponse(Socket socket) throws Exception {
        InputStream in = socket.getInputStream();
        byte[] bytes = new byte[0];
        try {
            int b;
            while ((b = in.read()) >= 0) {
                bytes = ArrayUtils.add(bytes, (byte) b);
            }
        } catch (SocketTimeoutException stex) {
            // nothing to do, simply end of input handled
        } 
        return bytes.length == 0 ? null : new String(bytes, "US-ASCII");
    }
    
    private Socket newSocket() throws IOException {
        destroySockets();
        String socketHost = testConfig.getTrackerAgentHost();
        try {
            Socket socket = new Socket(socketHost, getLocalPort());
            socket.setSoTimeout(2000);
            sockets.add(socket);
            return socket;
        } catch (IOException ex) {
            System.out.println("Cant connect to socket, host = " + socketHost + ", port = " + getLocalPort());
            throw ex;
        }
    }
    
    private final int getLocalPort() {
        return trackerAgentConfig.getPort(trackingProtocol);
    }
}
