package c8y.remoteaccess.tunnel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class VncSocketClient {

    private final String host;

    private final int port;

    private Socket clientSocket = null;

    private DataOutputStream toVnc = null;

    private DataInputStream fromVnc = null;

    public VncSocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void sendMessage(byte[] data) throws IOException {
        if (toVnc == null) {
            throw new IllegalStateException("Not connect to VNC");
        }
        toVnc.write(data);
    }

    public void connect() throws UnknownHostException, IOException {
        clientSocket = new Socket(host, port);
        toVnc = new DataOutputStream(clientSocket.getOutputStream());
        fromVnc = new DataInputStream(clientSocket.getInputStream());
    }

    public int read(byte[] buffer) throws IOException {
        return fromVnc.read(buffer);
    }

    public void close() throws IOException {
        if (fromVnc != null) {
            fromVnc.close();
        }
        if (toVnc != null) {
            toVnc.close();
        }
        if (clientSocket != null) {
            clientSocket.close();
        }
    }

    public boolean isConnected() {
        return clientSocket.isConnected();
    }
}
