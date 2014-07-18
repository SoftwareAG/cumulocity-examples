package com.cumulocity.tekelec.server.main;

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.IsDevice;

import com.cumulocity.model.ID;
import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.rest.representation.identity.ExternalIDRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.polling.PollingStrategy;
import com.cumulocity.tekelec.Imei;

public class DeviceService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceService.class);

    private final Platform bootstrapPlatform;

    private Platform devicePlatform;

    private CredentialsManager credentialsManager;

    public DeviceService() {
        credentialsManager = CredentialsManager.defaultCredentialsManager();
        CumulocityCredentials deviceCredentials = credentialsManager.getDeviceCredentials();
        bootstrapPlatform = new PlatformImpl(credentialsManager.getHost(), credentialsManager.getBootstrapCredentials());
        if (deviceCredentials != null) {
            devicePlatform = new PlatformImpl(credentialsManager.getHost(), new CumulocityCredentials(deviceCredentials.getTenantId(),
                    deviceCredentials.getUsername(), deviceCredentials.getPassword(), null));
        }
    }
    
    public ManagedObjectRepresentation register(String imei) {
        ManagedObjectRepresentation result = null;
        if (credentialsManager.getDeviceCredentials() == null) {
            logger.info("Device not registered, starting bootstrap for imei: " + imei);
            initCredentials(imei);
            result = registerDeviceManagedObject(new Imei(imei));
        } else {
            logger.info("Found device credentials");
            result = findByExternalId(new Imei(imei));
            logger.info("Found device in cumulocity");
        }
        return result;
    }

    private void initCredentials(String imei) {
        final DeviceCredentialsRepresentation credentials = bootstrapPlatform.getDeviceCredentialsApi().pollCredentials(imei,
                new PollingStrategy(300L, SECONDS, asList(10l)));
        CumulocityCredentials cumulocityCredentials = new CumulocityCredentials(credentials.getTenantId(), credentials.getUsername(),
                credentials.getPassword(), null);
        credentialsManager.saveDeviceCredentials(cumulocityCredentials);
        devicePlatform = new PlatformImpl(credentialsManager.getHost(), new CumulocityCredentials(credentials.getTenantId(),
                credentials.getUsername(), credentials.getPassword(), null));
    }

    public ManagedObjectRepresentation registerDeviceManagedObject(final Imei imei) {
        final ManagedObjectRepresentation managedObject = createManagedObject();
        registerExternalId(imei, managedObject.getId());
        return managedObject;
    }

    private void registerExternalId(final Imei imei, final GId gid) {
        ExternalIDRepresentation externalIDRepresentation = new ExternalIDRepresentation();
        externalIDRepresentation.setExternalId(imei.getValue());
        externalIDRepresentation.setType(imei.getType());
        externalIDRepresentation.setManagedObject(asManagedObject(gid));
        devicePlatform.getIdentityApi().create(externalIDRepresentation);
    }

    public static ManagedObjectRepresentation asManagedObject(GId id) {
        final ManagedObjectRepresentation managedObjectRepresentation = new ManagedObjectRepresentation();
        managedObjectRepresentation.setId(id);
        return managedObjectRepresentation;
    }

    private ManagedObjectRepresentation createManagedObject() {
        ManagedObjectRepresentation managedObjectRepresentation = new ManagedObjectRepresentation();

        managedObjectRepresentation.setName("Tekelec device");
        managedObjectRepresentation.set(new IsDevice());

        final ManagedObjectRepresentation managedObject = devicePlatform.getInventoryApi().create(managedObjectRepresentation);
        return managedObject;
    }

    public ManagedObjectRepresentation findByExternalId(ID externalID) {
        return findById(find(externalID));
    }

    public ManagedObjectRepresentation findById(GId id) {
        return devicePlatform.getInventoryApi().get(id);
    }

    public GId find(ID id) {
        return devicePlatform.getIdentityApi().getExternalId(id).getManagedObject().getId();
    }

    public void update(ManagedObjectRepresentation device) {
        devicePlatform.getInventoryApi().update(device);
    }
}
