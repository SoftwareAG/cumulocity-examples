/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent_it.service;

import c8y.trackeragent.utils.message.TrackerMessage;
import c8y.trackeragent_it.TestSettings;
import com.cumulocity.model.authentication.CumulocityBasicCredentials;
import com.cumulocity.rest.representation.devicebootstrap.NewDeviceRequestRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.InventoryFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;


public class Bootstraper {

    private static Logger logger = LoggerFactory.getLogger(Bootstraper.class);

    private final Platform bootstrapPlatform;
    private final PlatformImpl trackerPlatform;
    private final TestSettings testSettings;
    private final SocketWritter socketWriter;
    private final InventoryApi inventoryApi;
    private final DeviceCredentialsApi deviceCredentialsApi;
    private final NewDeviceRequestService newDeviceRequestService;

    public Bootstraper(TestSettings testSettings, SocketWritter socketWriter, NewDeviceRequestService newDeviceRequestService) {
        this.testSettings = testSettings;
        this.newDeviceRequestService = newDeviceRequestService;
        this.bootstrapPlatform = createPlatform(testSettings.getBootstrapUser(), testSettings.getBootstrapPassword());
        this.trackerPlatform = createPlatform(testSettings.getC8yUser(), testSettings.getC8yPassword());
        this.socketWriter = socketWriter;
        this.inventoryApi = this.trackerPlatform.getInventoryApi();
        this.deviceCredentialsApi = this.trackerPlatform.getDeviceCredentialsApi();
    }

    public void bootstrapDevice(String imei, TrackerMessage deviceMessage) throws Exception {
        bootstrapDevice(imei, deviceMessage, true);
    }
    public void bootstrapDeviceNotAgentAware(String imei, TrackerMessage deviceMessage) throws Exception {
        bootstrapDevice(imei, deviceMessage, false);
    }
    private void bootstrapDevice(String imei, TrackerMessage deviceMessage, boolean assureAgent) throws Exception {
        logger.info("Will bootstrap: {} with message {}", imei, deviceMessage);

        newDeviceRequestService.create(imei);
        Thread.sleep(1000);
        // WAITING_FOR_CONNECTION status

        socketWriter.write(deviceMessage);
        Thread.sleep(2000);
        // PENDING_ACCEPTANCE status

        logger.info("accept request for imei: {}", imei);
        newDeviceRequestService.accept(imei);
        // ACCEPTED status

        socketWriter.write(deviceMessage);
        Thread.sleep(1000);
        // Device credentials got

        socketWriter.closeExistingConnection();
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

    private PlatformImpl createPlatform(String userName, String password) {
        CumulocityBasicCredentials credentials = CumulocityBasicCredentials.builder()
                .tenantId(testSettings.getC8yTenant())
                .username(userName)
                .password(password)
                .build();
        return new PlatformImpl(testSettings.getC8yHost(), credentials);
    }
}
