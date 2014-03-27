package c8y.trackeragent;

import static com.cumulocity.rest.representation.operation.DeviceControlMediaType.NEW_DEVICE_REQUEST;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import c8y.Position;
import c8y.trackeragent.repository.DeviceCredentials;
import c8y.trackeragent.repository.DeviceCredentialsRepository;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.Reports;
import c8y.trackeragent.utils.TrackerContext;

import com.cumulocity.rest.representation.devicebootstrap.NewDeviceRequestRepresentation;
import com.cumulocity.sdk.client.ResponseParser;
import com.cumulocity.sdk.client.RestConnector;

public class TrackerServerITTest {

    private static final String NEW_IMEI = "newImei9";

    private static Random random = new Random();
    
    private TrackerPlatform platform;
    private TrackerContext trackerContext = TrackerContext.get();
    private RestConnector restConnector;
    private Server server;    
    private TrackerDevice trackerDevice;
    private ExecutorService executor = Executors.newFixedThreadPool(1);
    private int port;

    
    @Before
    public void setup() throws IOException {
        port = random.nextInt(20000) + 40000;
        trackerContext.setLocalSocketPort(port);
        platform = trackerContext.getPlatform("vaillant");
        restConnector = new RestConnector(platform.getPlatformParameters(), new ResponseParser());

        server = new Server();
        server.init();
        trackerDevice = server.getTrackerAgent().getOrCreateTrackerDevice(TelicLocationReportTest.IMEI);
        trackerDevice.setPosition(Positions.ZERO);
        
        executor.submit(server);
    }

    @After
    public void tearDown() throws IOException {
        deleteNewDeviceRequest(NEW_IMEI);
        executor.shutdownNow();
    }
    
    @Test
    public void shouldChangeDeviceLocation() throws Exception {
        writeToSocket(TelicLocationReportTest.getTelicReportBytes());
        
        Thread.sleep(1000);
        Position actualPosition = trackerDevice.getPosition();
        Positions.assertEqual(actualPosition, TelicLocationReportTest.POS);
    }
    
    @Test
    public void shouldBootstrapNewDevice() throws Exception {
        createNewDeviceRequest(NEW_IMEI);
        byte[] report = Reports.getTelicReportBytes(NEW_IMEI);
        
        writeToSocket(report);

        Thread.sleep(5000);
        
        acceptNewDeviceRequest(NEW_IMEI);
        
        Thread.sleep(5000);
        
        DeviceCredentials credentials = DeviceCredentialsRepository.instance().getCredentials(NEW_IMEI);
        
    }

    private void writeToSocket(byte[] bis) throws Exception {
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = new Socket(host.getHostName(), port);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(bis);
        outputStream.close();
        Thread.sleep(100);
    }
    
    private void createNewDeviceRequest(String deviceId) {
        NewDeviceRequestRepresentation representation = new NewDeviceRequestRepresentation();
        representation.setId(deviceId);
        restConnector.post(newDeviceRequestsUri(), NEW_DEVICE_REQUEST, representation);
    }

    private void deleteNewDeviceRequest(String deviceId) {
        try {
            restConnector.delete(newDeviceRequestUri(deviceId));
        } catch (Exception ex) {
        }
    }

    private String newDeviceRequestsUri() {
        return platform.getHost() + "devicecontrol/newDeviceRequests";
    }

    private String newDeviceRequestUri(String deviceId) {
        return newDeviceRequestsUri() + "/" + deviceId;
    }
    
    private void acceptNewDeviceRequest(String deviceId) {
        NewDeviceRequestRepresentation representation = new NewDeviceRequestRepresentation();
        representation.setStatus("ACCEPTED");
        restConnector.put(newDeviceRequestUri(deviceId), NEW_DEVICE_REQUEST, representation);
    }

}
