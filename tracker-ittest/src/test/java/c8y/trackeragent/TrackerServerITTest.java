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
import org.junit.Ignore;
import org.junit.Test;

import c8y.Position;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import c8y.trackeragent.exception.UnknownDeviceException;
import c8y.trackeragent.utils.Devices;
import c8y.trackeragent.utils.Positions;
import c8y.trackeragent.utils.Reports;
import c8y.trackeragent.utils.TrackerContext;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.devicebootstrap.NewDeviceRequestRepresentation;
import com.cumulocity.sdk.client.ResponseParser;
import com.cumulocity.sdk.client.RestConnector;

public class TrackerServerITTest {

    private static final boolean LOCAL_TEST = true;
    //split into two tests - one connecting to remote platform (functional test) and other starting server (integration test)  
    private static final int REMOTE_PORT = 40000;
    
    private static final String NEW_IMEI = "100000";//use random imei
    private static final String OLD_IMEI = Devices.IMEI_1;
    private static Random random = new Random();
    
    private TrackerPlatform platform;
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
        TrackerContext.get().setLocalSocketPort(port);
        platform = TrackerContext.get().getPlatform("vaillant");
        restConnector = new RestConnector(platform.getPlatformParameters(), new ResponseParser());
        server = new Server();
        if(LOCAL_TEST) {
            server.init();
            executor.submit(server);
        }
    }

    @After
    public void tearDown() throws IOException {
        deleteNewDeviceRequest(NEW_IMEI);
        if(LOCAL_TEST) {
            executor.shutdownNow();
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
    @Ignore//change NEW_IMEI property to not used value
    public void shouldBootstrapNewDeviceAndThenChangeItsLocation() throws Exception {
        createNewDeviceRequest(NEW_IMEI);
        byte[] report = Reports.getTelicReportBytes(NEW_IMEI, Positions.ZERO);
        
        //trigger bootstrap
        writeToSocket(report);
        Thread.sleep(5000);
        acceptNewDeviceRequest(NEW_IMEI);
        Thread.sleep(5000);
        
        if(LOCAL_TEST) {
            DeviceCredentials credentials = pollCredentials();
            assertThat(credentials).isNotNull();  
        }
        
        //trigger regular report 
        report = Reports.getTelicReportBytes(NEW_IMEI, Positions.SAMPLE_1);
        writeToSocket(report);
        
        Thread.sleep(1000);
        TrackerDevice newDevice = getTrackerDevice(NEW_IMEI);
        Position actualPosition = newDevice.getPosition();
        Positions.assertEqual(actualPosition, Positions.SAMPLE_1);
    }

    private DeviceCredentials pollCredentials() throws InterruptedException {
        try {
            return DeviceCredentialsRepository.get().getCredentials(NEW_IMEI);
        } catch (UnknownDeviceException uex) {
            Thread.sleep(5000);
            return DeviceCredentialsRepository.get().getCredentials(NEW_IMEI);
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
    
    private TrackerDevice getTrackerDevice(String imei) {
        DeviceManagedObject deviceManagedObject = new DeviceManagedObject(platform);
        GId agentId = deviceManagedObject.getAgentId();
        return new TrackerDevice(platform, agentId, imei);
    }

}
