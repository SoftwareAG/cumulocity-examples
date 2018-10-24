package c8y.remoteaccess.tunnel;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class DeviceSocketClient implements Closeable {

    private final String host;

    private final int port;

    private Socket clientSocket = null;

    private DataOutputStream toHostOutputStream = null;

    private DataInputStream fromHostInputStream = null;

    public DeviceSocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void sendMessage(byte[] data) throws IOException {
        if (toHostOutputStream == null) {
            throw new IllegalStateException("Not connected to the host!");
        }
        toHostOutputStream.write(data);
    }

    public void connect() throws IOException {
        clientSocket = new Socket(host, port);
        toHostOutputStream = new DataOutputStream(clientSocket.getOutputStream());
        fromHostInputStream = new DataInputStream(clientSocket.getInputStream());
    }

    public int read(byte[] buffer) throws IOException {
        return fromHostInputStream.read(buffer);
    }

    @Override
    public void close() throws IOException {
        if (fromHostInputStream != null) {
            fromHostInputStream.close();
        }
        if (toHostOutputStream != null) {
            toHostOutputStream.close();
        }
        if (clientSocket != null) {
            clientSocket.close();
        }
    }

}
