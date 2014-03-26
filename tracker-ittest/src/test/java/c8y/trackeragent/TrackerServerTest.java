package c8y.trackeragent;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.junit.Before;
import org.junit.Test;

import c8y.Position;
import c8y.trackeragent.utils.Positions;

public class TrackerServerTest {

    private Server server;
    private TrackerPlatform trackerPlatform;
    private TrackerDevice trackerDevice;

    @Before
    public void setup() throws IOException {
        server = new Server();
        server.init();
        trackerPlatform = server.getTrackerContext().getPlatforms().get(0);
        trackerDevice = server.getTrackerAgent().getOrCreateTrackerDevice(TelicLocationReportTest.IMEI);
        trackerDevice.setPosition(Positions.ZERO);
    }

    @Test
    public void shouldChangeDeviceLocation() throws Exception {
        Thread thread = new Thread(server);
        thread.start();        
        
        writeToSocket(TelicLocationReportTest.getTelicReportBytes());
        
        Thread.sleep(1000);
        Position actualPosition = trackerDevice.getPosition();
        Positions.assertEqual(actualPosition, TelicLocationReportTest.POS);
    }

    private void writeToSocket(byte[] bis) throws Exception {
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = new Socket(host.getHostName(), server.getTrackerContext().getLocalSocketPort());
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(bis);
        outputStream.close();
        Thread.sleep(100);
    }
}
