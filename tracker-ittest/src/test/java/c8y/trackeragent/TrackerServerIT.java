package c8y.trackeragent;

import static com.cumulocity.rest.representation.operation.DeviceControlMediaType.NEW_DEVICE_REQUEST;
import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import c8y.Position;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import c8y.trackeragent.exception.UnknownDeviceException;
import c8y.trackeragent.utils.ConfigUtils;
import c8y.trackeragent.utils.Devices;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.Reports;
import c8y.trackeragent.utils.TrackerConfiguration;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.devicebootstrap.NewDeviceRequestRepresentation;
import com.cumulocity.sdk.client.ResponseParser;
import com.cumulocity.sdk.client.RestConnector;

public class TrackerServerIT extends BaseIT {

    private static final boolean LOCAL_TEST = true;
    //split into two tests - one connecting to remote platform (functional test) and other starting server (integration test)  
    private static final int REMOTE_PORT = 40000;
    
    private static final String OLD_IMEI = Devices.IMEI_1;
    private static Random random = new Random();
    
    private RestConnector restConnector;
    private Server server;    
    private ExecutorService executor = Executors.newFixedThreadPool(1);
    private int port;
    
    @Before
    public void setup() throws IOException {
        if(LOCAL_TEST) {
            port = random.nextInt(20000) + 40000;
        } else {
            port = REMOTE_PORT;
        }
        TrackerConfiguration config = ConfigUtils.get().loadCommonConfiguration().setLocalPort(port);
        platform = createTrackerPlatform();
        restConnector = new RestConnector(platform.getPlatformParameters(), new ResponseParser());
        server = new Server(config);
        if(LOCAL_TEST) {
            server.init();
            executor.submit(server);
        }
    }
    
    @Test
    public void shouldChangeDeviceLocation() throws Exception {
        getTrackerDevice(OLD_IMEI).setPosition(Positions.ZERO);
        byte[] report = Reports.getTelicReportBytes(OLD_IMEI, Positions.SAMPLE_1);
        writeToSocket(report);
        
        Thread.sleep(1000);
        Position actualPosition = getTrackerDevice(OLD_IMEI).getPosition();
        Positions.assertEqual(actualPosition, Positions.SAMPLE_1);
    }
    
    @Test
    public void shouldBootstrapNewDeviceAndThenChangeItsLocation() throws Exception {
        String imei = Devices.randomImei();
        createNewDeviceRequest(imei);
        byte[] report = Reports.getTelicReportBytes(imei, Positions.ZERO);
        
        //trigger bootstrap
        writeToSocket(report);
        Thread.sleep(5000);
        acceptNewDeviceRequest(imei);
        Thread.sleep(5000);
        
        if(LOCAL_TEST) {
            DeviceCredentials credentials = pollCredentials(imei);
            assertThat(credentials).isNotNull();  
        }
        
        //trigger regular report 
        report = Reports.getTelicReportBytes(imei, Positions.SAMPLE_1);
        writeToSocket(report);
        
        Thread.sleep(1000);
        TrackerDevice newDevice = getTrackerDevice(imei);
        Position actualPosition = newDevice.getPosition();
        Positions.assertEqual(actualPosition, Positions.SAMPLE_1);
    }
    
    @After
    public void tearDown() throws IOException {
        if(LOCAL_TEST) {
            executor.shutdownNow();
        }
    }

    private DeviceCredentials pollCredentials(String imei) throws InterruptedException {
        try {
            return DeviceCredentialsRepository.get().getCredentials(imei);
        } catch (UnknownDeviceException uex) {
            Thread.sleep(5000);
            return DeviceCredentialsRepository.get().getCredentials(imei);
        }
    }

    private void writeToSocket(byte[] bis) throws Exception {
        Socket socket = new Socket("localhost", port);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(bis);
        outputStream.close();
    }
    
    private void createNewDeviceRequest(String deviceId) {
        NewDeviceRequestRepresentation representation = new NewDeviceRequestRepresentation();
        representation.setId(deviceId);
        restConnector.post(newDeviceRequestsUri(), NEW_DEVICE_REQUEST, representation);
    }

    private String newDeviceRequestsUri() {
        return host + "/devicecontrol/newDeviceRequests";
    }

    private String newDeviceRequestUri(String deviceId) {
        return newDeviceRequestsUri() + "/" + deviceId;
    }
    
    private void acceptNewDeviceRequest(String deviceId) {
        NewDeviceRequestRepresentation representation = new NewDeviceRequestRepresentation();
        representation.setStatus("ACCEPTED");
        restConnector.put(newDeviceRequestUri(deviceId), NEW_DEVICE_REQUEST, representation);
    }
    
    private TrackerDevice getTrackerDevice(String imei) {
        DeviceManagedObject deviceManagedObject = new DeviceManagedObject(platform);
        GId agentId = deviceManagedObject.getAgentId();
        return new TrackerDevice(platform, agentId, imei);
    }

}
