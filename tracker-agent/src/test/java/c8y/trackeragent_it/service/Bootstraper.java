package c8y.trackeragent_it.service;

import static com.cumulocity.model.authentication.CumulocityCredentials.Builder.cumulocityCredentials;
import static com.cumulocity.rest.representation.operation.DeviceControlMediaType.NEW_DEVICE_REQUEST;
import static org.fest.assertions.Assertions.assertThat;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.agent.server.context.DeviceContext;
import com.cumulocity.agent.server.context.DeviceContextService;
import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.rest.representation.devicebootstrap.NewDeviceRequestRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.ResponseParser;
import com.cumulocity.sdk.client.RestConnector;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.InventoryFilter;

import c8y.trackeragent.devicebootstrap.DeviceCredentials;
import c8y.trackeragent.devicebootstrap.DeviceCredentialsRepository;
import c8y.trackeragent.utils.message.TrackerMessage;
import c8y.trackeragent_it.SocketWriter;
import c8y.trackeragent_it.TestSettings;

public class Bootstraper {

    private static Logger logger = LoggerFactory.getLogger(Bootstraper.class);

    private final DeviceContextService contextService;
    private final DeviceCredentialsRepository deviceCredentialsRepository;
    private final Platform bootstrapPlatform;
    private final PlatformImpl trackerPlatform;
    private final TestSettings testSettings;
    private final RestConnector restConnector;
    private final SocketWriter socketWriter;
    private final InventoryApi inventoryApi;
    private Boolean isAgentBootstraped;

    public Bootstraper(
            // @formatter:off
            TestSettings testSettings,
            DeviceContextService contextService, 
            DeviceCredentialsRepository deviceCredentialsRepository, 
            SocketWriter socketWriter) {
            // @formatter:on
        this.testSettings = testSettings;
        this.contextService = contextService;
        this.deviceCredentialsRepository = deviceCredentialsRepository;
        this.bootstrapPlatform = createBootstrapPlatform();
        this.trackerPlatform = createTrackerPlatform();
        this.restConnector = new RestConnector(trackerPlatform, new ResponseParser());
        this.socketWriter = socketWriter;
        this.inventoryApi = this.trackerPlatform.getInventoryApi();
    }

    public Bootstraper(TestSettings testSettings, SocketWriter socketWriter) {
        this(testSettings, null, null, socketWriter);
    }

    public void bootstrapDevice(String imei, TrackerMessage deviceMessage) throws Exception {
        logger.info("Bootstrap: {}", imei);
        if (isAgentBootstraped()) {
            logger.info("Agent boostraped");
        } else {
            logger.info("Agent not boostraped");
            bootstrapAgent(deviceMessage);
        }
        createNewDeviceRequest(imei);
        Thread.sleep(1000);
        // WAITING_FOR_CONNECTION status

        socketWriter.writeInNewConnection(deviceMessage);
        Thread.sleep(1000);
        // PENDING_ACCEPTANCE status

        logger.info("accept request for imei: {}", imei);
        acceptNewDeviceRequest(imei);
        // ACCEPTED status

        socketWriter.writeInNewConnection(deviceMessage);
        Thread.sleep(1000);
        // Device credentials got

        if (deviceCredentialsRepository != null) {
            DeviceCredentials credentials = deviceCredentialsRepository.getDeviceCredentials(imei);
            logger.info("Created credentails: {}", credentials);
            assertThat(credentials).isNotNull();
            enterDeviceContext(imei);
        }
    }

    private boolean isAgentBootstraped() {
//        if (isAgentBootstraped != null) {
//            return isAgentBootstraped;
//        }
        InventoryFilter filter = new InventoryFilter().byType("c8y_TrackerAgent");
        isAgentBootstraped = !inventoryApi.getManagedObjectsByFilter(filter).get().getManagedObjects().isEmpty();
        return isAgentBootstraped;
    }

    public synchronized void bootstrapAgent(TrackerMessage deviceMessage)
            throws UnsupportedEncodingException, Exception, InterruptedException {
        logger.info("Boostrap agent");
        String id = bootstrapAgentRequestId();

        NewDeviceRequestRepresentation newDeviceRequest = new NewDeviceRequestRepresentation();
        newDeviceRequest.setId(id);
        try {
            bootstrapPlatform.getDeviceCredentialsApi().delete(newDeviceRequest);
        } catch (Exception ex) {

        }
        // agent request deleted

        createNewDeviceRequest(id);
        // WAITING_FOR_CONNECTION

        connectNewDeviceRequest(id);
        // PENDING_ACCEPTANCE

        acceptNewDeviceRequest(id);
        // ACCEPTED status
    }

    public synchronized void deleteExistingAgentRequest() {
        try {
            restConnector.delete(newDeviceRequestUri() + "/" + bootstrapAgentRequestId());
        } catch (Exception ex) {
            logger.info("OK, there is no legacy device request for tracker agent.");
        }
    }

    private String bootstrapAgentRequestId() {
        return "tracker-agent-" + testSettings.getC8yTenant();
    }

    private synchronized void createNewDeviceRequest(String deviceId) {
        NewDeviceRequestRepresentation newDeviceRequest = getNewDeviceRequest(deviceId);
        if (newDeviceRequest != null) {
            return;
        }
        NewDeviceRequestRepresentation representation = new NewDeviceRequestRepresentation();
        representation.setId(deviceId);
        restConnector.post(newDeviceRequestUri(), NEW_DEVICE_REQUEST, representation);
    }

    private NewDeviceRequestRepresentation getNewDeviceRequest(String deviceId) {
        try {
            return restConnector.get(newDeviceRequestUri(deviceId), NEW_DEVICE_REQUEST, NewDeviceRequestRepresentation.class);
        } catch (Exception ex) {
            return null;
        }
    }

    protected void connectNewDeviceRequest(String deviceId) throws Exception {
        try {
            bootstrapPlatform.getDeviceCredentialsApi().pollCredentials(deviceId);
            logger.info("Device with id {} connected.", deviceId);
        } catch (Exception ex) {
            logger.info("Device with id {} not connected.", deviceId);
        }
    }

    private String newDeviceRequestUri() {
        return testSettings.getC8yHost() + "/devicecontrol/newDeviceRequests";
    }

    private String newDeviceRequestUri(String deviceId) {
        return newDeviceRequestUri() + "/" + deviceId;
    }

    private void acceptNewDeviceRequest(String deviceId) {
        try {
            NewDeviceRequestRepresentation representation = new NewDeviceRequestRepresentation();
            representation.setStatus("ACCEPTED");
            restConnector.put(newDeviceRequestUri(deviceId), NEW_DEVICE_REQUEST, representation);
            logger.info("Device with id {} accepted.", deviceId);
        } catch (Exception ex) {
            logger.error("Device with id " + deviceId + " not accepted.", ex);
        }
    }

    private void enterDeviceContext(String imei) {
        DeviceCredentials deviceCredentials = deviceCredentialsRepository.getDeviceCredentials(imei);
        DeviceCredentials agentCredentials = deviceCredentialsRepository.getAgentCredentials(deviceCredentials.getTenant());
        DeviceContext deviceContext = new DeviceContext(agentCredentials);
        contextService.enterContext(deviceContext);
    }

    private PlatformImpl createBootstrapPlatform() {
        CumulocityCredentials credentials = cumulocityCredentials(testSettings.getBootstrapUser(), testSettings.getBootstrapPassword())
                .withTenantId(testSettings.getC8yTenant()).build();
        return new PlatformImpl(testSettings.getC8yHost(), credentials);
    }

    private PlatformImpl createTrackerPlatform() {
        CumulocityCredentials credentials = cumulocityCredentials(testSettings.getC8yUser(), testSettings.getC8yPassword())
                .withTenantId(testSettings.getC8yTenant()).build();
        return new PlatformImpl(testSettings.getC8yHost(), credentials);
    }
}
