package c8y.trackeragent_it.service;

import static com.cumulocity.model.authentication.CumulocityCredentials.Builder.cumulocityCredentials;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.rest.representation.devicebootstrap.NewDeviceRequestRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.InventoryFilter;

import c8y.trackeragent.utils.message.TrackerMessage;
import c8y.trackeragent_it.TestSettings;

public class Bootstraper {

    private static Logger logger = LoggerFactory.getLogger(Bootstraper.class);

    private final Platform bootstrapPlatform;
    private final PlatformImpl trackerPlatform;
    private final TestSettings testSettings;
    private final SocketWriter socketWriter;
    private final InventoryApi inventoryApi;
    private final NewDeviceRequestService newDeviceRequestService;

    public Bootstraper(TestSettings testSettings, SocketWriter socketWriter, NewDeviceRequestService newDeviceRequestService) {
        this.testSettings = testSettings;
        this.newDeviceRequestService = newDeviceRequestService;
        this.bootstrapPlatform = createBootstrapPlatform();
        this.trackerPlatform = createTrackerPlatform();
        this.socketWriter = socketWriter;
        this.inventoryApi = this.trackerPlatform.getInventoryApi();
    }

    public void bootstrapDevice(String imei, TrackerMessage deviceMessage) throws Exception {
        logger.info("Bootstrap: {}", imei);
        if (isAgentBootstraped()) {
            logger.info("Agent boostraped");
        } else {
            logger.info("Agent not boostraped");
            bootstrapAgent(deviceMessage);
        }
        newDeviceRequestService.create(imei);
        Thread.sleep(1000);
        // WAITING_FOR_CONNECTION status

        socketWriter.writeInNewConnection(deviceMessage);
        Thread.sleep(1000);
        // PENDING_ACCEPTANCE status

        logger.info("accept request for imei: {}", imei);
        newDeviceRequestService.accept(imei);
        // ACCEPTED status

        socketWriter.writeInNewConnection(deviceMessage);
        Thread.sleep(1000);
        // Device credentials got
    }

    public synchronized void bootstrapAgent(TrackerMessage deviceMessage)
            throws UnsupportedEncodingException, Exception, InterruptedException {
        logger.info("Boostrap agent");
        String id = bootstrapAgentRequestId();
        logger.info("Request id: {}", id);

        NewDeviceRequestRepresentation newDeviceRequest = new NewDeviceRequestRepresentation();
        newDeviceRequest.setId(id);
        try {
            bootstrapPlatform.getDeviceCredentialsApi().delete(newDeviceRequest);
        } catch (Exception ex) {

        }
        // agent request deleted

        newDeviceRequestService.create(id);
        Thread.sleep(1000);
        // WAITING_FOR_CONNECTION

        connectNewDeviceRequest(id);
        Thread.sleep(1000);
        // PENDING_ACCEPTANCE

        newDeviceRequestService.accept(id);
        // ACCEPTED status
    }

    private boolean isAgentBootstraped() {
        InventoryFilter filter = new InventoryFilter().byType("c8y_TrackerAgent");
        return inventoryApi.getManagedObjectsByFilter(filter).get().getManagedObjects().size() > 0;
    }

    public synchronized void deleteExistingAgentRequest() {
        try {
            newDeviceRequestService.deleteSilent(bootstrapAgentRequestId());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String bootstrapAgentRequestId() {
        return "tracker-agent-" + testSettings.getC8yTenant();
    }

    protected void connectNewDeviceRequest(String deviceId) throws Exception {
        try {
            bootstrapPlatform.getDeviceCredentialsApi().pollCredentials(deviceId);
            logger.info("Device with id {} connected.", deviceId);
        } catch (Exception ex) {
            logger.info("Device with id {} not connected.", deviceId);
        }
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
