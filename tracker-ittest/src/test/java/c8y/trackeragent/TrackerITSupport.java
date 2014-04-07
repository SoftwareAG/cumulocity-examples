package c8y.trackeragent;

import static com.cumulocity.model.authentication.CumulocityCredentials.Builder.cumulocityCredentials;
import static com.cumulocity.rest.representation.operation.DeviceControlMediaType.NEW_DEVICE_REQUEST;

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
    private static final int REMOTE_PORT = 40000;
    private static final Random random = new Random();
    
    private final boolean local;
    protected TrackerPlatform testPlatform;
    protected TrackerConfiguration config;
    protected RestConnector restConnector;
    private final ExecutorService executor = Executors.newFixedThreadPool(1);
    
    private Server server;
    private Socket socket;    
    
    public TrackerITSupport() {
        this(LOCAL);
    }

    public TrackerITSupport(boolean local) {
        this.local = local;
        if(local) {
            config = getLocalPlatformConfiguration();
        } else {
            config = getRemotePlatformConfiguration();
        }
    }

    @Before
    public void baseSetUp() throws IOException {
        if(local) {
            clearDevices();
        }
        testPlatform = createTrackerPlatform();        
        restConnector = new RestConnector(testPlatform.getPlatformParameters(), new ResponseParser());
        if(local) {
            server = new Server(config);
            server.init();
            executor.submit(server);
        }
    }
    
    private void clearDevices() throws IOException {
        String filePath = ConfigUtils.get().getConfigFilePath(DeviceCredentialsRepository.SOURCE_FILE);
        System.out.println(filePath);
        File devicesFile = new File(filePath);
        FileUtils.deleteQuietly(devicesFile);
        devicesFile.createNewFile();
    }

    @After
    public void baseTearDown() throws IOException {
        if(local) {
            executor.shutdownNow();
        }
        if(socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    private TrackerConfiguration getLocalPlatformConfiguration() {
        return getRemotePlatformConfiguration()
                .setLocalPort(randomPort())
                .setPlatformHost("http://localhost:8181");
    }
    private TrackerConfiguration getRemotePlatformConfiguration() {
        return ConfigUtils.get().loadCommonConfiguration()
                .setLocalPort(REMOTE_PORT);
    }
    
    private int randomPort() {
        return random.nextInt(20000) + 40000;
    }

    protected TrackerPlatform createTrackerPlatform() {
        String testFilePath = ConfigUtils.get().getConfigFilePath("test.properties");
        Properties testProperties = ConfigUtils.get().getProperties(testFilePath);
        CumulocityCredentials credentials = cumulocityCredentials(
                testProperties.getProperty("user"), testProperties.getProperty("password"))
                .withTenantId(testProperties.getProperty("tenant"))
                .build();
        PlatformImpl platform = new PlatformImpl(config.getPlatformHost(), credentials);
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
        socket = new Socket("localhost", config.getLocalPort());
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
        return config.getPlatformHost() + "/devicecontrol/newDeviceRequests";
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

}
