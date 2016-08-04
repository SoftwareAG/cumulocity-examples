package c8y.trackeragent_it;

import static com.cumulocity.model.authentication.CumulocityCredentials.Builder.cumulocityCredentials;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import c8y.trackeragent.protocol.TrackingProtocol;
import c8y.trackeragent.service.AlarmMappingService;
import c8y.trackeragent.service.AlarmType;
import c8y.trackeragent.utils.message.TrackerMessage;
import c8y.trackeragent_it.config.ServerConfiguration;
import c8y.trackeragent_it.config.TestConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServerConfiguration.class, TestConfiguration.class })
public abstract class TrackerITSupport {

    @Autowired
    protected TrackerConfiguration trackerAgentConfig;
    
    @Autowired
    protected TestSettings testSettings;

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

    protected Bootstraper bootstraper;
    protected SocketWriter socketWriter;

    @BeforeClass
    public static void baseClassSetup() throws IOException {
        clearPersistedDevices();
    }

    @Before
    public void baseSetUp() throws Exception {
        trackerPlatform = trackerPlatform(testSettings);
        Thread.sleep(200);// avoid address already in use error
        System.out.println(testSettings);
        System.out.println(trackerAgentConfig);
        socketWriter = new SocketWriter(testSettings, trackerAgentConfig.getPort(getTrackerProtocol()));
        bootstraper = new Bootstraper(trackerAgentConfig, testSettings, contextService, deviceCredentialsRepository, socketWriter);
        bootstraper.deleteExistingAgentRequest();
    }

    @After
    public void baseTearDown() throws IOException {
        socketWriter.destroySockets();
    }

    protected abstract TrackingProtocol getTrackerProtocol();

    private static void clearPersistedDevices() throws IOException {
        String filePath = "/etc/tracker-agent/" + ConfigUtils.DEVICES_FILE_NAME;
        File devicesFile = new File(filePath);
        FileUtils.deleteQuietly(devicesFile);
        devicesFile.createNewFile();
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

    protected AlarmRepresentation findAlarm(String imei, AlarmType alarmType) {
        String type = alarmMappingService.getType(alarmType.name());
        return getTrackerDevice(imei).findActiveAlarm(type);
    }
    
    private TrackerPlatform trackerPlatform(TestSettings testSettings) {
        CumulocityCredentials credentials = cumulocityCredentials(testSettings.getC8yUser(), testSettings.getC8yPassword())
                .withTenantId(testSettings.getC8yTenant()).build();
        PlatformImpl trackerPlatformImpl = new PlatformImpl(testSettings.getC8yHost(), credentials);
        return new TrackerPlatform(trackerPlatformImpl);
    }

}
