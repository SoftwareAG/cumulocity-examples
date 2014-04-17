package c8y.trackeragent;

import static com.cumulocity.model.authentication.CumulocityCredentials.Builder.cumulocityCredentials;
import static com.cumulocity.rest.representation.operation.DeviceControlMediaType.NEW_DEVICE_REQUEST;
import static java.lang.Integer.parseInt;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;

import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import c8y.trackeragent.exception.UnknownDeviceException;
import c8y.trackeragent.utils.ConfigUtils;
import c8y.trackeragent.utils.TrackerConfiguration;

import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.devicebootstrap.NewDeviceRequestRepresentation;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.ResponseParser;
import com.cumulocity.sdk.client.RestConnector;

public abstract class TrackerITSupport {

    protected static final boolean REMOTE = false;
    protected static final boolean LOCAL = true;
    private static final Random random = new Random();

    private final boolean local;
    protected TrackerPlatform testPlatform;
    protected TrackerConfiguration trackerAgentConfig;
    protected TestConfiguration testConfig;
    protected RestConnector restConnector;
    private final ExecutorService executor = Executors.newFixedThreadPool(1);
    private Server server;
    private Socket socket;

    public TrackerITSupport() {
        this(LOCAL);
    }

    public TrackerITSupport(boolean local) {
        this.local = local;
    }

    @Before
    public void baseSetUp() throws IOException {
        testConfig = getTestConfig(local);
        System.out.println(testConfig);
        trackerAgentConfig = getPlatformConfiguration();
        System.out.println(trackerAgentConfig);
        if (local) {
            clearPersistedDevices();
        }
        testPlatform = createTrackerPlatform();
        restConnector = new RestConnector(testPlatform.getPlatformParameters(), new ResponseParser());
        if (local) {
            server = new Server(trackerAgentConfig);
            server.init();
            executor.submit(server);
        }
    }

    private void clearPersistedDevices() throws IOException {
        String filePath = ConfigUtils.get().getConfigFilePath(DeviceCredentialsRepository.SOURCE_FILE);
        File devicesFile = new File(filePath);
        FileUtils.deleteQuietly(devicesFile);
        devicesFile.createNewFile();
    }

    @After
    public void baseTearDown() throws IOException {
        if (local) {
            executor.shutdownNow();
        }
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    private TrackerConfiguration getPlatformConfiguration() {
        return ConfigUtils.get().loadCommonConfiguration()
                .setLocalPort(testConfig.getTrackerAgentPort()).setPlatformHost(testConfig.getC8yHost());
    }

    protected TrackerPlatform createTrackerPlatform() {
        //@formatter:off
        CumulocityCredentials credentials = cumulocityCredentials(
                testConfig.getC8yUser(),
                testConfig.getC8yPassword())
                .withTenantId(testConfig.getC8yTenant())
                .build();
        //@formatter:on
        PlatformImpl platform = new PlatformImpl(testConfig.getC8yHost(), credentials);
        return new TrackerPlatform(platform);
    }

    protected DeviceCredentials pollCredentials(String imei) throws InterruptedException {
        try {
            return DeviceCredentialsRepository.get().getCredentials(imei);
        } catch (UnknownDeviceException uex) {
            Thread.sleep(5000);
            return DeviceCredentialsRepository.get().getCredentials(imei);
        }
    }

    protected void writeToSocket(byte[] bis) throws Exception {
        String socketHost = testConfig.getTrackerAgentHost();
        int socketPort = testConfig.getTrackerAgentPort();
        try {
            socket = new Socket(socketHost, socketPort);
        } catch (IOException ex) {
            System.out.println("Cant connect to socket, host = " + socketHost + ", port = " + socketPort);
            throw ex;
        }
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(bis);
        outputStream.close();
    }

    protected void createNewDeviceRequest(String deviceId) {
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
        NewDeviceRequestRepresentation representation = new NewDeviceRequestRepresentation();
        representation.setStatus("ACCEPTED");
        restConnector.put(newDeviceRequestUri(deviceId), NEW_DEVICE_REQUEST, representation);
    }

    protected TrackerDevice getTrackerDevice(String imei) {
        DeviceManagedObject deviceManagedObject = new DeviceManagedObject(testPlatform);
        GId agentId = deviceManagedObject.getAgentId();
        return new TrackerDevice(testPlatform, agentId, imei);
    }
    

    private static TestConfiguration getTestConfig(boolean local) {
        String fileName = local ? "test-local.properties" : "test-remote.properties"; 
        String testFilePath = ConfigUtils.get().getConfigFilePath(fileName);
        Properties props = ConfigUtils.get().getProperties(testFilePath);
        //@formatter:off
        return new TestConfiguration()
            .setC8yHost(props.getProperty("c8y.host"))
            .setC8yTenant(props.getProperty("c8y.tenant"))
            .setC8yUser(props.getProperty("c8y.user"))
            .setC8yPassword(props.getProperty("c8y.password"))
            .setTrackerAgentHost(local ? "localhost" : props.getProperty("tracker-agent.host"))
            .setTrackerAgentPort(local ? randomPort() : parseInt(props.getProperty("tracker-agent.port")));
        //@formatter:on            
    }
    
    private static int randomPort() {
        return random.nextInt(20000) + 40000;
    }


}
