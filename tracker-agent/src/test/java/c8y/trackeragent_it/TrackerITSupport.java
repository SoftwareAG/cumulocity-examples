package c8y.trackeragent_it;

import static com.cumulocity.model.authentication.CumulocityCredentials.Builder.cumulocityCredentials;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cumulocity.agent.server.context.DeviceContext;
import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.agent.server.repository.InventoryRepository;
import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.sdk.client.PlatformImpl;

import c8y.trackeragent.TrackerAgent;
import c8y.trackeragent.TrackerPlatform;
import c8y.trackeragent.configuration.ConfigUtils;
import c8y.trackeragent.configuration.TrackerConfiguration;
import c8y.trackeragent.device.TrackerDevice;
import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.service.AlarmMappingService;
import c8y.trackeragent.service.AlarmType;
import c8y.trackeragent.utils.ByteHelper;
import c8y.trackeragent.utils.message.TrackerMessage;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ITConfiguration.class)
public abstract class TrackerITSupport {

    private static Logger logger = LoggerFactory.getLogger(TrackerITSupport.class);

    @Value("${C8Y.tenant}")
    private String tenant;

    @Value("${C8Y.username}")
    private String username;

    @Value("${C8Y.password}")
    private String password;

    @Value("${tracker-agent.host}")
    private String trackerAgentHost;

    @Autowired
    protected TrackerConfiguration trackerAgentConfig;

    @Autowired
    protected TrackerAgent trackerAgent;

    @Autowired
    protected AlarmMappingService alarmMappingService;

    @Autowired
    protected DeviceContextService contextService;

    @Autowired
    protected InventoryRepository inventoryRepository;

    @Autowired
    protected DeviceCredentialsRepository deviceCredentialsRepository;

    protected TrackerPlatform trackerPlatform;
    protected TestConfiguration testConfig;
    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    protected Bootstraper bootstraper;
    protected SocketWriter socketWriter;

    @BeforeClass
    public static void baseClassSetup() throws IOException {
        clearPersistedDevices();
    }

    @Before
    public void baseSetUp() throws Exception {
        Thread.sleep(200);// avoid address already in use error
        testConfig = getTestConfig();
        System.out.println(testConfig);
        System.out.println(trackerAgentConfig);
        trackerPlatform = createTrackerPlatform();

        socketWriter = new SocketWriter(trackerAgentConfig, testConfig, getTrackerProtocol());

        bootstraper = new Bootstraper(trackerAgentConfig, testConfig, contextService, deviceCredentialsRepository, socketWriter);
        bootstraper.deleteExistingAgentRequest();
    }

    @After
    public void baseTearDown() throws IOException {
        executor.shutdownNow();
        socketWriter.destroySockets();
    }

    protected abstract TrackingProtocol getTrackerProtocol();

    private static void clearPersistedDevices() throws IOException {
        String filePath = "/etc/tracker-agent/" + ConfigUtils.DEVICES_FILE_NAME;
        File devicesFile = new File(filePath);
        FileUtils.deleteQuietly(devicesFile);
        devicesFile.createNewFile();
    }

    protected TrackerPlatform createTrackerPlatform() {
        CumulocityCredentials credentials = cumulocityCredentials(testConfig.getC8yUser(), testConfig.getC8yPassword())
                .withTenantId(testConfig.getC8yTenant()).build();
        PlatformImpl platform = new PlatformImpl(trackerAgentConfig.getPlatformHost(), credentials);
        return new TrackerPlatform(platform);
    }

    protected String writeInNewConnection(TrackerMessage... deviceMessages) throws Exception {
        return socketWriter.writeInNewConnection(deviceMessages);
    }

    protected TrackerDevice getTrackerDevice(String imei) {
        return trackerAgent.getOrCreateTrackerDevice(imei);
    }

    protected void bootstrapDevice(String imei, TrackerMessage deviceMessage) throws Exception {
        bootstraper.bootstrapDevice(imei, deviceMessage);
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

    protected AlarmRepresentation findAlarm(String imei, AlarmType alarmType) {
        String type = alarmMappingService.getType(alarmType.name());
        return getTrackerDevice(imei).findActiveAlarm(type);
    }

}
