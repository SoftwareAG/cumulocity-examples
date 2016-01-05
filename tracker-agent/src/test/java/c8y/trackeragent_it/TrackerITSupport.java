package c8y.trackeragent_it;

import static com.cumulocity.model.authentication.CumulocityCredentials.Builder.cumulocityCredentials;
import static com.cumulocity.rest.representation.operation.DeviceControlMediaType.NEW_DEVICE_REQUEST;
import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import c8y.trackeragent.DeviceManagedObject;
import c8y.trackeragent.TrackerDevice;
import c8y.trackeragent.TrackerPlatform;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import c8y.trackeragent.exception.UnknownDeviceException;
import c8y.trackeragent.utils.ConfigUtils;
import c8y.trackeragent.utils.TrackerConfiguration;
import c8y.trackeragent.utils.message.TrackerMessage;

import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.devicebootstrap.NewDeviceRequestRepresentation;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.ResponseParser;
import com.cumulocity.sdk.client.RestConnector;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ITConfiguration.class)
public abstract class TrackerITSupport {
    
    private static Logger logger = LoggerFactory.getLogger(TrackerITSupport.class);
    
    @Value("${c8y.tenant}")
    private String tenant;
    @Value("${c8y.username}")
    private String username;
    @Value("${c8y.password}")
    private String password;
    @Value("${tracker-agent.host}")
    private String trackerAgentHost;
    
    @Autowired
    protected TrackerConfiguration trackerAgentConfig;
    
    private int socketPort;
    protected TrackerPlatform testPlatform;
    protected TestConfiguration testConfig;
    protected RestConnector restConnector;
    private final ExecutorService executor = Executors.newFixedThreadPool(1);
    private final Collection<Socket> sockets = new HashSet<Socket>();

    @Before
    public void baseSetUp() throws Exception {
        Thread.sleep(200);//avoid address already in use error 
        testConfig = getTestConfig();
        System.out.println(testConfig);
        System.out.println(trackerAgentConfig);
        socketPort = trackerAgentConfig.getLocalPort();
        if (isLocalTrackerTest()) {
            clearPersistedDevices();
        }
        testPlatform = createTrackerPlatform();
        restConnector = new RestConnector(testPlatform.getPlatformParameters(), new ResponseParser());
    }

    private boolean isLocalTrackerTest() {
        return testConfig.getTrackerAgentHost().equals("localhost");
    }

    private void clearPersistedDevices() throws IOException {
        String filePath = ConfigUtils.get().getConfigFilePath(DeviceCredentialsRepository.SOURCE_FILE);
        File devicesFile = new File(filePath);
        FileUtils.deleteQuietly(devicesFile);
        devicesFile.createNewFile();
    }

    @After
    public void baseTearDown() throws IOException {
        if (isLocalTrackerTest()) {
            executor.shutdownNow();
        }
        destroySockets();
    }

    private void destroySockets() throws IOException {
        for (Socket socket : sockets) {
            if (!socket.isClosed()) {
                socket.close();
            }
        }
        sockets.clear();
    }

    protected TrackerPlatform createTrackerPlatform() {
        CumulocityCredentials credentials = cumulocityCredentials(testConfig.getC8yUser(),testConfig.getC8yPassword())
                .withTenantId(testConfig.getC8yTenant()).build();
        PlatformImpl platform = new PlatformImpl(trackerAgentConfig.getPlatformHost(), credentials);
        return new TrackerPlatform(platform);
    }

    protected DeviceCredentials pollCredentials(String imei) throws InterruptedException {
        try {
            Thread.sleep(5000);
            return DeviceCredentialsRepository.get().getCredentials(imei);
        } catch (UnknownDeviceException uex) {
            Thread.sleep(5000);
            return DeviceCredentialsRepository.get().getCredentials(imei);
        }
    }

    protected String writeInNewConnection(Socket socket, byte[] bis) throws Exception {
        OutputStream out = socket.getOutputStream();
        out.write(bis);
        out.flush();
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
    
    protected String writeInNewConnection(TrackerMessage... deviceMessages) throws Exception {
        TrackerMessage sum = deviceMessages[0];
        for (int index = 1; index < deviceMessages.length; index++) {
            sum = sum.appendReport(deviceMessages[index]);
        }
        logger.info("Send message: {}", sum);
        return writeInNewConnection(newSocket(), sum.asBytes());
    }
    
    @Deprecated//use DeviceMessage object
    protected String writeInNewConnection(byte[] bis) throws Exception {
        return writeInNewConnection(newSocket(), bis);
    }
    
    protected Socket newSocket() throws IOException {
        destroySockets();
        String socketHost = testConfig.getTrackerAgentHost();
        try {
            Socket socket = new Socket(socketHost, socketPort);
            socket.setSoTimeout(6000);
            sockets.add(socket);
            return socket;
        } catch (IOException ex) {
            System.out.println("Cant connect to socket, host = " + socketHost + ", port = " + socketPort);
            throw ex;
        }
    }

    protected synchronized void createNewDeviceRequest(String deviceId) {
        NewDeviceRequestRepresentation representation = new NewDeviceRequestRepresentation();
        representation.setId(deviceId);
        restConnector.post(newDeviceRequestsUri(), NEW_DEVICE_REQUEST, representation);
    }

    protected String newDeviceRequestsUri() {
        return testPlatform.getHost() + "devicecontrol/newDeviceRequests";
    }

    protected String newDeviceRequestUri(String deviceId) {
        return newDeviceRequestsUri() + "/" + deviceId;
    }

    protected void acceptNewDeviceRequest(String deviceId) {
        try {
            NewDeviceRequestRepresentation representation = new NewDeviceRequestRepresentation();
            representation.setStatus("ACCEPTED");
            restConnector.put(newDeviceRequestUri(deviceId), NEW_DEVICE_REQUEST, representation);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected TrackerDevice getTrackerDevice(String imei) {
        DeviceManagedObject deviceManagedObject = new DeviceManagedObject(testPlatform);
        GId agentId = deviceManagedObject.getAgentId();
        return new TrackerDevice(testPlatform, trackerAgentConfig, agentId, imei);
    }
    
    protected void bootstrap(String imei, TrackerMessage deviceMessage) throws UnsupportedEncodingException, Exception, InterruptedException {
        bootstrap(imei, deviceMessage.asBytes());
    }
    
    @Deprecated//use DeviceMessage
    protected void bootstrap(String imei, byte[] report) throws UnsupportedEncodingException, Exception, InterruptedException {        
        createNewDeviceRequest(imei);
        // WAITING_FOR_CONNECTION status
        
        writeInNewConnection(report);        
        // PENDING_ACCEPTANCE status
        Thread.sleep(5000);
        
        logger.info("accept request for imei " + imei);
        acceptNewDeviceRequest(imei);
        // ACCEPTED status
        
        DeviceCredentials credentials = pollCredentials(imei);
        assertThat(credentials).isNotNull();
    }    

    private TestConfiguration getTestConfig() {
        //@formatter:off
        return new TestConfiguration()
            .setC8yTenant(tenant)
            .setC8yUser(username)
            .setC8yPassword(password)
            .setTrackerAgentHost(trackerAgentHost);
        //@formatter:on            
    }
}
